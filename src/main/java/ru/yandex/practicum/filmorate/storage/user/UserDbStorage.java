package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.PreparedStatement;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Component("dbUserStorage")
@Slf4j
public class UserDbStorage implements UserStorage {

    private final JdbcTemplate jdbcTemplate;
    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public UserDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<User> getUsersList() {
        List<User> users = new ArrayList<>();
        Map<Long, Set<Long>> userFriendList = getUserFriendList(0L);

        SqlRowSet userRows = jdbcTemplate.queryForRowSet(
                "SELECT * FROM `user` ");

        while (userRows.next()) {
            users.add(userFromRowSet(userRows, userFriendList));
        }

        return users;
    }

    @Override
    public User addUser(User user) {
        String sqlQuery =
                "insert into `user` (login, name, email, birthday) " +
                "values (?, ?, ?, ?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sqlQuery, new String[]{"id"});
            stmt.setString(1, user.getLogin());
            stmt.setString(2, user.getName());
            stmt.setString(3, user.getEmail());
            stmt.setString(4, user.getBirthday().format(dateTimeFormatter));
            return stmt;
        }, keyHolder);

        Long id = Objects.requireNonNull(keyHolder.getKey()).longValue();
        return getUser(id);
    }

    @Override
    public User updateUser(User user) throws NotFoundException {
        String sqlQuery =
                "update `user` set " +
                "  login = ?, name = ?, email = ?, birthday = ? " +
                "where id = ?";
        jdbcTemplate.update(sqlQuery,
                user.getLogin(),
                user.getName(),
                user.getEmail(),
                user.getBirthday().format(dateTimeFormatter),
                user.getId());

        Long id = user.getId();
        user = getUser(id);
        if (user == null) {
            throw new NotFoundException(String.format("Пользователь с указанным ID (%d) не найден", id));
        }

        return user;
    }

    @Override
    public User getUser(Long id) {
        Map<Long, Set<Long>> userFriendList = getUserFriendList(id);

        SqlRowSet userRows = jdbcTemplate.queryForRowSet(
                "SELECT * FROM `user` WHERE id = ?", id);

        if (!userRows.next()) {
            log.info("Пользователь с идентификатором {} не найден в базе данных", id);
            return null;
        }

        return userFromRowSet(userRows, userFriendList);
    }

    @Override
    public void addFriend(Long userId, Long friendId) {
        String sqlQuery =
                "MERGE INTO friends (user_id, friend_id) " +
                "VALUES (?, ?) ";
        jdbcTemplate.update(sqlQuery,
                userId, friendId);
    }

    @Override
    public void deleteFriend(Long userId, Long friendId) {
        String sqlQuery =
                "DELETE FROM friends " +
                "WHERE user_id = ? AND friend_id = ? ";
        jdbcTemplate.update(sqlQuery,
                userId, friendId);
    }

    @Override
    public List<User> getFriends(Long userId) {
        List<User> friends = new ArrayList<>();
        Map<Long, Set<Long>> userFriendList = getUserFriendList(0L);

        SqlRowSet userRows = jdbcTemplate.queryForRowSet(
                "SELECT u.* " +
                    "FROM friends AS f " +
                    "  INNER JOIN `user` AS u ON f.friend_id = u.id " +
                    "WHERE f.user_id = ? " +
                    "ORDER BY u.name",
                userId);

        while (userRows.next()) {
            friends.add(userFromRowSet(userRows, userFriendList));
        }

        return friends;
    }

    @Override
    public List<User> getCommonFriends(Long userId, Long otherId) {
        List<User> friends = new ArrayList<>();
        Map<Long, Set<Long>> userFriendList = getUserFriendList(0L);

        SqlRowSet userRows = jdbcTemplate.queryForRowSet(
                "SELECT u.* " +
                    "FROM friends AS f1 " +
                    "  INNER JOIN friends AS f2 on f1.friend_id = f2.friend_id " +
                    "  INNER JOIN `user` AS u ON f1.friend_id = u.id " +
                    "WHERE f1.user_id = ? " +
                    "  AND f2.user_id = ? " +
                    "ORDER BY u.name",
                userId, otherId);

        while (userRows.next()) {
            friends.add(userFromRowSet(userRows, userFriendList));
        }

        return friends;
    }

    private Map<Long, Set<Long>> getUserFriendList(Long userId) {
        Map<Long, Set<Long>> friendList = new HashMap<>();

        StringBuilder cmd = new StringBuilder("SELECT * FROM friends ");
        if (userId != 0) {
            cmd.append("WHERE user_id = ").append(userId);
        }

        SqlRowSet friendsRows = jdbcTemplate.queryForRowSet(cmd.toString());
        while (friendsRows.next()) {
            userId = friendsRows.getLong("user_id");
            if (!friendList.containsKey(userId)) {
                friendList.put(userId, new HashSet<>());
            }
            friendList.get(userId).add(friendsRows.getLong("friend_id"));
        }

        return friendList;
    }


    private User userFromRowSet(SqlRowSet userRows, Map<Long, Set<Long>> userFriendList) {
        User user = new User();

        user.setId(userRows.getLong("id"));
        user.setEmail(userRows.getString("email"));
        user.setLogin(userRows.getString("login"));
        user.setName(userRows.getString("name"));
        user.setBirthday(Objects.requireNonNull(userRows.getDate("birthday")).toLocalDate());

        if (userFriendList.containsKey(user.getId())) {
            user.setFriends(userFriendList.get(user.getId()));
        }

        return user;
    }
}
