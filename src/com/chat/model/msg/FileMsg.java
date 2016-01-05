package com.chat.model.msg;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import com.chat.main.ChatClient;
import com.chat.main.Server;
import com.chat.view.ChatFrame;

public class FileMsg extends Msg {

	private File srcFile, destFile;
	private byte[] buffer = new byte[2048];
	private int length;

	@Override
	public void process(ChatClient client) {
		if (client.getUser().equals(targetUser)) {
			ChatFrame cf = null;
			for (ChatFrame c : client.getChatFrameList()) {
				if (c.getTargetUser().equals(user)) {
					cf = c;
					break;
				}
			}
			if (length == -1) {
				cf.setLength(-1L);
				return;
			}
			while (cf.getFos() == null)
				;
			try {
				cf.getFos().write(buffer, 0, length);
			} catch (IOException e) {
				e.printStackTrace();
			}
			cf.setLength(cf.getLength() + length);
		}
	}

	@Override
	public void process(Server server) {
	}

	public File getSrcFile() {
		return srcFile;
	}

	public void setSrcFile(File srcFile) {
		this.srcFile = srcFile;
	}

	public File getDestFile() {
		return destFile;
	}

	public void setDestFile(File destFile) {
		this.destFile = destFile;
	}

	public int getLength() {
		return length;
	}

	public void setLength(int length) {
		this.length = length;
	}

	public void setBuffer(byte[] buffer) {
		this.buffer = Arrays.copyOf(buffer, buffer.length);
	}

}
