package Client.feature.record;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.Socket;
import java.util.Base64;

public class ScreenSend implements Runnable{

	Socket socket = null;
	Robot robot = null;
	Rectangle rectangle = null;
	boolean continueLoop = true;

	OutputStream os = null;

	public ScreenSend(Socket socket, Robot robot, Rectangle rect) {
		this.socket = socket;
		this.robot = robot;
		rectangle = rect;
		new Thread(this).start();
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
				ImageIO.write(image, "png", baos);
				byte[] imgData = baos.toByteArray();
				ObjectOutputStream oos = new ObjectOutputStream(os);
				oos.writeObject(imgData);
				oos.flush();
				System.out.println("da gui");
				// oos.writeUTF(byteString);
			} catch (IOException ex) {
				ex.printStackTrace();
			}

			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
