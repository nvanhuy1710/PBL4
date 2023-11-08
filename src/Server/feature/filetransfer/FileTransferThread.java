package Server.feature.filetransfer;

import Server.feature.remote.ReceiverForm;

import java.awt.*;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class FileTransferThread implements Runnable{
    private int port;

    public FileTransferThread(int port) {
        this.port = port;
    }

    @Override
    public void run() {
        try {
            final ServerSocket server = new ServerSocket(port);

            while (true) {
                Socket socket;
                try {
                    socket = server.accept();
                    new Thread(new FileTransferForm(socket)).start();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
