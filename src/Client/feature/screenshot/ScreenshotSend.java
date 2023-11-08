package Client.feature.screenshot;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.Socket;
import java.util.Base64;

public class ScreenshotSend implements Runnable {

	Socket socket = null;
	OutputStream os = null;

	public ScreenshotSend(Socket socket) {
		this.socket = socket;
	}

	public void run() {
		try {
			os = socket.getOutputStream();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		try {
			BufferedImage image = new Robot()
					.createScreenCapture(new Rectangle(Toolkit.getDefaultToolkit().getScreenSize()));
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ImageIO.write(image, "png", baos);
			byte[] imgData = baos.toByteArray();
			ObjectOutputStream oos = new ObjectOutputStream(os);
			oos.writeObject(imgData);
			oos.flush();
			// oos.writeUTF(byteString);
		} catch (IOException ex) {
			ex.printStackTrace();
		} catch (HeadlessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (AWTException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
