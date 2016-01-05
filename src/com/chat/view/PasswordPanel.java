package com.chat.view;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;

import com.chat.main.ChatClient;
import com.chat.model.User;
import com.chat.model.msg.UpdateUserMsg;

public class PasswordPanel extends JPanel {

	private User user;
	private UserInfoFrame frame;

	private JPasswordField txt_old = new JPasswordField();
	private JPasswordField txt_new1 = new JPasswordField();
	private JPasswordField txt_new2 = new JPasswordField();
	private JButton btn_submit = new JButton("确定");
	private JButton btn_cancel = new JButton("取消");

	public PasswordPanel(UserInfoFrame frame) {
		this.frame = frame;
		this.user = frame.getUser();
		this.setLayout(null);
		init();
	}

	public void init() {
		JLabel l = new JLabel("原密码");
		l.setBounds(10, 10, 50, 25);
		this.add(l);

		txt_old.setBounds(70, 10, 120, 25);
		this.add(txt_old);

		l = new JLabel("新密码");
		l.setBounds(10, 45, 50, 25);
		this.add(l);

		txt_new1.setBounds(70, 45, 120, 25);
		this.add(txt_new1);

		l = new JLabel("新密码2");
		l.setBounds(10, 80, 50, 25);
		this.add(l);

		txt_new2.setBounds(70, 80, 120, 25);
		this.add(txt_new2);

		btn_submit.setBounds(100, 290, 50, 20);
		btn_submit.addActionListener(new Monitor());
		this.add(btn_submit);

		btn_cancel.setBounds(180, 290, 50, 20);
		btn_cancel.addActionListener(new Monitor());
		this.add(btn_cancel);

	}

	class Monitor implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			if (e.getSource() == btn_submit) {
				if (!new String(txt_old.getPassword()).equals(user
						.getPassword())) {
					JOptionPane.showMessageDialog(null, "密码输入错误", "错误",
							JOptionPane.ERROR_MESSAGE);
					txt_old.setText("");
					txt_new1.setText("");
					txt_new2.setText("");
					return;
				}
				if (new String(txt_new1.getPassword()).equals("")
						|| new String(txt_new2.getPassword()).equals("")) {
					JOptionPane.showMessageDialog(null, "密码不能为空", "错误",
							JOptionPane.ERROR_MESSAGE);
					txt_old.setText("");
					txt_new1.setText("");
					txt_new2.setText("");
					return;
				}
				if (!new String(txt_new1.getPassword()).equals(new String(
						txt_new2.getPassword()))) {
					JOptionPane.showMessageDialog(null, "新密码输入不一致", "错误",
							JOptionPane.ERROR_MESSAGE);
					txt_old.setText("");
					txt_new1.setText("");
					txt_new2.setText("");
					return;
				}
				UpdateUserMsg msg = new UpdateUserMsg();
				User u = new User(ChatClient.getInstance().getUser());
				u.setPassword(new String(txt_new1.getPassword()));
				msg.setUser(u);
				msg.send();
				JOptionPane.showMessageDialog(null, "密码修改成功");
				frame.dispose();
			} else if (e.getSource() == btn_cancel) {
				frame.dispose();
			}
		}
	}
}
