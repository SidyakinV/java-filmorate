INSERT INTO genre (id, name)
SELECT id, name FROM (
  SELECT 1 as id, 'Комедия' AS name UNION
  SELECT 2 as id, 'Драма' AS name UNION
  SELECT 3 as id, 'Мультфильм' AS name UNION
  SELECT 4 as id, 'Триллер' AS name UNION
  SELECT 5 as id, 'Документальный' AS name UNION
  SELECT 6 as id, 'Боевик' AS name
) AS Q
WHERE NOT EXISTS (SELECT * FROM genre);

INSERT INTO rating (id, name)
SELECT id, name FROM (
  SELECT 1 as id, 'G' AS name UNION
  SELECT 2 as id, 'PG' AS name UNION
  SELECT 3 as id, 'PG-13' AS name UNION
  SELECT 4 as id, 'R' AS name UNION
  SELECT 5 as id, 'NC-17' AS name
) AS Q
WHERE NOT EXISTS (SELECT * FROM rating);
