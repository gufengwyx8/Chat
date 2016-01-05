package com.chat.service;

import com.chat.dao.UserDao;
import com.chat.model.User;

public class UserManager {

	private UserDao userDao = new UserDao();

	public User login(String name, String password) {
		return userDao.login(name, password);
	}

	public boolean addFriend(User user1, User user2) {
		return userDao.addFriend(user1, user2);
	}

	public boolean removeFriend(User user1, User user2) {
		return userDao.removeFriend(user1, user2);
	}

	public boolean updateUser(User user) {
		return userDao.updateUser(user);
	}

	public boolean reg(User user) {
		return userDao.reg(user);
	}
}
