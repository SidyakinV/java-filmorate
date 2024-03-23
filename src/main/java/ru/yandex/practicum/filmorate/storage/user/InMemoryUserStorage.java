package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;

@Component("memUserStorage")
public class InMemoryUserStorage implements UserStorage {

    private Long lastId;
    final Map<Long, User> usersList;

    public InMemoryUserStorage() {
        lastId = 0L;
        usersList = new HashMap<>();
    }

    @Override
    public List<User> getUsersList() {
        return new ArrayList<>(usersList.values());
    }

    @Override
    public User addUser(User user) {
        user.setId(++lastId);
        usersList.put(user.getId(), user);
        return user;
    }

    @Override
    public User updateUser(User user) throws NotFoundException {
        if (!usersList.containsKey(user.getId())) {
            throw new NotFoundException(String.format("Пользователь с указанным ID (%d) не найден", user.getId()));
        }

        usersList.put(user.getId(), user);
        return user;
    }

    @Override
    public User getUser(Long id) {
        return usersList.get(id);
    }

    @Override
    public void addFriend(Long userId, Long friendId) {
        User user = getUser(userId);
        User friend = getUser(friendId);
        user.getFriends().add(friendId);
        friend.getFriends().add(userId);
    }

    @Override
    public void deleteFriend(Long userId, Long friendId) {
        User user = getUser(userId);
        User friend = getUser(friendId);
        user.getFriends().remove(friendId);
        friend.getFriends().remove(userId);
    }

    @Override
    public List<User> getFriends(Long userId) {
        User user = getUser(userId);
        List<User> users = new ArrayList<>();
        for (Long id : user.getFriends()) {
            users.add(getUser(id));
        }
        return users;
    }

    @Override
    public List<User> getCommonFriends(Long userId, Long otherId) {
        Set<Long> friends1 = getUser(userId).getFriends();
        Set<Long> friends2 = getUser(otherId).getFriends();
        List<User> list = new ArrayList<>();
        for (Long id : friends1) {
            if (friends2.contains(id)) {
                list.add(getUser(id));
            }
        }
        return list;
    }

}
