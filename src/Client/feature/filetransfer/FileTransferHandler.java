package Client.feature.filetransfer;

import Client.feature.remote.EventReceiver;
import Client.feature.remote.ScreenSender;
import packages.GeneralPackage;

import java.awt.*;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class FileTransferHandler {

    private String ipServer;

    public FileTransferHandler(String ipServer) {
        this.ipServer = ipServer;
    }

    public void handleFileTransfer(GeneralPackage pkTin) {

        int port = Integer.parseInt(pkTin.getMessage());

        try {
            final Socket socketReceiveFile = new Socket(ipServer, port);
            new Thread(new FileReceive(socketReceiveFile)).start();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
