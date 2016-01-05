package com.chat.model.msg;

import javax.swing.JOptionPane;

import com.chat.main.ChatClient;
import com.chat.main.Server;

public class RemoveFriendMsg extends Msg {

	@Override
	public void process(ChatClient client) {
		if (client.getUser().equals(user)) {
			client.getUser().getFirends().remove(targetUser);
			client.getUserListFrame().getUserListPanel().setUser(targetUser);
			JOptionPane.showMessageDialog(null, "用户  " + targetUser.getName()
					+ " 已与您解除好友关系");
		} else {
			client.getUser().getFirends().remove(user);
			client.getUserListFrame().getUserListPanel().setUser(user);
			JOptionPane.showMessageDialog(null, "用户  " + user.getName()
					+ " 已不再是您的好友");
		}
		client.getUserListFrame().getUserListPanel().rebuild();
	}

	@Override
	public void process(Server server) {
		server.getUserManager().removeFriend(user, targetUser);
	}
}
