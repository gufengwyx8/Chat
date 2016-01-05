package com.chat.main;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.chat.model.User;
import com.chat.model.msg.ChatMsg;
import com.chat.model.msg.LoginMsg;
import com.chat.model.msg.LogoutMsg;
import com.chat.model.msg.Msg;
import com.chat.service.UserManager;
import com.chat.util.PropertiesUtil;

public class Server {

	public static final String PROPERTIES_PATH = "config.properties";

	public static final int LOGIN_MSG = 1;
	public static final int REG_MSG = 2;

	private List<Client> clientList = new ArrayList<Client>();
	private UserManager userManager = new UserManager();
	private List<ChatMsg> offlineMsg = new ArrayList<ChatMsg>();

	private Map<String, String> properties = PropertiesUtil
			.getPropertiesMap(PROPERTIES_PATH);

	public static void main(String[] args) {
		new Server();
	}

	public Server() {
		ServerSocket ss = null;
		try {
			ss = new ServerSocket(Integer.parseInt(properties
					.get("SERVER_TCP_PORT")));
			while (ss != null) {
				Socket s = ss.accept();
				InputStream is = s.getInputStream();
				OutputStream os = s.getOutputStream();
				DataInputStream dis = new DataInputStream(is);
				ObjectOutputStream oos = new ObjectOutputStream(os);
				int msgStr = is.read();
				if (msgStr == (LOGIN_MSG)) {
					String name = dis.readUTF();
					String password = dis.readUTF();
					User user = userManager.login(name, password);
					if (user != null && this.getClientByUser(user) == null) {
						System.out.println("用户:" + user.getName() + "  登录成功");
						user.setLogin(true);
						LoginMsg msg = new LoginMsg();
						msg.setUser(user);
						for (Client c : clientList) {
							msg.getOnlineUser().add(c.getUser());
							c.send(msg);
						}
						msg.process(this);
						oos.writeObject(msg);
						oos.flush();
						Client c = new Client(user, s, is, os);
						clientList.add(c);
						new Thread(new TCPThread(c)).start();
					} else {
						LoginMsg msg = new LoginMsg();
						oos.writeObject(msg);
						oos.flush();
						s.close();
					}
				} else if (msgStr == (REG_MSG)) {
					ObjectInputStream ois = new ObjectInputStream(is);
					User user = null;
					try {
						user = (User) ois.readObject();
					} catch (ClassNotFoundException e) {
						e.printStackTrace();
					}
					boolean success = userManager.reg(user);
					oos.writeBoolean(success);
					oos.flush();
					s.close();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (ss != null) {
				try {
					ss.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				ss = null;
			}
		}
	}

	class TCPThread implements Runnable {

		private Client c;

		public TCPThread(Client c) {
			this.c = c;
		}

		public void run() {
			try {
				while (true) {
					Msg msg = (Msg) c.getOis().readObject();
					msg.process(Server.this);
					if (msg.getTargetUser() == null) {
						for (Client c : clientList) {
							c.send(msg);
						}
					} else {
						Server.this.getClientByUser(msg.getUser()).send(msg);
						if (Server.this.getClientByUser(msg.getTargetUser()) != null) {
							Server.this.getClientByUser(msg.getTargetUser())
									.send(msg);
						}
					}
				}
			} catch (IOException e) {
				System.out.println("用户:" + c.getUser().getName() + "  退出");
				for (int i = 0; i < clientList.size(); i++) {
					if (clientList.get(i).getUser().equals(c.getUser())) {
						clientList.remove(i);
						break;
					}
				}
				LogoutMsg msg = new LogoutMsg();
				c.getUser().setLogin(false);
				msg.setUser(c.getUser());
				for (Client c : clientList) {
					c.send(msg);
				}
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} finally {
				if (c.getSocket() != null) {
					try {
						c.getSocket().close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

	class Client {
		private User user;
		private Socket socket;
		private ObjectOutputStream oos;
		private ObjectInputStream ois;

		public Client(User user, Socket s, InputStream is, OutputStream os) {
			this.user = user;
			this.socket = s;
			try {
				this.oos = new ObjectOutputStream(os);
				this.ois = new ObjectInputStream(is);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		public void send(Msg msg) {
			try {
				oos.writeObject(msg);
				oos.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		public User getUser() {
			return user;
		}

		public void setUser(User user) {
			this.user = user;
		}

		public ObjectOutputStream getOos() {
			return oos;
		}

		public void setOos(ObjectOutputStream oos) {
			this.oos = oos;
		}

		public ObjectInputStream getOis() {
			return ois;
		}

		public void setOis(ObjectInputStream ois) {
			this.ois = ois;
		}

		public Socket getSocket() {
			return socket;
		}

		public void setSocket(Socket socket) {
			this.socket = socket;
		}
	}

	public List<Client> getClientList() {
		return clientList;
	}

	public void setClientList(List<Client> clientList) {
		this.clientList = clientList;
	}

	public Map<String, String> getProperties() {
		return properties;
	}

	public void setProperties(Map<String, String> properties) {
		this.properties = properties;
	}

	private Client getClientByUser(User user) {
		for (Client c : clientList) {
			if (c.getUser().equals(user)) {
				return c;
			}
		}
		return null;
	}

	public User getUserByUser(User user) {
		for (Client c : clientList) {
			if (c.getUser().equals(user)) {
				return c.getUser();
			}
		}
		return null;
	}

	public List<ChatMsg> getOfflineMsg() {
		return offlineMsg;
	}

	public void setOfflineMsg(List<ChatMsg> offlineMsg) {
		this.offlineMsg = offlineMsg;
	}

	public UserManager getUserManager() {
		return userManager;
	}

	public void setUserManager(UserManager userManager) {
		this.userManager = userManager;
	}
}
