package com.chat.model.msg;

import java.awt.Color;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

import com.chat.main.ChatClient;
import com.chat.main.Server;
import com.chat.view.ChatFrame;

public class SendFileMsg extends SoundMsg {

	public static final SimpleDateFormat FORMAT = new SimpleDateFormat(
			"hh:mm:ss");

	private File file;
	private Date date;

	@Override
	public void process(ChatClient client) {
		ChatFrame cf = null;
		if (client.getUser().equals(user)) {
			for (ChatFrame c : client.getChatFrameList()) {
				if (c.getTargetUser().equals(targetUser)) {
					cf = c;
					break;
				}
			}
			SimpleAttributeSet sas = new SimpleAttributeSet();
			StyleConstants.setForeground(sas, Color.BLUE);
			try {
				cf.getTxt_display().getStyledDocument().insertString(
						cf.getTxt_display().getStyledDocument().getLength(),
						user.getName() + " " + FORMAT.format(date)
								+ " : 请求发送文件 " + file.getName() + " 大小:"
								+ file.length() / 1024 + "k\n", sas);
				cf.getTxt_display().setCaretPosition(
						cf.getTxt_display().getStyledDocument().getLength());
			} catch (BadLocationException e) {
				e.printStackTrace();
			}
		} else if (client.getUser().equals(targetUser)) {
			for (ChatFrame c : client.getChatFrameList()) {
				if (c.getTargetUser().equals(user)) {
					cf = c;
					break;
				}
			}
			this.playSound(client.getProperties().get("MSG_SOUND_PATH"));
			if (cf == null) {
				client.getUserListFrame().addUnreadMsg(this);
				return;
			}
			SimpleAttributeSet sas = new SimpleAttributeSet();
			StyleConstants.setForeground(sas, Color.BLUE);
			try {
				cf.getTxt_display().getStyledDocument().insertString(
						cf.getTxt_display().getStyledDocument().getLength(),
						user.getName() + " " + FORMAT.format(date)
								+ " : 请求向您发送文件 " + file.getName() + " 大小:"
								+ file.length() / 1024 + "k\n", sas);
				cf.getTxt_display().setCaretPosition(
						cf.getTxt_display().getStyledDocument().getLength());
			} catch (BadLocationException e) {
				e.printStackTrace();
			}
			cf.getBtn_acceptFile().setVisible(true);
			cf.getBtn_refuseFile().setVisible(true);
		}
		cf.setFile(file);
	}

	@Override
	public void process(Server server) {
		date = new Date();
	}

	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file;
	}

	@Override
	public void processWithNoSound(ChatClient client) {
		ChatFrame cf = null;
		for (ChatFrame c : client.getChatFrameList()) {
			if (c.getTargetUser().equals(user)) {
				cf = c;
				break;
			}
		}
		if (cf == null) {
			client.getUserListFrame().addUnreadMsg(this);
			return;
		}
		SimpleAttributeSet sas = new SimpleAttributeSet();
		StyleConstants.setForeground(sas, Color.BLUE);
		try {
			cf.getTxt_display().getStyledDocument().insertString(
					cf.getTxt_display().getStyledDocument().getLength(),
					user.getName() + " " + FORMAT.format(date) + " : 请求向您发送文件 "
							+ file.getName() + " 大小:" + file.length() / 1024
							+ "k\n", sas);
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
		cf.setFile(file);
		cf.getBtn_acceptFile().setVisible(true);
		cf.getBtn_refuseFile().setVisible(true);
	}

}
