package Client.feature.remote;

import java.awt.*;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

public class EventReceiver implements Runnable{

    Socket socket = null;
    Robot robot = null;

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
            while (true) {
                //recieve commands and respond accordingly

                int command = scanner.nextInt();
                switch (command) {
                    case -1:
                        robot.mousePress(scanner.nextInt());
                        break;
                    case -2:
                        robot.mouseRelease(scanner.nextInt());
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
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
