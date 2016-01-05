package com.chat.model.msg;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;

import sun.audio.AudioPlayer;

import com.chat.main.ChatClient;
import com.chat.main.Server;
import com.chat.model.User;
import com.chat.view.ChatFrame;
import com.chat.view.PopupFrame;
import com.chat.view.UserListFrame;

public class LoginMsg extends Msg {

	private List<User> onlineUser = new ArrayList<User>();
	private List<ChatMsg> offlineMsg = new ArrayList<ChatMsg>();

	@Override
	public void process(ChatClient client) {
		if (this.getUser() != null) {
			if (client.getUser() == null || client.getUser().equals(user)) {
				client.setUser(user);
				if (client.getLoginFrame() != null) {
					client.getLoginFrame().dispose();
					client.setLoginFrame(null);
				}
				if (client.getUserListFrame() != null) {
					client.getUserListFrame().dispose();
				}
				client.setUserListFrame(new UserListFrame());
				for (User u : user.getFirends()) {
					if (onlineUser.contains(u)) {
						u.setLogin(true);
						client.getUserListFrame().getUserListPanel().setUser(u);
					}
				}
				for (User u : onlineUser) {
					if (!user.getFirends().contains(u)) {
						client.getUserListFrame().getUserListPanel().setUser(u);
					}
				}
				for (User u : user.getFirends()) {
					if (!onlineUser.contains(u)) {
						u.setLogin(false);
						client.getUserListFrame().getUserListPanel().setUser(u);
					}
				}
				for (ChatMsg msg : offlineMsg) {
					msg.process(client);
				}
			} else {
				try {
					AudioPlayer.player.start(new FileInputStream(client
							.getProperties().get("GLOBAL_SOUND_PATH")));
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
				new PopupFrame(user);
				client.getUserListFrame().getUserListPanel().setUser(user);
				for (ChatFrame cf : client.getChatFrameList()) {
					if (user.equals(cf.getTargetUser())) {
						cf.getTargetUser().setLogin(true);
						cf.setTargetUser(user);
					}
				}
			}
		} else {
			JOptionPane.showMessageDialog(null, "登录失败，请检查用户名和密码");
		}
	}

	@Override
	public void process(Server server) {
		for (ChatMsg msg : server.getOfflineMsg()) {
			if (msg.getTargetUser().equals(user)) {
				offlineMsg.add(msg);
			}
		}
		server.getOfflineMsg().removeAll(offlineMsg);
	}

	public List<User> getOnlineUser() {
		return onlineUser;
	}

	public void setOnlineUser(List<User> onlineUser) {
		this.onlineUser = onlineUser;
	}

	public List<ChatMsg> getOfflineMsg() {
		return offlineMsg;
	}

	public void setOfflineMsg(List<ChatMsg> offlineMsg) {
		this.offlineMsg = offlineMsg;
	}

}
