package com.chat.main;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.jvnet.substance.skin.SubstanceOfficeBlue2007LookAndFeel;

import com.chat.model.User;
import com.chat.util.PropertiesUtil;
import com.chat.view.ChatFrame;
import com.chat.view.LoginFrame;
import com.chat.view.UserListFrame;

public class ChatClient {

	public static final String PROPERTIES_PATH = "config.properties";

	private Map<String, String> properties = PropertiesUtil
			.getPropertiesMap(PROPERTIES_PATH);

	private NetClient netClient = new NetClient(this, properties
			.get("SERVER_IP"), Integer.parseInt(properties
			.get("SERVER_TCP_PORT")));
	private User user;
	private LoginFrame loginFrame;
	private UserListFrame userListFrame;
	private List<ChatFrame> chatFrameList = new ArrayList<ChatFrame>();

	private static ChatClient instance;

	private ChatClient() {

	}

	public synchronized static ChatClient getInstance() {
		if (instance == null) {
			instance = new ChatClient();
		}
		return instance;
	}

	public static void main(String[] args) {
		JFrame.setDefaultLookAndFeelDecorated(true);
		try {
			UIManager.setLookAndFeel(new SubstanceOfficeBlue2007LookAndFeel());
		} catch (UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}
		getInstance();
		instance.loginFrame = new LoginFrame();
	}

	public Map<String, String> getProperties() {
		return properties;
	}

	public void setProperties(Map<String, String> properties) {
		this.properties = properties;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public LoginFrame getLoginFrame() {
		return loginFrame;
	}

	public void setLoginFrame(LoginFrame loginFrame) {
		this.loginFrame = loginFrame;
	}

	public NetClient getNetClient() {
		return netClient;
	}

	public void setNetClient(NetClient netClient) {
		this.netClient = netClient;
	}

	public UserListFrame getUserListFrame() {
		return userListFrame;
	}

	public void setUserListFrame(UserListFrame userListFrame) {
		this.userListFrame = userListFrame;
	}

	public List<ChatFrame> getChatFrameList() {
		return chatFrameList;
	}

	public void setChatFrameList(List<ChatFrame> chatFrameList) {
		this.chatFrameList = chatFrameList;
	}
}
