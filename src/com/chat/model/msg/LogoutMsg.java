package com.chat.model.msg;

import com.chat.main.ChatClient;
import com.chat.main.Server;
import com.chat.view.ChatFrame;

public class LogoutMsg extends Msg {

	@Override
	public void process(ChatClient client) {
		user.setLogin(false);
		if (client.getUser().getFirends().contains(user)) {
			client.getUserListFrame().getUserListPanel().setUser(user);
		} else {
			client.getUserListFrame().getUserListPanel().removeUser(user);
		}
		for (ChatFrame cf : client.getChatFrameList()) {
			if (user.equals(cf.getTargetUser())) {
				cf.getTargetUser().setLogin(false);
				cf.setTargetUser(user);
			}
		}
	}

	@Override
	public void process(Server server) {

	}

}
