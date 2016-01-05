package com.chat.view;

import java.awt.AWTException;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import com.chat.main.ChatClient;
import com.chat.model.User;
import com.chat.model.msg.Msg;
import com.chat.model.msg.SoundMsg;
import com.chat.model.msg.UpdateUserMsg;

public class UserListFrame extends JFrame {

	public static final int FRAME_WIDTH = 300;
	public static final int FRAME_HEIGHT = 550;

	private UserListPanel userListPanel = new UserListPanel();
	private Map<User, List<SoundMsg>> chatMap = new HashMap<User, List<SoundMsg>>();
	private JLabel lbl_name = new JLabel();
	private JLabel lbl_head = new JLabel();
	private JLabel lbl_sign = new JLabel();
	private JTextField txt_sign = new JTextField();
	private JButton btn_chatAll = new JButton("群聊");

	public UserListFrame() {
		super("聊天系统--用户列表");
		this.setLocation(1000, 100);
		this.setSize(FRAME_WIDTH, FRAME_HEIGHT);
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.setResizable(false);
		this.addWindowListener(new WindowAdapter() {
			public void windowIconified(WindowEvent e) {
				if (SystemTray.isSupported()) {
					setVisible(false);
				}
			}
		});
		if (SystemTray.isSupported()) {
			SystemTray tray = SystemTray.getSystemTray();
			PopupMenu popup = new PopupMenu();
			MenuItem defaultItem = new MenuItem("打开/隐藏界面");
			defaultItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					if (isVisible()) {
						setVisible(false);
					} else {
						setVisible(true);
						setState(0);
					}
				}
			});
			MenuItem updateItem = new MenuItem("修改信息");
			updateItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					new UserInfoFrame(null);
				}
			});
			MenuItem exitItem = new MenuItem("退出");
			exitItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					System.exit(0);
				}
			});
			popup.add(defaultItem);
			popup.add(updateItem);
			popup.add(exitItem);
			ImageIcon img = new ImageIcon(ChatClient.getInstance().getUser()
					.getImgPath());
			TrayIcon ti = new TrayIcon(img.getImage().getScaledInstance(16, 16,
					Image.SCALE_DEFAULT), "聊天系统", popup);
			ti.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					UserListFrame.this.setVisible(true);
					setState(0);
					if (!chatMap.isEmpty()) {
						User user = chatMap.keySet().iterator().next();

						ChatFrame cf = null;
						if (chatMap.get(user).get(0).getTargetUser() == null) {
							cf = new ChatFrame(ChatClient.getInstance()
									.getUser(), null);
						} else {
							cf = new ChatFrame(ChatClient.getInstance()
									.getUser(), user);
						}
						ChatClient.getInstance().getChatFrameList().add(cf);
						for (SoundMsg msg : chatMap.get(user)) {
							msg.processWithNoSound(ChatClient.getInstance());
						}
						chatMap.remove(user);
					}
				}
			});
			try {
				tray.add(ti);
			} catch (AWTException e1) {
				e1.printStackTrace();
			}
		}
		init();
		this.setVisible(true);
		new Thread(new Runnable() {
			public void run() {
				boolean flag = false;
				boolean nullFlag = true;
				while (SystemTray.isSupported()) {
					SystemTray tray = SystemTray.getSystemTray();
					TrayIcon ti = tray.getTrayIcons()[0];
					Image img = null;
					if (!chatMap.isEmpty()) {
						Iterator<User> i = chatMap.keySet().iterator();
						User user = i.next();
						if (!flag) {
							img = new ImageIcon(ChatClient.getInstance()
									.getProperties().get("DEFAULT_HEAD_PATH"))
									.getImage();
						} else {
							img = new ImageIcon(user.getImgPath()).getImage()
									.getScaledInstance(16, 16,
											Image.SCALE_DEFAULT);
						}
						flag = !flag;
						nullFlag = false;
						ti.setImage(img);
					} else {
						img = new ImageIcon(ChatClient.getInstance().getUser()
								.getImgPath()).getImage().getScaledInstance(16,
								16, Image.SCALE_DEFAULT);
						if (!nullFlag) {
							ti.setImage(img);
							nullFlag = true;
						}
					}
					try {
						Thread.sleep(500);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}).start();
	}

	public void init() {
		this.setLayout(null);

		ImageIcon img = new ImageIcon(ChatClient.getInstance().getUser()
				.getImgPath());
		img.setImage(img.getImage().getScaledInstance(40, 40,
				Image.SCALE_DEFAULT));
		lbl_head.setIcon(img);
		lbl_head.setBounds(10, 10, 40, 40);
		lbl_head.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				new UserInfoFrame(null);
			}
		});
		this.add(lbl_head);

		lbl_name.setText(ChatClient.getInstance().getUser().getName());
		lbl_name.setBounds(60, 20, 300, 30);
		this.add(lbl_name);

		lbl_sign.setText(ChatClient.getInstance().getUser().getSign());
		if (lbl_sign.getText().equals("")) {
			lbl_sign.setText("请输入个性签名");
		}
		lbl_sign.setBounds(20, 60, 300, 30);
		lbl_sign.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				txt_sign.setText(lbl_sign.getText());
				txt_sign.setVisible(true);
				txt_sign.requestFocus();
				txt_sign.selectAll();
				lbl_sign.setVisible(false);
			}
		});
		this.add(lbl_sign);

		txt_sign.setBounds(20, 60, 200, 30);
		txt_sign.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Msg msg = new UpdateUserMsg();
				msg.setUser(new User(ChatClient.getInstance().getUser()));
				msg.getUser().setSign(txt_sign.getText().trim());
				msg.send();
				txt_sign.setVisible(false);
				lbl_sign.setVisible(true);
			}
		});
		txt_sign.setVisible(false);
		this.add(txt_sign);

		userListPanel.setBounds(0, 0, FRAME_WIDTH - 30, 350);
		JScrollPane sp = new JScrollPane();
		sp.getViewport().add(userListPanel);
		sp.setBounds(0, 100, FRAME_WIDTH - 10, 350);
		this.add(sp);

		btn_chatAll.setBounds(50, 460, 50, 25);
		btn_chatAll.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				for (ChatFrame c : ChatClient.getInstance().getChatFrameList()) {
					if (c.getTargetUser() == null) {
						return;
					}
				}
				ChatFrame cf = new ChatFrame(
						ChatClient.getInstance().getUser(), null);
				ChatClient.getInstance().getChatFrameList().add(cf);
			}
		});
		this.add(btn_chatAll);
	}

	public UserListPanel getUserListPanel() {
		return userListPanel;
	}

	public void setUserListPanel(UserListPanel userListPanel) {
		this.userListPanel = userListPanel;
	}

	public Map<User, List<SoundMsg>> getChatMap() {
		return chatMap;
	}

	public void setChatMap(Map<User, List<SoundMsg>> chatMap) {
		this.chatMap = chatMap;
	}

	public void addUnreadMsg(SoundMsg msg) {
		if (chatMap.get(msg.getUser()) == null) {
			chatMap.put(msg.getUser(), new ArrayList<SoundMsg>());
		}
		chatMap.get(msg.getUser()).add(msg);
	}

	public JLabel getLbl_name() {
		return lbl_name;
	}

	public void setLbl_name(JLabel lblName) {
		lbl_name = lblName;
	}

	public JLabel getLbl_head() {
		return lbl_head;
	}

	public void setLbl_head(JLabel lblHead) {
		lbl_head = lblHead;
	}

	public JLabel getLbl_sign() {
		return lbl_sign;
	}

	public void setLbl_sign(JLabel lblSign) {
		lbl_sign = lblSign;
	}
}
