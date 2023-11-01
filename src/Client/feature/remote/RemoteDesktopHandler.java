package Client.feature.remote;

import packages.GeneralPackage;

import java.awt.*;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class RemoteDesktopHandler {

    private String ipServer;

    public RemoteDesktopHandler(String ipServer) {
        this.ipServer = ipServer;
    }
    public void xuLyRemoteDesktop(GeneralPackage pkTin) {
        int port = Integer.parseInt(pkTin.getMessage().toString());
        // Dùng để xử lý màn hình
        Robot robot;
        // Dùng dể tính độ phân giải và kích thước màn hình cho client
        Rectangle rectangle;
        try {
            // Tạo socket nhận remote với port đã gởi qua
            final Socket socketRemote =
                    new Socket(ipServer, port);
            try {
                // Lấy màn hình mặc định của hệ thống
                GraphicsEnvironment gEnv = GraphicsEnvironment.getLocalGraphicsEnvironment();
                GraphicsDevice gDev = gEnv.getDefaultScreenDevice();

                // Lấy dimension màn hình
                Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
                String width = "" + dim.getWidth();
                String height = "" + dim.getHeight();
                rectangle = new Rectangle(dim);

                // Chuẩn bị robot thao tác màn hình
                robot = new Robot(gDev);
                DataOutputStream dos = new DataOutputStream(socketRemote.getOutputStream());
                dos.writeUTF(width);
                dos.writeUTF(height);
                dos.flush();
                // Gởi màn hình
                ScreenSender screenSender = new ScreenSender(socketRemote, robot, rectangle);
                new EventReceiver(socketRemote, robot);
                new Thread(screenSender).start();
            } catch (Exception ex) {
                ex.printStackTrace();
            }

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
