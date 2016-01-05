package com.chat.model.msg;

import java.awt.Color;
import java.io.File;

import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

import com.chat.main.ChatClient;
import com.chat.main.Server;
import com.chat.view.ChatFrame;

public class RefuseFileMsg extends Msg {

	private File file;

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
		} else if (client.getUser().equals(targetUser)) {
			for (ChatFrame c : client.getChatFrameList()) {
				if (c.getTargetUser().equals(user)) {
					cf = c;
					break;
				}
			}
		}
		if (cf.getFile().equals(file)) {
			SimpleAttributeSet sas = new SimpleAttributeSet();
			StyleConstants.setForeground(sas, Color.BLUE);
			try {
				cf.getTxt_display().getStyledDocument().insertString(
						cf.getTxt_display().getStyledDocument().getLength(),
						"拒绝文件 " + file.getName() + " 大小:" + file.length()
								/ 1024 + "k\n", sas);
				cf.getTxt_display().setCaretPosition(
						cf.getTxt_display().getStyledDocument()
								.getLength());
			} catch (BadLocationException e) {
				e.printStackTrace();
			}
			cf.getBtn_acceptFile().setVisible(false);
			cf.getBtn_refuseFile().setVisible(false);
			cf.setFile(null);
		}
	}

	@Override
	public void process(Server server) {

	}

	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file;
	}

}
