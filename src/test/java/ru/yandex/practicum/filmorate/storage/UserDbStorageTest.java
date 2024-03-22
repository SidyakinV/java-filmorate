package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@JdbcTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserDbStorageTest {

	private final JdbcTemplate jdbcTemplate;

	@Test
	public void addUserTest() {
		User newUser = createDefaultUser();
		UserDbStorage userStorage = new UserDbStorage(jdbcTemplate);
		User user = userStorage.addUser(newUser);
		newUser.setId(user.getId());

		User savedUser = userStorage.getUser(user.getId());

		compare(newUser, savedUser);
	}

	@Test
	public void updateUserTest() throws NotFoundException {
		User newUser = createDefaultUser();
		UserDbStorage userStorage = new UserDbStorage(jdbcTemplate);
		User user = userStorage.addUser(newUser);

		user.setLogin("NewLogin");
		user.setLogin("Петя Иванов");
		user.setEmail("ivanov@mail.ru");
		user.setBirthday(LocalDate.of(1991, 1, 1));
		userStorage.updateUser(user);

		User savedUser = userStorage.getUser(user.getId());

		compare(user, savedUser);
	}

	@Test
	public void addFriendTest() {
		UserDbStorage userStorage = new UserDbStorage(jdbcTemplate);
		User user1 = userStorage.addUser(createDefaultUser());
		User user2 = userStorage.addUser(createDefaultUser());

		assertEquals(0, userStorage.getFriends(user1.getId()).size());
		assertEquals(0, userStorage.getFriends(user2.getId()).size());

		userStorage.addFriend(user1.getId(), user2.getId());
		List<User> friends = userStorage.getFriends(user1.getId());

		assertEquals(1, friends.size());
		assertEquals(0, userStorage.getFriends(user2.getId()).size());
		assertEquals(user2.getId(), friends.get(0).getId());
	}

	@Test
	public void removeFriendTest() {
		UserDbStorage userStorage = new UserDbStorage(jdbcTemplate);
		User user1 = userStorage.addUser(createDefaultUser());
		User user2 = userStorage.addUser(createDefaultUser());

		userStorage.addFriend(user2.getId(), user1.getId());

		assertEquals(0, userStorage.getFriends(user1.getId()).size());
		assertEquals(1, userStorage.getFriends(user2.getId()).size());

		userStorage.deleteFriend(user2.getId(), user1.getId());

		assertEquals(0, userStorage.getFriends(user1.getId()).size());
		assertEquals(0, userStorage.getFriends(user2.getId()).size());
	}

	@Test
	public void commonFriendsTest() {
		UserDbStorage userStorage = new UserDbStorage(jdbcTemplate);
		User user1 = userStorage.addUser(createDefaultUser());
		User user2 = userStorage.addUser(createDefaultUser());
		User user3 = userStorage.addUser(createDefaultUser());

		userStorage.addFriend(user1.getId(), user2.getId());
		userStorage.addFriend(user1.getId(), user3.getId());

		userStorage.addFriend(user2.getId(), user1.getId());
		userStorage.addFriend(user2.getId(), user3.getId());

		List<User> friends1 = userStorage.getFriends(user1.getId());
		Set<Long> list1 = friends1.stream().map(User::getId).collect(Collectors.toCollection(HashSet::new));

		List<User> friends2 = userStorage.getFriends(user2.getId());
		Set<Long> list2 = friends2.stream().map(User::getId).collect(Collectors.toCollection(HashSet::new));

		assertEquals(2, list1.size());
		assertEquals(2, list2.size());
		assertTrue(list1.contains(user3.getId()));
		assertTrue(list2.contains(user3.getId()));
	}

	public static User createDefaultUser() {
		User user = new User();
		user.setId(0L);
		user.setLogin("login");
		user.setLogin("Вася Пупкин");
		user.setEmail("pupkin@email.com");
		user.setBirthday(LocalDate.of(1990, 12, 31));
		return user;
	}

	private void compare(User user1, User user2) {
		assertNotNull(user1);
		assertNotNull(user2);
		assertEquals(user1.getLogin(), user2.getLogin());
		assertEquals(user1.getName(), user2.getName());
		assertEquals(user1.getEmail(), user2.getEmail());
		assertEquals(user1.getBirthday(), user2.getBirthday());
	}

}

