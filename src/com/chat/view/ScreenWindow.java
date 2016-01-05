package com.chat.view;

import java.awt.AWTException;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.image.BufferedImage;
import java.awt.image.LookupOp;
import java.awt.image.LookupTable;
import java.awt.image.ShortLookupTable;
import java.io.IOException;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

public class ScreenWindow extends JFrame {

	private boolean isDrag = false;
	private int x = 0;
	private int y = 0;
	private int xEnd = 0;
	private int yEnd = 0;
	private Image image;
	private Image offImage;
	
	public ScreenWindow() {
		Dimension screenDims = Toolkit.getDefaultToolkit().getScreenSize();
		try {
			image = this.getScreenImage(0, 0, screenDims.width,
					screenDims.height);
		} catch (AWTException e2) {
			e2.printStackTrace();
		} catch (InterruptedException e2) {
			e2.printStackTrace();
		}
		BufferedImage bufferImg = this.convertImageToBuffer(image);
		bufferImg = this.getPicture(bufferImg);
		JLabel label = new JLabel(new ImageIcon(bufferImg));
		label.setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));
		label.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if (e.getButton() == MouseEvent.BUTTON3) {
					dispose();
				} else if (e.getButton() == MouseEvent.BUTTON1
						&& e.getClickCount() == 2) {
					if (e.getX() > Math.min(x, xEnd)
							&& e.getY() > Math.min(y, yEnd)
							&& e.getX() < Math.max(x, xEnd)
							&& e.getY() < Math.max(y, yEnd)) {
						try {
							Image i = getScreenImage(Math.min(x, xEnd), Math
									.min(y, yEnd), Math.abs(x - xEnd), Math
									.abs(y - yEnd));
							Image tmp = createImage(Math.abs(x - xEnd), Math
									.abs(y - yEnd));
							tmp.getGraphics().drawImage(i, 0, 0, null);
							setClipboardImage(tmp);
						} catch (AWTException e1) {
							e1.printStackTrace();
						} catch (InterruptedException e1) {
							e1.printStackTrace();
						}
						dispose();
					}
				}
			}

			public void mousePressed(MouseEvent e) {
				if (e.getX() > Math.min(x, xEnd)
						&& e.getY() > Math.min(y, yEnd)
						&& e.getX() < Math.max(x, xEnd)
						&& e.getY() < Math.max(y, yEnd)) {
					return;
				}
				x = e.getX();
				xEnd = x;
				y = e.getY();
				yEnd = y;
			}

			public void mouseReleased(MouseEvent e) {
				if (e.getX() > Math.min(x, xEnd)
						&& e.getY() > Math.min(y, yEnd)
						&& e.getX() < Math.max(x, xEnd)
						&& e.getY() < Math.max(y, yEnd)) {
					return;
				}
				if (isDrag) {
					xEnd = e.getX();
					yEnd = e.getY();
					if (x > xEnd) {
						int temp = x;
						x = xEnd;
						xEnd = temp;
					}
					if (y > yEnd) {
						int temp = y;
						y = yEnd;
						yEnd = temp;
					}
					repaint();
				}
			}
		});
		label.addMouseMotionListener(new MouseMotionAdapter() {
			public void mouseDragged(MouseEvent e) {
				if (!isDrag) {
					isDrag = true;
				}
				xEnd = e.getX();
				yEnd = e.getY();
				repaint();
			}
		});
		this.setUndecorated(true);
		this.getContentPane().add(label);
		this.setSize(screenDims.width, screenDims.height);
		this.setVisible(true);
		this.setExtendedState(JFrame.MAXIMIZED_BOTH);
	}

	public void paint(Graphics g) {
		if (offImage == null) {
			offImage = this.createImage(Toolkit.getDefaultToolkit()
					.getScreenSize().width, Toolkit.getDefaultToolkit()
					.getScreenSize().height);
		}
		super.paint(offImage.getGraphics());
		if (Math.abs(x - xEnd) > 0 && Math.abs(y - yEnd) > 0) {
			Image img = convertImageToBuffer(image).getSubimage(
					Math.min(x, xEnd), Math.min(y, yEnd), Math.abs(x - xEnd),
					Math.abs(y - yEnd));
			offImage.getGraphics().drawImage(img, Math.min(x, xEnd),
					Math.min(y, yEnd), Math.abs(x - xEnd), Math.abs(y - yEnd),
					null);
		}
		g.drawImage(offImage, 0, 0, null);
	}

	public BufferedImage convertImageToBuffer(Image pic) {
		BufferedImage bufferedImage = new BufferedImage(pic.getWidth(null), pic
				.getHeight(null), BufferedImage.TYPE_INT_RGB);
		Graphics g = bufferedImage.createGraphics();
		g.drawImage(pic, 0, 0, null);
		g.dispose();
		return bufferedImage;
	}

	public BufferedImage getPicture(BufferedImage originalPic) {
		int imageWidth = originalPic.getWidth();
		int imageHeight = originalPic.getHeight();

		BufferedImage newPic = new BufferedImage(imageWidth, imageHeight,
				BufferedImage.TYPE_3BYTE_BGR);

		short[] brighten = new short[256];
		short pixelValue;

		for (int i = 0; i < 256; i++) {
			pixelValue = (short) (i - 60);
			if (pixelValue < 0) {
				pixelValue = 0;
			}
			brighten[i] = pixelValue;
		}

		LookupTable lut = new ShortLookupTable(0, brighten);
		LookupOp lop = new LookupOp(lut, null);
		lop.filter(originalPic, newPic);
		return newPic;
	}

	public Image getScreenImage(int x, int y, int w, int h)
			throws AWTException, InterruptedException {
		Robot robot = new Robot();
		Image screen = robot.createScreenCapture(new Rectangle(x, y, w, h))
				.getScaledInstance(w, h, Image.SCALE_SMOOTH);
		return screen;
	}

	public void setClipboardImage(final Image image) {
		Transferable trans = new Transferable() {
			public DataFlavor[] getTransferDataFlavors() {
				return new DataFlavor[] { DataFlavor.imageFlavor };
			}

			public boolean isDataFlavorSupported(DataFlavor flavor) {
				return DataFlavor.imageFlavor.equals(flavor);
			}

			public Object getTransferData(DataFlavor flavor)
					throws UnsupportedFlavorException, IOException {
				if (isDataFlavorSupported(flavor))
					return image;
				throw new UnsupportedFlavorException(flavor);
			}
		};
		Toolkit.getDefaultToolkit().getSystemClipboard().setContents(trans,
				null);
	}
}
