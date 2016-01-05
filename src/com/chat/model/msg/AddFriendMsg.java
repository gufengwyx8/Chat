package com.chat.model.msg;

import javax.swing.JOptionPane;

import com.chat.main.ChatClient;
import com.chat.main.Server;

public class AddFriendMsg extends SoundMsg {

	private String str;

	@Override
	public void process(ChatClient client) {
		if (client.getUser().equals(user)) {
			JOptionPane.showMessageDialog(null, "发送请求成功");
		} else {
			this.playSound(client.getProperties().get("MSG_SOUND_PATH"));
			client.getUserListFrame().addUnreadMsg(this);
		}
	}

	@Override
	public void process(Server server) {

	}

	public String getStr() {
		return str;
	}

	public void setStr(String str) {
		this.str = str;
	}

	@Override
	public void processWithNoSound(ChatClient client) {
		if (JOptionPane.showConfirmDialog(null, "用户  " + user.getName()
				+ " 想跟您成为好友，验证信息：" + str + "，接受请求吗？", "",
				JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
			AcceptFriendMsg msg = new AcceptFriendMsg();
			msg.setUser(user);
			msg.setTargetUser(targetUser);
			msg.send();
		}
	}

}
