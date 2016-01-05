package com.chat.model.msg;

import java.io.Serializable;

import com.chat.main.ChatClient;
import com.chat.main.Server;
import com.chat.model.User;

public abstract class Msg implements Serializable {

	public static final int LOGIN_MSG = 1;
	public static final int LOGIN_ERROR_MSG = 2;
	public static final int LOGOUT_MSG = 0;

	protected int type;
	protected User user, targetUser;
	protected String ip;
	protected int port;

	public Msg() {

	}

	public abstract void process(ChatClient client);

	public abstract void process(Server server);

	public void send() {
		ChatClient.getInstance().getNetClient().send(this);
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public User getTargetUser() {
		return targetUser;
	}

	public void setTargetUser(User targetUser) {
		this.targetUser = targetUser;
	}
}
