package Client.feature.remote;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.Socket;
import java.util.Base64;

public class ScreenSender implements Runnable {

	Socket socket = null;
	Robot robot = null;
	Rectangle rectangle = null;
	boolean continueLoop = true;

	OutputStream os = null;

	public ScreenSender(Socket socket, Robot robot, Rectangle rect) {
		this.socket = socket;
		this.robot = robot;
		rectangle = rect;
	}

	public void run() {
		try {
			os = socket.getOutputStream();
		} catch (IOException ex) {
			ex.printStackTrace();
		}

		while (continueLoop) {
			BufferedImage image = robot.createScreenCapture(rectangle);
			try {
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				ImageIO.write(image, "jpeg", baos);
				byte[] imgData = baos.toByteArray();
				ObjectOutputStream oos = new ObjectOutputStream(os);
				oos.writeObject(imgData);
				oos.flush();
				// oos.writeUTF(byteString);
				Thread.sleep(100);
			} catch (IOException | InterruptedException ex) {
				//ex.printStackTrace();
				break;
			}
		}
	}

	public void stop() {
		this.continueLoop = false;
	}
}
