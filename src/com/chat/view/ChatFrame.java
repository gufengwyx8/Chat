package com.chat.view;

import java.awt.Color;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.color.ColorSpace;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

import com.chat.main.ChatClient;
import com.chat.model.User;
import com.chat.model.msg.AcceptFileMsg;
import com.chat.model.msg.ChatMsg;
import com.chat.model.msg.RefuseFileMsg;
import com.chat.model.msg.SendFileMsg;

public class ChatFrame extends JFrame {

	public static final int FRAME_WIDTH = 500;
	public static final int FRAME_HEIGHT = 480;

	private File file;
	private FileOutputStream fos;
	private long length;
	private User user, targetUser;
	private JTextPane txt_input = new JTextPane() {
		@SuppressWarnings("unchecked")
		public void paste() {
			if (isEditable() && isEnabled()) {
				Clipboard cb = Toolkit.getDefaultToolkit().getSystemClipboard();
				Transferable contents = cb.getContents(null);
				if (contents != null
						&& contents
								.isDataFlavorSupported(DataFlavor.stringFlavor)) {
					super.paste();
				} else if (contents != null
						&& contents
								.isDataFlavorSupported(DataFlavor.imageFlavor)) {
					try {
						Image img = (Image) contents
								.getTransferData(DataFlavor.imageFlavor);
						Icon icon = new ImageIcon(img);
						insertIcon(icon);
					} catch (UnsupportedFlavorException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
				} else if (contents != null
						&& contents
								.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
					try {
						if (!targetUser.isLogin()) {
							JOptionPane.showMessageDialog(null, "不允许向离线用户发送文件",
									"错误", JOptionPane.ERROR_MESSAGE);
							return;
						}
						if (file != null) {
							JOptionPane.showMessageDialog(null,
									"正在发送文件，不允许同时发送", "错误",
									JOptionPane.ERROR_MESSAGE);
							return;
						}
						List<File> file = (List<File>) contents
								.getTransferData(DataFlavor.javaFileListFlavor);
						SendFileMsg msg = new SendFileMsg();
						msg.setUser(user);
						msg.setTargetUser(targetUser);
						msg.setFile(file.get(0));
						msg.send();
					} catch (UnsupportedFlavorException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
	};
	private JTextPane txt_display = new JTextPane();
	private JComboBox com_size = new JComboBox(new String[] { "10", "11", "12",
			"14", "16", "20", "24", "32", "36" });
	private JButton btn_color = new JButton("改颜色");
	private JButton btn_bold = new JButton("粗体");
	private JButton btn_italic = new JButton("斜体");
	private JButton btn_underline = new JButton("下划线");
	private JButton btn_sendFile = new JButton("发送文件");
	private JButton btn_acceptFile = new JButton("接收文件");
	private JButton btn_refuseFile = new JButton("拒绝文件");
	private JLabel lbl_head = new JLabel();
	private JLabel lbl_name = new JLabel();
	private JLabel lbl_file = new JLabel() {
		public void setText(String text) {
			int length = 13;
			for (int i = length - 6; i < text.length(); i += length) {
				text = text.substring(0, i + 1) + "<br/>"
						+ text.substring(i + 1);
			}
			text = "<html>" + text + "</html>";
			super.setText(text);
		}
	};
	private JProgressBar fileBar = new JProgressBar();

	private Color color = Color.BLACK;
	private boolean bold = false, italic = false, underline = false;

	public ChatFrame(User user, User targetUser) {
		super("与  " + (targetUser == null ? "大家" : targetUser.getName())
				+ " 聊天");
		this.user = user;
		this.targetUser = targetUser;
		this.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				for (int i = 0; i < ChatClient.getInstance().getChatFrameList()
						.size(); i++) {
					if (ChatClient.getInstance().getChatFrameList().get(i) == ChatFrame.this) {
						ChatClient.getInstance().getChatFrameList().remove(i);
						break;
					}
				}
				ChatFrame.this.dispose();
			}
		});
		this.setLocation(150, 100);
		this.setSize(FRAME_WIDTH, FRAME_HEIGHT);
		this.setResizable(false);
		init();
		this.setVisible(true);
	}

	public void init() {
		this.setLayout(null);
		JScrollPane sp = new JScrollPane(txt_input);
		sp.setBounds(20, 330, 350, 100);
		this.add(sp);

		if (targetUser != null) {
			this.setTargetUser(targetUser);
			lbl_head.setBounds(20, 10, 40, 40);
			this.add(lbl_head);

			lbl_name.setText(targetUser.getName());
			lbl_name.setBounds(70, 10, 100, 30);
			this.add(lbl_name);
		}

		sp = new JScrollPane(txt_display,
				JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		txt_display.setEditable(false);
		sp.setBounds(20, 50, 350, 250);
		this.add(sp);

		Monitor m = new Monitor();

		com_size.setBounds(20, 305, 70, 20);
		com_size.setSelectedIndex(2);
		com_size.addActionListener(m);
		this.add(com_size);

		btn_color.setBounds(99, 305, 50, 20);
		btn_color.addActionListener(m);
		this.add(btn_color);

		btn_bold.setBounds(160, 305, 50, 20);
		btn_bold.addActionListener(m);
		this.add(btn_bold);

		btn_italic.setBounds(220, 305, 50, 20);
		btn_italic.addActionListener(m);
		this.add(btn_italic);

		btn_underline.setBounds(280, 305, 50, 20);
		btn_underline.addActionListener(m);
		this.add(btn_underline);

		btn_sendFile.setBounds(390, 80, 90, 30);
		btn_sendFile.addActionListener(m);
		if (targetUser != null) {
			this.add(btn_sendFile);
		}

		btn_acceptFile.setBounds(390, 120, 90, 30);
		btn_acceptFile.addActionListener(m);
		btn_acceptFile.setVisible(false);
		this.add(btn_acceptFile);

		btn_refuseFile.setBounds(390, 160, 90, 30);
		btn_refuseFile.addActionListener(m);
		btn_refuseFile.setVisible(false);
		this.add(btn_refuseFile);

		lbl_file.setBounds(390, 200, 600, 190);
		this.add(lbl_file);

		fileBar.setVisible(false);
		fileBar.setBounds(390, 400, 100, 20);
		this.add(fileBar);

		txt_input.addKeyListener(new KeyMonitor());
	}

	class Monitor implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			SimpleAttributeSet sas = new SimpleAttributeSet();
			txt_input.setCaretPosition(txt_input.getStyledDocument()
					.getLength());
			if (e.getSource() == com_size) {
				StyleConstants.setFontSize(sas, Integer.parseInt(com_size
						.getSelectedItem().toString()));
			} else if (e.getSource() == btn_color) {
				Color c = JColorChooser.showDialog(null, "颜色选择", null);
				if (c != null) {
					color = c;
					StyleConstants.setForeground(sas, color);
				}
			} else if (e.getSource() == btn_bold) {
				bold = !bold;
				btn_bold.setSelected(bold);
				StyleConstants.setBold(sas, bold);
			} else if (e.getSource() == btn_italic) {
				italic = !italic;
				btn_italic.setSelected(italic);
				StyleConstants.setItalic(sas, italic);
			} else if (e.getSource() == btn_underline) {
				underline = !underline;
				btn_underline.setSelected(underline);
				StyleConstants.setUnderline(sas, underline);
			} else if (e.getSource() == btn_sendFile) {
				if (!targetUser.isLogin()) {
					JOptionPane.showMessageDialog(null, "不允许向离线用户发送文件", "错误",
							JOptionPane.ERROR_MESSAGE);
					return;
				}
				if (file != null) {
					JOptionPane.showMessageDialog(null, "正在发送文件，不允许同时发送", "错误",
							JOptionPane.ERROR_MESSAGE);
					return;
				}
				JFileChooser chooser = new JFileChooser();
				if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
					if (!new File(chooser.getSelectedFile().getAbsolutePath())
							.exists()) {
						JOptionPane.showMessageDialog(null, "文件不存在", "错误",
								JOptionPane.ERROR_MESSAGE);
						return;
					}
					SendFileMsg msg = new SendFileMsg();
					msg.setUser(user);
					msg.setTargetUser(targetUser);
					msg.setFile(chooser.getSelectedFile());
					msg.send();
				}
				return;
			} else if (e.getSource() == btn_acceptFile) {
				if (file != null) {
					JFileChooser chooser = new JFileChooser();
					chooser.setSelectedFile(new File(file.getName()));
					if (chooser.showDialog(null, "保存") == JFileChooser.APPROVE_OPTION) {
						if (chooser.getSelectedFile().exists()) {
							chooser.getSelectedFile().delete();
						}
						try {
							chooser.getSelectedFile().createNewFile();
						} catch (IOException e1) {
							e1.printStackTrace();
						}
						AcceptFileMsg msg = new AcceptFileMsg();
						msg.setSrcFile(file);
						msg.setDestFile(chooser.getSelectedFile());
						msg.setUser(user);
						msg.setTargetUser(targetUser);
						msg.send();
					}
				}
			} else if (e.getSource() == btn_refuseFile) {
				if (file != null) {
					RefuseFileMsg msg = new RefuseFileMsg();
					msg.setFile(file);
					msg.setUser(user);
					msg.setTargetUser(targetUser);
					msg.send();
				}
			}
			txt_input.getStyledDocument().setCharacterAttributes(0,
					txt_input.getStyledDocument().getLength() + 1, sas, false);
			txt_input.setCharacterAttributes(sas, false);
		}
	}

	class KeyMonitor extends KeyAdapter {
		int num = 0;

		public void keyPressed(KeyEvent e) {
			if (e.isControlDown() && e.getKeyCode() == KeyEvent.VK_ENTER) {
				try {
					txt_input.getStyledDocument().insertString(
							txt_input.getCaretPosition(), "\n", null);
				} catch (BadLocationException e1) {
					e1.printStackTrace();
				}
			} else if (e.getKeyCode() == KeyEvent.VK_ENTER) {
				if (txt_input.getText().equals("")) {
					JOptionPane.showMessageDialog(null, "不允许发送空消息", "错误",
							JOptionPane.ERROR_MESSAGE);
				} else {
					ChatMsg msg = new ChatMsg();
					msg.setDoc(txt_input.getStyledDocument());
					msg.setUser(user);
					msg.setTargetUser(targetUser);
					msg.send();
				}
			} else if (e.isControlDown() && e.isAltDown()
					&& e.getKeyCode() == KeyEvent.VK_A) {
				JFrame.setDefaultLookAndFeelDecorated(false);
				new ScreenWindow();
				JFrame.setDefaultLookAndFeelDecorated(true);
			}
		}

		public void keyReleased(KeyEvent e) {
			if (!e.isControlDown() && e.getKeyCode() == KeyEvent.VK_ENTER) {
				txt_input.setText("");
			}
		}
	}

	public User getTargetUser() {
		return targetUser;
	}

	public void setTargetUser(User targetUser) {
		this.targetUser = targetUser;
		if (targetUser != null) {
			ImageIcon img = new ImageIcon(targetUser.getImgPath());
			if (!targetUser.isLogin()) {
				BufferedImage bi = null;
				try {
					bi = ImageIO.read(new File(targetUser.getImgPath()));
				} catch (IOException e) {
					e.printStackTrace();
				}
				bi = new ColorConvertOp(ColorSpace
						.getInstance(ColorSpace.CS_GRAY), null)
						.filter(bi, null);
				img.setImage(bi);
			}
			img.setImage(img.getImage().getScaledInstance(40, 40,
					Image.SCALE_DEFAULT));
			lbl_head.setIcon(img);
		}
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public JTextPane getTxt_display() {
		return txt_display;
	}

	public void setTxt_display(JTextPane txtDisplay) {
		txt_display = txtDisplay;
	}

	public boolean equals(Object o) {
		if (!(o instanceof ChatFrame)) {
			return false;
		}
		ChatFrame cf = (ChatFrame) o;
		if (cf.targetUser == null && targetUser == null) {
			return cf.user.equals(user);
		} else if (cf.targetUser == null || targetUser == null) {
			return false;
		}
		return cf.user.equals(user) && cf.targetUser.equals(targetUser);
	}

	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file;
	}

	public JButton getBtn_acceptFile() {
		return btn_acceptFile;
	}

	public void setBtn_acceptFile(JButton btnAcceptFile) {
		btn_acceptFile = btnAcceptFile;
	}

	public JButton getBtn_refuseFile() {
		return btn_refuseFile;
	}

	public void setBtn_refuseFile(JButton btnRefuseFile) {
		btn_refuseFile = btnRefuseFile;
	}

	public JLabel getLbl_file() {
		return lbl_file;
	}

	public void setLbl_file(JLabel lblFile) {
		lbl_file = lblFile;
	}

	public long getLength() {
		return length;
	}

	public void setLength(long length) {
		this.length = length;
	}

	public FileOutputStream getFos() {
		return fos;
	}

	public void setFos(FileOutputStream fos) {
		this.fos = fos;
	}

	public JProgressBar getFileBar() {
		return fileBar;
	}

	public void setFileBar(JProgressBar fileBar) {
		this.fileBar = fileBar;
	}
}
