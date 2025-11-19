// Сервисный слой для генерации рекомендаций фильмов.
package ru.yourteam.filmorate.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yourteam.filmorate.dal.repositories.RecommendationRepository;//pr от Дмитрия
import ru.yourteam.filmorate.dal.repositories.RecommendationRepository.UserLikeRow;//pr от Дмитрия
import ru.yourteam.filmorate.dto.RecommendationDto;
import ru.yourteam.filmorate.exceptions.NotFoundException;//Здесь использован NotFoundException из ветки add-reviews 
//(ru.yourteam.filmorate.exceptions.NotFoundException).
//потом перед финалом если  актуальной ветке он лежит в другом пакете — поправлю импорт.
import ru.yourteam.filmorate.mappers.recommendationMappers.RecommendationMapper;
import ru.yourteam.filmorate.model.Film;

// Сервисный слой для генерации рекомендаций фильмов.
@Service
@RequiredArgsConstructor
@Slf4j
public class RecommendationService {

    private static final int DEFAULT_LIMIT = 10;

    private final RecommendationRepository recommendationRepository;

    /**
     * Возвращает рекомендации для пользователя по id.
     * Алгоритм:
     *  - ищем пользователей с максимальным пересечением лайков;
     *  - выбираем фильмы, которые они лайкнули, а целевой пользователь — нет;
     *  - сортируем по "весу" рекомендации.
     */
    public List<RecommendationDto> getRecommendationsForUser(int userId) {
        if (!recommendationRepository.userExists(userId)) {
            throw new NotFoundException("Пользователь с id=" + userId + " не найден.");
        }

        List<UserLikeRow> allLikes = recommendationRepository.findAllLikes();
        if (allLikes.isEmpty()) {
            log.info("В таблице лайков пока нет записей — рекомендации для пользователя {} отсутствуют", userId);
            return Collections.emptyList();
        }

        Map<Integer, Set<Integer>> userToFilms = buildUserToFilmsMap(allLikes);
        Set<Integer> filmsOfUser = userToFilms.get(userId);

        if (filmsOfUser == null || filmsOfUser.isEmpty()) {
            log.info("У пользователя {} ещё нет лайков — рекомендации отсутствуют", userId);
            return Collections.emptyList();
        }

        SimilarUsersResult similarUsersResult = findMostSimilarUsers(userId, filmsOfUser, userToFilms);

        if (similarUsersResult.getMaxCommonCount() == 0
            || similarUsersResult.getSimilarUserIds().isEmpty()) {
            log.info("Для пользователя {} не найдено похожих пользователей — рекомендации отсутствуют", userId);
            return Collections.emptyList();
        }

        Map<Integer, Integer> scoreByFilmId =
            buildRecommendationScores(filmsOfUser, userToFilms, similarUsersResult.getSimilarUserIds());

        if (scoreByFilmId.isEmpty()) {
            log.info(
                "У похожих пользователей нет фильмов, которые пользователь {} ещё не лайкнул",
                userId
            );
            return Collections.emptyList();
        }

        List<Integer> sortedFilmIds = sortFilmIdsByScore(scoreByFilmId);

        List<Integer> limitedFilmIds;
        if (sortedFilmIds.size() > DEFAULT_LIMIT) {
            limitedFilmIds = sortedFilmIds.subList(0, DEFAULT_LIMIT);
        } else {
            limitedFilmIds = sortedFilmIds;
        }

        List<Film> films = recommendationRepository.findFilmsByIds(limitedFilmIds);
        Map<Integer, Film> filmById = mapFilmsById(films);

        List<RecommendationDto> result = new ArrayList<>(limitedFilmIds.size());
        for (Integer filmId : limitedFilmIds) {
            Film film = filmById.get(filmId);
            if (film != null) {
                int score = scoreByFilmId.getOrDefault(filmId, 0);
                result.add(RecommendationMapper.mapToRecommendationDto(film, score));
            }
        }

        log.info(
            "Сформировано {} рекомендаций для пользователя {} (макс. пересечение лайков: {})",
            result.size(),
            userId,
            similarUsersResult.getMaxCommonCount()
        );
        return result;
    }

    private Map<Integer, Set<Integer>> buildUserToFilmsMap(List<UserLikeRow> allLikes) {
        Map<Integer, Set<Integer>> userToFilms = new HashMap<>();
        for (UserLikeRow like : allLikes) {
            int userId = like.getUserId();
            int filmId = like.getFilmId();
            Set<Integer> films = userToFilms.computeIfAbsent(userId, id -> new HashSet<>());
            films.add(filmId);
        }
        return userToFilms;
    }

    private SimilarUsersResult findMostSimilarUsers(
        int targetUserId,
        Set<Integer> targetFilms,
        Map<Integer, Set<Integer>> userToFilms
    ) {
        int maxCommon = 0;
        Set<Integer> similarUserIds = new HashSet<>();

        for (Map.Entry<Integer, Set<Integer>> entry : userToFilms.entrySet()) {
            int otherUserId = entry.getKey();
            if (otherUserId == targetUserId) {
                continue;
            }

            Set<Integer> otherFilms = entry.getValue();
            if (otherFilms.isEmpty()) {
                continue;
            }

            int commonCount = countIntersection(targetFilms, otherFilms);
            if (commonCount == 0) {
                continue;
            }

            if (commonCount > maxCommon) {
                maxCommon = commonCount;
                similarUserIds.clear();
                similarUserIds.add(otherUserId);
            } else if (commonCount == maxCommon) {
                similarUserIds.add(otherUserId);
            }
        }

        return new SimilarUsersResult(maxCommon, similarUserIds);
    }

    private int countIntersection(Set<Integer> first, Set<Integer> second) {
        int count = 0;
        for (Integer filmId : first) {
            if (second.contains(filmId)) {
                count++;
            }
        }
        return count;
    }

    private Map<Integer, Integer> buildRecommendationScores(
        Set<Integer> filmsOfUser,
        Map<Integer, Set<Integer>> userToFilms,
        Set<Integer> similarUserIds
    ) {
        Map<Integer, Integer> scoreByFilmId = new HashMap<>();
        for (Integer similarUserId : similarUserIds) {
            Set<Integer> filmsOfSimilarUser = userToFilms.get(similarUserId);
            if (filmsOfSimilarUser == null || filmsOfSimilarUser.isEmpty()) {
                continue;
            }

            for (Integer filmId : filmsOfSimilarUser) {
                if (filmsOfUser.contains(filmId)) {
                    continue;
                }
                int currentScore = scoreByFilmId.getOrDefault(filmId, 0);
                scoreByFilmId.put(filmId, currentScore + 1);
            }
        }
        return scoreByFilmId;
    }

    private List<Integer> sortFilmIdsByScore(Map<Integer, Integer> scoreByFilmId) {
        List<Map.Entry<Integer, Integer>> entries = new ArrayList<>(scoreByFilmId.entrySet());
        entries.sort((left, right) -> {
            int scoreCompare = Integer.compare(right.getValue(), left.getValue());
            if (scoreCompare != 0) {
                return scoreCompare;
            }
            return Integer.compare(left.getKey(), right.getKey());
        });

        List<Integer> filmIds = new ArrayList<>(entries.size());
        for (Map.Entry<Integer, Integer> entry : entries) {
            filmIds.add(entry.getKey());
        }
        return filmIds;
    }

    private Map<Integer, Film> mapFilmsById(List<Film> films) {
        Map<Integer, Film> filmById = new HashMap<>();
        for (Film film : films) {
            filmById.put(film.getId(), film);
        }
        return filmById;
    }

    private static final class SimilarUsersResult {
        private final int maxCommonCount;
        private final Set<Integer> similarUserIds;

        private SimilarUsersResult(int maxCommonCount, Set<Integer> similarUserIds) {
            this.maxCommonCount = maxCommonCount;
            this.similarUserIds = similarUserIds;
        }

        public int getMaxCommonCount() {
            return maxCommonCount;
        }

        public Set<Integer> getSimilarUserIds() {
            return similarUserIds;
        }
    }
}
