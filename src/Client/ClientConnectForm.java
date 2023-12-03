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
import java.net.InetAddress;
import java.net.Socket;
import java.awt.event.ActionEvent;
import java.net.UnknownHostException;

public class ClientConnectForm extends JFrame implements Runnable {

	private final JTextField textField;
	private final JButton connectButton;
	private final JLabel lblStatus;

	Socket socketFromServer;
	String ipServer;
	ChatFormClient chatForm;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) throws UnknownHostException {
		new Thread(new ClientConnectForm()).start();
	}

	/**
	 * Create the frame.
	 */
	public ClientConnectForm() throws UnknownHostException {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 340, 267);
		JPanel contentPane = new JPanel();
		contentPane.setBackground(new Color(192, 192, 192));
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));

		setContentPane(contentPane);
		contentPane.setLayout(null);

		textField = new JTextField();
		textField.setBounds(112, 50, 182, 22);
		contentPane.add(textField);
		textField.setColumns(10);

		connectButton = new JButton("Kết nối");
		connectButton.setBackground(new Color(155, 225, 255));
		connectButton.setForeground(new Color(0, 0, 0));
		connectButton.setBounds(30, 158, 105, 24);
		connectButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				connectAction(e);
			}
		});
		contentPane.add(connectButton);

		JLabel lblNewLabel = new JLabel("Nhập địa chỉ IP");
		lblNewLabel.setBounds(20, 54, 82, 14);
		contentPane.add(lblNewLabel);

		JLabel lblNewLabel_1 = new JLabel("Kết nối Server");
		lblNewLabel_1.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel_1.setFont(lblNewLabel_1.getFont().deriveFont(lblNewLabel_1.getFont().getSize() + 6f));
		lblNewLabel_1.setBounds(66, 11, 182, 22);
		contentPane.add(lblNewLabel_1);

		JLabel lblNewLabel_2 = new JLabel("Tên máy");
		lblNewLabel_2.setBounds(20, 83, 82, 14);
		contentPane.add(lblNewLabel_2);

		JLabel lblNameDevice = new JLabel();
		lblNameDevice.setHorizontalAlignment(SwingConstants.CENTER);
		lblNameDevice.setText(InetAddress.getLocalHost().getHostName());
		lblNameDevice.setBounds(112, 83, 182, 14);
		contentPane.add(lblNameDevice);

		JLabel lblNewLabel_4 = new JLabel("Địa chỉ IP");
		lblNewLabel_4.setBounds(20, 108, 82, 14);
		contentPane.add(lblNewLabel_4);

		JLabel lblIP = new JLabel();
		lblIP.setHorizontalAlignment(SwingConstants.CENTER);
		lblIP.setText(InetAddress.getLocalHost().getHostAddress());
		lblIP.setBounds(112, 108, 182, 14);
		contentPane.add(lblIP);

		JLabel lblNewLabel_8 = new JLabel("Trạng thái");
		lblNewLabel_8.setBounds(20, 133, 82, 14);
		contentPane.add(lblNewLabel_8);

		lblStatus = new JLabel();
		lblStatus.setHorizontalAlignment(SwingConstants.CENTER);
		lblStatus.setBounds(112, 133, 182, 14);
		lblStatus.setText("Đang chờ kết nối...");
		contentPane.add(lblStatus);

		JButton btnNewButton = new JButton("Thoát");
		btnNewButton.setBackground(new Color(255, 77, 77));
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});
		btnNewButton.setBounds(178, 159, 105, 24);
		contentPane.add(btnNewButton);

		this.setVisible(true);
	}

	private void connectAction(java.awt.event.ActionEvent evt) {
		ipServer = textField.getText();
		try {
			socketFromServer = new Socket(ipServer, 9876);
			lblStatus.setText("Đã kết nối Server");
			connectButton.setEnabled(false);
		} catch (Exception ex) {
			JOptionPane.showMessageDialog(null, "Kết nối thất bại!");
			return;
		}
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
			if (chatForm == null) {
				chatForm = new ChatFormClient(socketFromServer);
			}
			if (!chatForm.isVisible()) {
				chatForm.clearMessage();
				chatForm.setVisible(true);
			}
			chatForm.sendRedunant();
			chatForm.receiveMessage(pkTin.getMessage());
		}
		else if (pkTin.getType() == PackageType.MESSAGE) {
			JOptionPane.showMessageDialog(null, pkTin.getMessage(), "Thông điệp từ máy chủ", JOptionPane.INFORMATION_MESSAGE);
		}
		System.err.println(pkTin.toString());
	}
}
