package Server;

import Server.checkconnection.CheckConnection;
import Server.feature.chat.ChatFormServer;
import Server.feature.filetransfer.FileTransferThread;
import Server.feature.record.ScreenRecordThread;
import Server.feature.remote.ReceiverForm;
import Server.feature.remote.RemoteDesktopThread;
import packages.ChatPackage;
import packages.FileTransferPackage;
import packages.RemoteDesktopPackage;
import packages.ScreenRecordPackage;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import javax.swing.JTable;
import javax.swing.JButton;

public class ServerIndex extends JFrame implements Runnable {

	private JPanel contentPane;
	private JTable table;
	private DefaultTableModel tableModel;
	private List<Socket> clients;

	private int mainPort = 9876;
	private int remotePort = 6996;
	private int fileTransferPort = 9669;
	private int screenRecordPort = 6999;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					new Thread(new ServerIndex()).start();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public ServerIndex() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 1026, 481);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));

		setContentPane(contentPane);
		contentPane.setLayout(null);

		tableModel = new DefaultTableModel();
		tableModel.addColumn("Device Name");
		tableModel.addColumn("IP Address");
		tableModel.addColumn("Port");

		JButton chatButton = new JButton("Nhắn tin");
		chatButton.setBounds(38, 60, 89, 23);
		contentPane.add(chatButton);
		chatButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				jButtonChatActionPerformed(e);
			}
		});

		JButton sendFileButton = new JButton("Gửi file");
		sendFileButton.setBounds(192, 60, 89, 23);
		contentPane.add(sendFileButton);
		sendFileButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				jButtonSendFileActionPerformed(e);
			}
		});

		JButton captureScreenButton = new JButton("Chụp");
		captureScreenButton.setBounds(350, 60, 89, 23);
		contentPane.add(captureScreenButton);
		captureScreenButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				jButtonCaptureActionPerformed(e);
			}
		});

		JButton screenRecordButton = new JButton("Quay");
		screenRecordButton.setBounds(546, 60, 89, 23);
		contentPane.add(screenRecordButton);
		screenRecordButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				jButtonRecordActionPerformed(e);
			}
		});

		JButton remoteButton = new JButton("Remote");
		remoteButton.setBounds(738, 60, 89, 23);
		contentPane.add(remoteButton);
		remoteButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				jButtonRemoteDesktopActionPerformed(e);
			}
		});

		table = new JTable(tableModel);

		JScrollPane scrollPane = new JScrollPane(table);
		scrollPane.setBounds(10, 113, 992, 283);
		contentPane.add(scrollPane);
		new Thread(new RemoteDesktopThread(remotePort)).start();
		new Thread(new FileTransferThread(fileTransferPort)).start();
		new Thread(new ScreenRecordThread(screenRecordPort)).start();
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		clients = new ArrayList<>();
		this.setVisible(true);
		try (ServerSocket server = new ServerSocket(mainPort)) {
			while (true) {
				Socket socket;
				try {
					socket = server.accept();
					clients.add(socket);
					this.addRow(socket);
					// new Thread(new CheckConnection(socket, this.tableModel, clients)).start();
				} catch (IOException ex) {
					ex.printStackTrace();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void jButtonRecordActionPerformed(java.awt.event.ActionEvent evt) {
		Socket client = getSelectedClient();
		if (client == null) {
			JOptionPane.showMessageDialog(null, "Bạn chưa chọn client!");
			return;
		}
		ScreenRecordPackage rdp = new ScreenRecordPackage();
		rdp.setCmd("Start");
		rdp.setMessage(String.valueOf(screenRecordPort));
		System.out.println("Sending record package to client!");
		PrintWriter outputToClient;
		try {
			outputToClient = new PrintWriter(client.getOutputStream(), true);
			outputToClient.println(rdp);
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	private void jButtonChatActionPerformed(java.awt.event.ActionEvent evt) {
		Socket client = getSelectedClient();
		if (client == null) {
			JOptionPane.showMessageDialog(null, "Bạn chưa chọn client!");
			return;
		}
		ChatPackage rdp = new ChatPackage();
		PrintWriter outputToClient;
		try {
			outputToClient = new PrintWriter(client.getOutputStream(), true);
			outputToClient.println(rdp);
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		new Thread(new ChatFormServer(client)).start();
	}

	private void jButtonCaptureActionPerformed(java.awt.event.ActionEvent evt) {
		Socket client = getSelectedClient();
		if (client == null) {
			JOptionPane.showMessageDialog(null, "Bạn chưa chọn client!");
			return;
		}
		new Thread(new Server.feature.capture.ReceiveForm(client)).start();
	}

	private void jButtonRemoteDesktopActionPerformed(java.awt.event.ActionEvent evt) {
		Socket client = getSelectedClient();
		if (client == null) {
			JOptionPane.showMessageDialog(null, "Bạn chưa chọn client!");
			return;
		}
		RemoteDesktopPackage rdp = new RemoteDesktopPackage();
		rdp.setCmd("Start");
		rdp.setMessage(String.valueOf(remotePort));
		PrintWriter outputToClient;
		try {
			outputToClient = new PrintWriter(client.getOutputStream(), true);
			outputToClient.println(rdp);
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	private void jButtonSendFileActionPerformed(java.awt.event.ActionEvent evt) {
		Socket client = getSelectedClient();
		if (client == null) {
			JOptionPane.showMessageDialog(null, "Bạn chưa chọn client!");
			return;
		}
		FileTransferPackage rdp = new FileTransferPackage();
		rdp.setCmd("Start");
		rdp.setMessage(String.valueOf(fileTransferPort));
		PrintWriter outputToClient;
		try {
			outputToClient = new PrintWriter(client.getOutputStream(), true);
			outputToClient.println(rdp);
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	public void addRow(Socket socket) {
		DefaultTableModel defaultTableModel = (DefaultTableModel) this.table.getModel();
		defaultTableModel.addRow(new Object[] { socket.getInetAddress().getHostName(),
				socket.getInetAddress().getHostAddress(), socket.getPort() });
	}

	public Socket getSelectedClient() {
		int selectedRow = table.getSelectedRow();

		if (selectedRow != -1) {
			return clients.get(selectedRow);
		}
		return null;
	}
}
