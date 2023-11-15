package Client.feature.remote;

import java.awt.*;
import java.awt.event.InputEvent;
import java.io.IOException;
import java.net.Socket;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class EventReceiver implements Runnable {

	Socket socket = null;
	Robot robot = null;

	boolean continueLoop = true;

	public EventReceiver(Socket socket, Robot robot) {
		this.socket = socket;
		this.robot = robot;
		new Thread(this).start();
	}

	@Override
	public void run() {
		Scanner scanner = null;
		try {
			scanner = new Scanner(socket.getInputStream());
			while (continueLoop) {
				// recieve commands and respond accordingly

				int command = scanner.nextInt();
				switch (command) {
				case -1:
					int mask = InputEvent.getMaskForButton(scanner.nextInt());
					robot.mousePress(mask);
					break;
				case -2:
					int masktmp = InputEvent.getMaskForButton(scanner.nextInt());
					robot.mouseRelease(masktmp);
					break;
				case -3:
					robot.keyPress(scanner.nextInt());
					break;
				case -4:
					robot.keyRelease(scanner.nextInt());
					break;
				case -5:
					robot.mouseMove(scanner.nextInt(), scanner.nextInt());
					break;
				case -6:
					robot.mouseWheel(scanner.nextInt());
					break;
				}
			}
		} catch (IOException | NoSuchElementException ex) {
			this.continueLoop = false;
		}
	}

	public void stop() {
		this.continueLoop = false;
	}
}
