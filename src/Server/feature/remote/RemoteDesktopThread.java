package Server.feature.remote;

import Client.feature.remote.RemoteDesktopHandler;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class RemoteDesktopThread implements Runnable{

    private int port;

    public RemoteDesktopThread(int port) {
        this.port = port;
    }
    @Override
    public void run() {
        try {
            final ServerSocket server = new ServerSocket(port);
            // Phục vụ nhiều client
            while (true) {
                Socket socket;
                try {
                    socket = server.accept();
                    DataInputStream disTmp = null;
                    String w = null;
                    String h = null;
                    try {
                        disTmp = new DataInputStream(socket.getInputStream());
                    } catch (IOException ioException) {
                        ioException.printStackTrace();
                    }
                    try {
                        w = disTmp.readUTF();
                        h = disTmp.readUTF();
                    } catch (IOException ioException) {
                        ioException.printStackTrace();
                    }
                    new Thread(new ReceiverForm(socket, w, h)).start();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
