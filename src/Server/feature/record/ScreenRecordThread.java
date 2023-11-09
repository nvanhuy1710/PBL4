package Server.feature.record;

import java.awt.BorderLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import Server.feature.capture.ReceiveForm;
import Server.feature.remote.ReceiverForm;

public class ScreenRecordThread implements Runnable {
	private int port;
	private JPanel contentPane;

	public ScreenRecordThread(int tr) {
		this.port = tr;
	}

	@Override
	public void run() {
		try {
			final ServerSocket server = new ServerSocket(port);
			System.out.println("da mo cong " + port);
			while (true) {
				Socket socket;
				try {
					socket = server.accept();
					System.out.println("da ket noi vs record");
					new Thread(new ScreenRecordForm(socket)).start();
				} catch (IOException ex) {
					ex.printStackTrace();
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}

}
