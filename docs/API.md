common-films  
=
`GET /films/common?userId={userId}&friendId={friendId}` - Возвращает список фильмов, отсортированных по популярности.  
Параметры  
`userId` — идентификатор пользователя, запрашивающего информацию;  
`friendId` — идентификатор пользователя, с которым необходимо сравнить список фильмов.

add-remove-users-films
=
`DELETE /users/{userId}` - Удаляет пользователя по идентификатору.

`DELETE /films/{filmId}` - Удаляет фильм по идентификатору.

search
=
`GET /fimls/search` - Возвращает список фильмов, отсортированных по популярности.

Параметры строки запроса  
`query` — текст для поиска  
`by` — может принимать значения director (поиск по режиссёру), title (поиск по названию), либо оба значения через запятую при поиске одновременно и по режиссеру и по названию.

Пример:  
`GET /films/search?query=крад&by=director,title`

recommendation
=
`GET /users/{id}/recommendations` - Возвращает рекомендации по фильмам для просмотра.

reviews
=
`POST /reviews` - Добавление нового отзыва.

`PUT /reviews` - Редактирование уже имеющегося отзыва.

`DELETE /reviews/{id}` - Удаление уже имеющегося отзыва.

`GET /reviews/{id}` - Получение отзыва по идентификатору.

`GET /reviews?filmId={filmId}&count={count}` - Получение всех отзывов по идентификатору фильма, если фильм не указан то все. Если кол-во не указано то 10.

`PUT /reviews/{id}/like/{userId}` — пользователь ставит лайк отзыву.
`PUT /reviews/{id}/dislike/{userId}` — пользователь ставит дизлайк отзыву.
`DELETE /reviews/{id}/like/{userId}` — пользователь удаляет лайк/дизлайк отзыву.
`DELETE /reviews/{id}/dislike/{userId}` — пользователь удаляет дизлайк отзыву.
Описание JSON-объекта с которым работают эндпоинты
```
{
"reviewId": 123,
"content": "This film is sooo baad.",
"isPositive": false,
"userId": 123, // Пользователь
"filmId": 2, // Фильм
"useful": 20 // рейтинг полезности
}
```
most-popular  
=
`GET /films/popular?count={limit}&genreId={genreId}&year={year}` - Возвращает список самых популярных фильмов указанного жанра за нужный год.

feed
=
`GET /users/{id}/feed` - Возвращает ленту событий пользователя.

director
=
`GET /films/director/{directorId}?sortBy=[year,likes]` - Возвращает список фильмов режиссера отсортированных по количеству лайков или году выпуска.

`POST /films`

```
{  
"name": "New film",
"releaseDate": "1999-04-30",  
"description": "New film about friends",  
"duration": 120,  
"mpa": { "id": 3},  
"genres": [{ "id": 1}],  
"director": [{ "id": 1}]
}
```

`GET /directors` - Список всех режиссёров

`GET /directors/{id}` - Получение режиссёра по id

`POST /directors` - Создание режиссёра

`PUT /directors` - Изменение режиссёра
```
{
"id": 1,
"name": "New director"
}
```
`DELETE /directors/{id}` - Удаление режиссёра

    
