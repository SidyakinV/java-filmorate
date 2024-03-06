# java-filmorate

## Структура БД

Схема структуры БД представлена на рисунке

![Схема БД](StructDB.png)

### Список таблиц

* **film** - список фильмов
* **genre** - список жанров фильмов
* **film_genre** - таблица для связи фильмов с жанрами (у одного фильма может быть несколько жанров) 
* **rating** - список возрастных ограничений (рейтинг MPA)
* **user** - список пользователей
* **user_film** - список пользователей, поставивших лайк фильму
* **friends** - список друзей пользователя

### Примеры запросов

**1. Получить 10 самых популярных фильмов:**

```
SELECT 
  film.id, film.name, 
  (SELECT count(*) FROM user_film WHERE film.id = user_film.film_id) AS count_likes
FROM film 
ORDER BY count_likes DESC, film.name 
LIMIT 10
```

**2. Список друзей пользователя**

```
SELECT friends.friend_id, user.name
FROM friends
  JOIN user ON friends.friend_id = user.id
WHERE friends.user_id = 1
  AND friends.is_confirmed <> 0
ORDER BY user.name
```

**3. Список общих друзей**

```
SELECT user.id, user.name
FROM (
    SELECT friend_id
    FROM friends
    WHERE user_id = 1
      AND is_confirmed = 1 
    ) AS user1
  JOIN (
    SELECT friend_id
    FROM friends
    WHERE user_id = 2
      AND is_confirmed = 1 
    ) AS user2
    ON user1.friend_id = user2.friend_id
  JOIN
    user on user.id = user1.friend_id
ORDER BY user.name
```






