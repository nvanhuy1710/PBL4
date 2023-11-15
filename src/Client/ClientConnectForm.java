package Client;

import Client.feature.chat.ChatFormClient;
import Client.feature.filetransfer.FileTransferHandler;
import Client.feature.record.ConnectRecord;
import Client.feature.remote.EventReceiver;
import Client.feature.remote.RemoteDesktopHandler;
import Client.feature.remote.ScreenSender;
import Client.feature.screenshot.ScreenshotSend;
import packages.GeneralPackage;
import packages.PackageType;

import java.awt.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import java.awt.event.ActionListener;
import java.io.*;
import java.net.Socket;
import java.awt.event.ActionEvent;
import java.net.UnknownHostException;

public class ClientConnectForm extends JFrame implements Runnable {

	private JPanel contentPane;
	private JTextField textField;
	private JButton connectButton;

	Socket socketFromServer;
	String ipServer;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		new Thread(new ClientConnectForm()).start();
	}

	/**
	 * Create the frame.
	 */
	public ClientConnectForm() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 665, 133);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));

		setContentPane(contentPane);
		contentPane.setLayout(null);

		textField = new JTextField();
		textField.setBounds(222, 50, 182, 22);
		contentPane.add(textField);
		textField.setColumns(10);

		connectButton = new JButton("Connect");
		connectButton.setBounds(434, 50, 89, 23);
		connectButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				connectAction(e);
			}
		});
		contentPane.add(connectButton);

		JLabel lblNewLabel = new JLabel("Input IP Server Address");
		lblNewLabel.setBounds(66, 54, 117, 14);
		contentPane.add(lblNewLabel);

		JLabel lblNewLabel_1 = new JLabel("Connect to server");
		lblNewLabel_1.setFont(lblNewLabel_1.getFont().deriveFont(lblNewLabel_1.getFont().getSize() + 6f));
		lblNewLabel_1.setBounds(222, 11, 182, 22);
		contentPane.add(lblNewLabel_1);

		this.setVisible(true);
	}

	private void connectAction(java.awt.event.ActionEvent evt) {
		ipServer = textField.getText();
		connectButton.setEnabled(false);
		try {
			socketFromServer = new Socket(ipServer, 9876);
		} catch (Exception ex) {
			JOptionPane.showMessageDialog(null, "Connect Failure!");
			return;
		}
		JOptionPane.showMessageDialog(null, "Connect Success!");
	}

	@Override
	public void run() {
		while (true) {
			try {
				BufferedReader dis = new BufferedReader(new InputStreamReader(socketFromServer.getInputStream()));
				String msg = dis.readLine();
				System.out.println(msg);
				if (msg != null && !msg.isEmpty()) {
					packageHandle(msg);
				}
			} catch (Exception e) {
				// e.printStackTrace();
			}
		}
	}

	private void packageHandle(String msg) throws UnknownHostException, IOException {
		GeneralPackage pkTin = new GeneralPackage();
		pkTin.phanTichMessage(msg);
		if (pkTin.getType() == PackageType.REMOTE) {
			new RemoteDesktopHandler(ipServer).handleRemoteDesktop(pkTin);
		}
		if (pkTin.getType() == PackageType.FILETRANSFER) {
			new FileTransferHandler(ipServer).handleFileTransfer(pkTin);
		} else if (pkTin.getType() == PackageType.SCREENSHOT) {
			new Thread(new ScreenshotSend(socketFromServer)).start();
		} else if (pkTin.getType() == PackageType.TRACKING) {
			new ConnectRecord(ipServer).Connect(pkTin);
		}
		else if (pkTin.getType() == PackageType.CHAT) {
			new Thread(new ChatFormClient(socketFromServer)).start();
		}
		else if (pkTin.getType() == PackageType.MESSAGE) {
			JOptionPane.showMessageDialog(null, pkTin.getMessage(), "Thông điệp từ máy chủ", JOptionPane.INFORMATION_MESSAGE);
		}
		System.err.println(pkTin.toString());
	}
}
