package ru.yourteam.filmorate.mapper.recommendation;

import ru.yourteam.filmorate.dto.RecommendationDto;
import ru.yourteam.filmorate.model.Film;//пакет от дмитрия смотреть PR

// Маппер из Film в RecommendationDto.
public final class RecommendationMapper {

    private RecommendationMapper() {
    }

    public static RecommendationDto mapToRecommendationDto(Film film, int relevanceScore) {
        RecommendationDto dto = new RecommendationDto();
        dto.setFilmId(film.getId());
        dto.setName(film.getName());
        dto.setDescription(film.getDescription());
        dto.setReleaseDate(film.getReleaseDate());
        dto.setDuration(film.getDuration());
        dto.setMpaId(film.getMpaId());
        dto.setRelevanceScore(relevanceScore);
        return dto;
    }
}
