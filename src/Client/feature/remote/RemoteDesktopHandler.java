package Client.feature.remote;

import packages.GeneralPackage;

import java.awt.*;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class RemoteDesktopHandler {

	private String ipServer;

	public RemoteDesktopHandler(String ipServer) {
		this.ipServer = ipServer;
	}

	public void handleRemoteDesktop(GeneralPackage pkTin) {

		int port = Integer.parseInt(pkTin.getMessage());
		Robot robot;
		Rectangle rectangle;

		try {
			final Socket socketRemote = new Socket(ipServer, port);

			GraphicsEnvironment gEnv = GraphicsEnvironment.getLocalGraphicsEnvironment();
			GraphicsDevice gDev = gEnv.getDefaultScreenDevice();

			Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
			rectangle = new Rectangle(dim);

			robot = new Robot(gDev);
			ObjectOutputStream dos = new ObjectOutputStream(socketRemote.getOutputStream());
			dos.writeObject(rectangle);
			dos.flush();

			ScreenSender screenSender = new ScreenSender(socketRemote, robot, rectangle);
			new Thread(screenSender).start();
			new EventReceiver(socketRemote, robot);
		} catch (IOException | AWTException ex) {
			ex.printStackTrace();
		}
	}
}
