CREATE TABLE IF NOT EXISTS rating (
  id int NOT NULL auto_increment PRIMARY KEY,
  name varchar(20) NOT NULL
);

CREATE TABLE IF NOT EXISTS film (
  id int NOT NULL auto_increment PRIMARY KEY,
  name varchar(100) NOT NULL,
  description varchar(200),
  release_date date NOT NULL,
  duration int NOT NULL,
  rating_id int,
  FOREIGN KEY (rating_id) REFERENCES rating(id)
);

CREATE TABLE IF NOT EXISTS genre (
  id int NOT NULL auto_increment PRIMARY KEY,
  name varchar(20) NOT NULL
);

CREATE TABLE IF NOT EXISTS film_genre (
  film_id int NOT NULL,
  genre_id int NOT NULL,
  PRIMARY KEY (film_id, genre_id),
  FOREIGN KEY (film_id) REFERENCES film(id),
  FOREIGN KEY (genre_id) REFERENCES genre(id)
);

CREATE TABLE IF NOT EXISTS `user` (
  id int NOT NULL auto_increment PRIMARY KEY,
  login varchar(50) NOT NULL,
  name varchar(100),
  email varchar(100),
  birthday date
);

CREATE TABLE IF NOT EXISTS user_film (
  film_id int NOT NULL,
  user_id int NOT NULL,
  PRIMARY KEY (film_id, user_id),
  FOREIGN KEY (film_id) REFERENCES film(id),
  FOREIGN KEY (user_id) REFERENCES `user`(id)
);

CREATE TABLE IF NOT EXISTS friends (
  user_id int NOT NULL,
  friend_id int NOT NULL,
  is_confirmed boolean,
  PRIMARY KEY (user_id, friend_id),
  FOREIGN KEY (user_id) REFERENCES `user` (id),
  FOREIGN KEY (friend_id) REFERENCES `user` (id)
);
