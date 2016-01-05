package com.chat.model.msg;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

import sun.audio.AudioPlayer;

import com.chat.main.ChatClient;

public abstract class SoundMsg extends Msg {
	public abstract void processWithNoSound(ChatClient client);

	public void playSound(String path) {
		try {
			AudioPlayer.player.start(new FileInputStream(path));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
}
