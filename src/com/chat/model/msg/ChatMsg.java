package com.chat.model.msg;

import java.awt.Color;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.swing.Icon;
import javax.swing.text.BadLocationException;
import javax.swing.text.Element;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import com.chat.main.ChatClient;
import com.chat.main.Server;
import com.chat.model.User;
import com.chat.view.ChatFrame;

public class ChatMsg extends SoundMsg {

	public static final SimpleDateFormat FORMAT = new SimpleDateFormat(
			"hh:mm:ss");

	private byte[] buf;
	private Date date;

	private StyledDocument doc;

	public boolean equals(User user1, User user2) {
		if (user1 == null && user2 == null) {
			return true;
		} else if (user1 == null || user2 == null) {
			return false;
		}
		return user1.equals(user2);
	}

	private static List<Element> getAllElements(Element[] roots) {
		List<Element> elements = new LinkedList<Element>();
		for (int i = 0; i < roots.length; i++) {
			if (roots[i] == null) {
				continue;
			}
			elements.add(roots[i]);
			for (int j = 0; j < roots[i].getElementCount(); j++) {
				Element element = roots[i].getElement(j);
				elements.addAll(getAllElements(new Element[] { element }));
			}
		}
		return elements;
	}

	@Override
	public void process(ChatClient client) {
		this.getDoc();
		ChatFrame cf = null;
		if (this.equals(client.getUser(), user)) {
			for (ChatFrame c : client.getChatFrameList()) {
				if (this.equals(c.getTargetUser(), targetUser)) {
					cf = c;
					break;
				}
			}
		} else {
			for (ChatFrame c : client.getChatFrameList()) {
				if (targetUser == null) {
					if (this.equals(c.getTargetUser(), null)) {
						cf = c;
						break;
					}
				} else if (this.equals(c.getTargetUser(), user)) {
					cf = c;
					break;
				}
			}
			this.playSound(client.getProperties().get("MSG_SOUND_PATH"));
		}
		if (cf == null) {
			client.getUserListFrame().addUnreadMsg(this);
			return;
		}
		List<Element> list = getAllElements(doc.getRootElements());
		List<Icon> iconList = new ArrayList<Icon>();
		SimpleAttributeSet sas = new SimpleAttributeSet();
		SimpleAttributeSet sas2 = new SimpleAttributeSet();
		StyleConstants.setForeground(sas2, Color.GRAY);
		for (int i = 0; i < list.size(); i++) {
			Element e = list.get(i);
			if (e.getName().equals("icon")) {
				Icon icon = StyleConstants.getIcon(e.getAttributes());
				iconList.add(icon);
			} else if (e.getName().equals("content")) {
				StyleConstants.setFontSize(sas, StyleConstants.getFontSize(e
						.getAttributes()));
				StyleConstants.setForeground(sas, StyleConstants
						.getForeground(e.getAttributes()));
				StyleConstants.setBold(sas, StyleConstants.isBold(e
						.getAttributes()));
				StyleConstants.setItalic(sas, StyleConstants.isItalic(e
						.getAttributes()));
				StyleConstants.setUnderline(sas, StyleConstants.isUnderline(e
						.getAttributes()));
			}
		}
		try {
			cf.getTxt_display().getStyledDocument().insertString(
					cf.getTxt_display().getStyledDocument().getLength(),
					user.getName() + " " + FORMAT.format(date) + " :\n", sas2);
			for (int i = 0; i < doc.getText(0, doc.getLength()).length(); i++) {
				cf.getTxt_display().setCaretPosition(
						cf.getTxt_display().getStyledDocument().getLength());
				if (((StyledDocument) doc).getCharacterElement(i).getName()
						.equals("icon")) {
					cf.getTxt_display().insertIcon(iconList.remove(0));
				} else {
					cf.getTxt_display().getStyledDocument()
							.insertString(
									cf.getTxt_display().getStyledDocument()
											.getLength(), doc.getText(i, 1),
									sas);
				}
			}
			cf.getTxt_display().getStyledDocument().insertString(
					cf.getTxt_display().getStyledDocument().getLength(), "\n",
					sas);
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
	}

	public void processWithNoSound(ChatClient client) {
		this.getDoc();
		ChatFrame cf = null;
		if (this.equals(client.getUser(), user)) {
			for (ChatFrame c : client.getChatFrameList()) {
				if (this.equals(c.getTargetUser(), targetUser)) {
					cf = c;
					break;
				}
			}
		} else {
			for (ChatFrame c : client.getChatFrameList()) {
				if (targetUser == null) {
					if (this.equals(c.getTargetUser(), null)) {
						cf = c;
						break;
					}
				} else if (this.equals(c.getTargetUser(), user)) {
					cf = c;
					break;
				}
			}
		}
		List<Element> list = getAllElements(doc.getRootElements());
		List<Icon> iconList = new ArrayList<Icon>();
		SimpleAttributeSet sas = new SimpleAttributeSet();
		SimpleAttributeSet sas2 = new SimpleAttributeSet();
		StyleConstants.setForeground(sas2, Color.GRAY);
		for (int i = 0; i < list.size(); i++) {
			Element e = list.get(i);
			if (e.getName().equals("icon")) {
				Icon icon = StyleConstants.getIcon(e.getAttributes());
				iconList.add(icon);
			} else if (e.getName().equals("content")) {
				StyleConstants.setFontSize(sas, StyleConstants.getFontSize(e
						.getAttributes()));
				StyleConstants.setForeground(sas, StyleConstants
						.getForeground(e.getAttributes()));
				StyleConstants.setBold(sas, StyleConstants.isBold(e
						.getAttributes()));
				StyleConstants.setItalic(sas, StyleConstants.isItalic(e
						.getAttributes()));
				StyleConstants.setUnderline(sas, StyleConstants.isUnderline(e
						.getAttributes()));
			}
		}
		try {
			cf.getTxt_display().getStyledDocument().insertString(
					cf.getTxt_display().getStyledDocument().getLength(),
					user.getName() + " " + FORMAT.format(date) + " :\n", sas2);
			for (int i = 0; i < doc.getText(0, doc.getLength()).length(); i++) {
				cf.getTxt_display().setCaretPosition(
						cf.getTxt_display().getStyledDocument().getLength());
				if (((StyledDocument) doc).getCharacterElement(i).getName()
						.equals("icon")) {
					cf.getTxt_display().insertIcon(iconList.remove(0));
				} else {
					cf.getTxt_display().getStyledDocument()
							.insertString(
									cf.getTxt_display().getStyledDocument()
											.getLength(), doc.getText(i, 1),
									sas);
				}
			}
			cf.getTxt_display().getStyledDocument().insertString(
					cf.getTxt_display().getStyledDocument().getLength(), "\n",
					sas);
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void process(Server server) {
		date = new Date();
		if (targetUser != null && server.getUserByUser(targetUser) == null) {
			server.getOfflineMsg().add(this);
		}
	}

	public StyledDocument getDoc() {
		ByteArrayInputStream bais = new ByteArrayInputStream(buf);
		ObjectInputStream ois = null;
		try {
			ois = new ObjectInputStream(bais);
			doc = (StyledDocument) ois.readObject();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} finally {
			if (ois != null) {
				try {
					ois.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return doc;
	}

	public void setDoc(StyledDocument doc) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream(102400);
		ObjectOutputStream oos = null;
		try {
			oos = new ObjectOutputStream(baos);
			oos.writeObject(doc);
			oos.flush();
			buf = baos.toByteArray();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (oos != null) {
				try {
					oos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

}
