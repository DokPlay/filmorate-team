add-director
=
*Commits on Nov 12, 2025*  
*Сделал шаблоны: Контроллер, Репозиторий, Класс Режиссер*  

*Commits on Nov 13, 2025*  
*Первые наброски*  

*Commits on Nov 15, 2025*  
*Доработки с учетом гайда*  

Что сделано:
* первые наброски - Добавление режиссеров в фильмы.
* src/main/java/ru/yourteam/filmorate/controller/DirectorFilmsController.java
* src/main/java/ru/yourteam/filmorate/repository/DirectorRepository.java
* src/main/java/ru/yourteam/filmorate/dto/DirectorDto.java
* src/main/java/ru/yourteam/filmorate/model/Director.java

add-common-films
=
*Commits on Nov 16, 2025*  
*первые наброски*

Что сделано:
* src/main/java/ru/yourteam/filmorate/controller/CommonFilmsController.java
* src/main/java/ru/yourteam/filmorate/service/CommonFilmService.java
* src/main/java/ru/yourteam/filmorate/repository/CommonFilmRepository.java  
  
Дополнительно сделал:
* src/main/java/ru/yourteam/filmorate/dto/CommonFilmDto
* src/main/java/ru/yourteam/filmorate/dto/CommonFilmRowMapper
  
add-reviews
=
*Commits on Nov 18, 2025*  
*Реализовал функционал отзывов.*  

**Что сделано:**  
* Реализовал функционал отзывов.
* Все эндпоинты проверил через постман.

**Как проверять**  
В init_data только жанры мпа и пара фильмов, для ручной проверки через постман надо будет добавлять вручную отзывы и лайки к ним. В целом, могу добавить в init_data.

**Дополнительно**  
В файле миграции V2__create_tables.sql, БД из 12 спринта, наверное ее можно оставить.

add-recommendation
=  
*Commits on Nov 18, 2025*  
*feat: add film recommendations and tests*  
  
Изменения по файлам:

* RecommendationController.java  
Реализован эндпоинт GET /users/{id}/recommendations, который возвращает список RecommendationDto для пользователя.

* RecommendationDto.java  
DTO для рекомендованных фильмов: id, название, описание, дата выхода, длительность, mpaId и relevanceScore (вес рекомендации).

* RecommendationService.java  
Реализован алгоритм рекомендаций на основе пересечения лайков:
поиск наиболее похожих пользователей, выбор фильмов, которые лайкнули они, но ещё не лайкнул целевой пользователь, расчёт веса и сортировка.

* application-test.yml  
Настроен профиль test: H2 in-memory БД + Flyway для прогонки миграций в интеграционных тестах.

* V1__init.sql  
Актуализирована схема таблицы likes (PRIMARY KEY (film_id, user_id), внешние ключи на users и films), чтобы сервис рекомендаций и тесты могли корректно работать.

* RecommendationMapper.java  
Новый маппер Film -> RecommendationDto с заполнением relevanceScore.

* RecommendationRepository.java  
Новый репозиторий для рекомендаций: проверка существования пользователя, чтение всех лайков (user_id/film_id) и загрузка фильмов по id.

* AbstractIntegrationTest.java  
Базовый класс для интеграционных тестов: @SpringBootTest, @activeprofiles("test"), очистка тестовых данных и хелперы insertTestUser/Film/Like.

* RecommendationServiceIntegrationTest.java  
Интеграционный тест RecommendationService на H2: проверка корректных рекомендаций, кейса без лайков и NotFound для несуществующего пользователя.

* RecommendationControllerIntegrationTest.java  
Интеграционный тест REST-эндпоинта /users/{id}/recommendations через MockMvc: проверка JSON-ответа, пустого списка и 404.

* RecommendationServiceUnitTest.java  
Юнит-тесты для алгоритма RecommendationService на моках RecommendationRepository, без доступа к БД.

add-most-popular
=
*Commits on Nov 18, 2025*  
*Implement most popular films endpoint*  
    
* Реализован REST-эндпоинт GET /films/popular для получения топ-N самых популярных фильмов по количеству лайков.
* Добавлен DTO PopularFilmDto для отдачи фильмов во внешнее API (id, название, описание, дата релиза, длительность, mpaId, количество лайков).
* Доработан сервисный слой:
  * расширен интерфейс PopularFilmService;
  * добавлена реализация PopularFilmServiceImpl с нормализацией параметра count (DEFAULT=10, MAX=100) и валидацией фильтров genreId/year.
* Добавлен репозиторий PopularFilmRepository и маппер PopularFilmRowMapper:
  * выборка и агрегация лайков происходят на уровне БД через JdbcTemplate;
  * поддерживаются варианты: без фильтров, только по жанру, только по году, жанр + год;
  * сортировка: по количеству лайков (DESC), затем по film_id (ASC) для детерминированного порядка.
* В контроллере PopularFilmsController:
  * настроен маршрут /films/popular;
  * добавлена валидация параметров запросов через @Positive и @Min(1895);
  * добавлено логирование вызовов и размера ответа.
* Добавлены тесты:
  * PopularFilmServiceUnitTest — юнит-тесты логики сервиса (нормализация count, валидация genreId/year, отсутствие лишних вызовов репозитория при ошибках);
  * PopularFilmRepositoryIntegrationTest — интеграционные тесты репозитория на реальной схеме БД (4 сценария фильтрации);
  * PopularFilmsControllerIntegrationTest — интеграционные тесты контроллера через MockMvc (успешные кейсы и 400 при некорректных параметрах).
