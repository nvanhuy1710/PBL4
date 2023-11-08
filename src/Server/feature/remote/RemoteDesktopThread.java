package Server.feature.remote;

import Client.feature.remote.RemoteDesktopHandler;

import java.awt.*;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class RemoteDesktopThread implements Runnable {

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
					ObjectInputStream ois = null;
					Rectangle clientScreenDim = null;
					try {
						ois = new ObjectInputStream(socket.getInputStream());
					} catch (IOException ioException) {
						ioException.printStackTrace();
					}
					try {
						clientScreenDim = (Rectangle) ois.readObject();
					} catch (IOException ioException) {
						ioException.printStackTrace();
					}
					new Thread(new ReceiverForm(socket, clientScreenDim)).start();
				} catch (IOException ex) {
					ex.printStackTrace();
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}
