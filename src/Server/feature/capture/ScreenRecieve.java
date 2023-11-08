package Server.feature.capture;

import javax.imageio.ImageIO;
import javax.swing.*;

import packages.CapturePackage;
import packages.RemoteDesktopPackage;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.Socket;

public class ScreenRecieve {
	private static Socket socket;
	private static InputStream inputStream;
	private static JPanel panel;
	static Image image1 = null;

	public ScreenRecieve(Socket sk, JPanel panel) {
		socket = sk;
		this.panel = panel;
	}

	public static void main(String[] args) {
		try {
			CapturePackage cp = new CapturePackage();
			cp.setCmd("Start");
			cp.setMessage("capture");
			PrintWriter outputToClient;
			try {
				outputToClient = new PrintWriter(socket.getOutputStream(), true);
				outputToClient.println(cp);
			} catch (IOException ex) {
			}

			inputStream = socket.getInputStream();
			// Read screenshots of the client and then draw them
			byte[] byteArray = (byte[]) new ObjectInputStream(inputStream).readObject();

			image1 = ImageIO.read(new ByteArrayInputStream(byteArray));

			// Draw the received screenshots
			Graphics graphics = panel.getGraphics();
			graphics.drawImage(image1, 0, 0, panel.getWidth(), panel.getHeight(), panel);
		} catch (IOException | ClassNotFoundException ex) {
			ex.printStackTrace();
		}
	}
}
