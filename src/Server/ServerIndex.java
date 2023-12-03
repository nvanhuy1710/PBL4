package Server;

import Server.feature.chat.ChatFormServer;
import Server.feature.filetransfer.FileTransferThread;
import Server.feature.message.SendMessageForm;
import Server.feature.record.ScreenRecordThread;
import Server.feature.remote.RemoteDesktopThread;
import packages.FileTransferPackage;
import packages.RemoteDesktopPackage;
import packages.ScreenRecordPackage;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;

public class ServerIndex extends JFrame implements Runnable {

	private JPanel contentPane;
	private JTable table;
	private DefaultTableModel tableModel;
	private static List<Socket> clients;
	private JTextArea textArea;

	private int mainPort = 9876;
	private int remotePort = 6996;
	private int fileTransferPort = 9669;
	private int screenRecordPort = 6999;
	private Timer timerUpdateListSocket;
	private long timeUpdateTable = 2;

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

		init();

		new Thread(new RemoteDesktopThread(remotePort)).start();
		new Thread(new FileTransferThread(fileTransferPort)).start();
		new Thread(new ScreenRecordThread(screenRecordPort)).start();

		logEvent("Đang lắng nghe kết nối");

		checkConnection();
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
					logEvent(socket.getInetAddress().getHostName() + " đã kết nối");
				} catch (IOException ex) {
					ex.printStackTrace();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void jButtonMessageActionPerformed(java.awt.event.ActionEvent evt) {
		new SendMessageForm(clients);
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

	public void logEvent(String message) {
		textArea.append(message + "\n");
	}

	private void checkConnection() {
		timerUpdateListSocket = new Timer();
		timerUpdateListSocket.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				if (clients != null && clients.size() != 0) {
					try {
						for (Socket client : clients) {
							try {
								DataOutputStream dos = new DataOutputStream(client.getOutputStream());
								dos.writeUTF("");
								dos.flush();
							} catch (IOException e) {
								clients.remove(client);
								int rowCount = tableModel.getRowCount();

								for (int i = 0; i < rowCount; i++) {
									String hostName = (String) tableModel.getValueAt(i, 0);
									String hostAddress = (String) tableModel.getValueAt(i, 1);
									if (hostName.equals(client.getInetAddress().getHostName())
											&& hostAddress.equals(client.getInetAddress().getHostAddress())) {
										tableModel.removeRow(i);
									}
								}
								textArea.append(client.getInetAddress().getHostName() + " đã ngắt kết nối\n");
							}
						}
					}catch (Exception e) {
						return;
					}

				}
			}
		}, timeUpdateTable * 1000, timeUpdateTable * 1000);
	}

	private void init() {
		Color color = Color.decode("#DDDDDD");
		Color enterColor = Color.decode("#E1E1E1");
		Color buttonColor = Color.decode("#8AC4FF");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 810, 431);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setBackground(color);

		setContentPane(contentPane);
		contentPane.setLayout(null);

		tableModel = new DefaultTableModel() {
			@Override
			public boolean isCellEditable(int row, int col) {
				return false;
			}
		};
		tableModel.addColumn("Device Name");
		tableModel.addColumn("IP Address");
		tableModel.addColumn("Port");

		table = new JTable(tableModel);
		DefaultTableCellRenderer headerRenderer = new DefaultTableCellRenderer();
		headerRenderer.setBackground(Color.decode("#60DDFF"));
		headerRenderer.setHorizontalAlignment(JLabel.CENTER);
		headerRenderer.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		table.getColumnModel().getColumn(0).setHeaderRenderer(headerRenderer);
		table.getColumnModel().getColumn(1).setHeaderRenderer(headerRenderer);
		table.getColumnModel().getColumn(2).setHeaderRenderer(headerRenderer);
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		JScrollPane scrollPane = new JScrollPane(table);
		scrollPane.setBounds(180, 78, 411, 283);
		contentPane.add(scrollPane);

		JLabel lblNewLabel = new JLabel("Danh sách các máy đã kết nối");
		lblNewLabel.setHorizontalAlignment(JLabel.CENTER);
		Font lblNewLabelFont = lblNewLabel.getFont().deriveFont(Font.BOLD, 16);
		lblNewLabel.setFont(lblNewLabelFont);
		lblNewLabel.setBounds(250, 37, 286, 40);
		contentPane.add(lblNewLabel);

		Font functionFont = Font.getFont(String.valueOf(Font.PLAIN));

		textArea = new JTextArea();
		JScrollPane scrollPane1 = new JScrollPane(textArea);
		scrollPane1.setBounds(594, 78, 193, 283);
		textArea.setEditable(false);
		contentPane.add(scrollPane1);

		JLabel lblThngBo = new JLabel("Thông báo");
		lblThngBo.setHorizontalAlignment(SwingConstants.CENTER);
		lblThngBo.setFont(new Font("Tahoma", Font.BOLD, 16));
		lblThngBo.setBounds(594, 37, 187, 40);
		contentPane.add(lblThngBo);

		JPanel panel = new JPanel();
		panel.setBounds(10, 78, 162, 283);
		contentPane.add(panel);
		panel.setLayout(null);

		JButton messageButton = new JButton("Thông điệp");
		messageButton.setBounds(0, 32, 161, 32);
		panel.add(messageButton);
		messageButton.setBackground(buttonColor);
		messageButton.setBorder(new LineBorder(Color.BLACK, 1, true));
		messageButton.setFocusPainted(false);
		messageButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icon/message.png")));

		JButton chatButton = new JButton("Nhắn tin");
		chatButton.setBounds(0, 0, 161, 32);
		panel.add(chatButton);
		chatButton.setBackground(buttonColor);
		chatButton.setBorder(new LineBorder(Color.BLACK, 1, true));
		chatButton.setFocusPainted(false);
		chatButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icon/chat.png")));

		JButton sendFileButton = new JButton("Gởi file");
		sendFileButton.setBounds(0, 64, 161, 32);
		panel.add(sendFileButton);
		sendFileButton.setBackground(buttonColor);
		sendFileButton.setBorder(new LineBorder(Color.BLACK, 1, true));
		sendFileButton.setFocusPainted(false);
		sendFileButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icon/send-file.png")));

		JButton captureScreenButton = new JButton("Chụp hình");
		captureScreenButton.setBounds(0, 96, 161, 32);
		panel.add(captureScreenButton);
		captureScreenButton.setBackground(buttonColor);
		captureScreenButton.setBorder(new LineBorder(Color.BLACK, 1, true));
		captureScreenButton.setFocusPainted(false);
		captureScreenButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icon/capture.png")));

		JButton screenRecordButton = new JButton("Quay màn hình");
		screenRecordButton.setBounds(0, 128, 161, 32);
		panel.add(screenRecordButton);
		screenRecordButton.setBackground(buttonColor);
		screenRecordButton.setBorder(new LineBorder(Color.BLACK, 1, true));
		screenRecordButton.setFocusPainted(false);
		screenRecordButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icon/recording.png")));

		JButton remoteButton = new JButton("Điều khiển");
		remoteButton.setBounds(0, 160, 161, 32);
		panel.add(remoteButton);
		remoteButton.setBackground(buttonColor);
		remoteButton.setBorder(new LineBorder(Color.BLACK, 1, true));
		remoteButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icon/remote.png")));

		JButton quitButton = new JButton("Thoát");
		quitButton.setBounds(0, 192, 161, 32);
		panel.add(quitButton);
		quitButton.setBackground(buttonColor);
		quitButton.setBorder(new LineBorder(Color.BLACK, 1, true));
		quitButton.setFocusPainted(false);
		quitButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icon/quit.png")));

		JPanel panel_1 = new JPanel();
		panel_1.setBackground(Color.decode("#03A7FF"));
		panel_1.setBounds(10, 11, 771, 27);
		contentPane.add(panel_1);
		panel_1.setLayout(null);

		JButton btnNewButton = new JButton("File");
		btnNewButton.setBorderPainted(false);
		btnNewButton.setFocusPainted(false);
		btnNewButton.setForeground(Color.WHITE);
		btnNewButton.setBackground(panel_1.getBackground());
		btnNewButton.setBounds(0, 0, 72, 27);
		panel_1.add(btnNewButton);
		btnNewButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				btnNewButton.setBackground(Color.decode("#0351DA"));
			}

			@Override
			public void mouseExited(MouseEvent e) {
				btnNewButton.setBackground(panel_1.getBackground());
			}
		});

		JButton btnEdit = new JButton("Edit");
		btnEdit.setBorderPainted(false);
		btnEdit.setFocusPainted(false);
		btnEdit.setForeground(Color.WHITE);
		btnEdit.setBackground(panel_1.getBackground());
		btnEdit.setBounds(69, 0, 72, 27);
		panel_1.add(btnEdit);
		btnEdit.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				btnEdit.setBackground(Color.decode("#0351DA"));
			}

			@Override
			public void mouseExited(MouseEvent e) {
				btnEdit.setBackground(panel_1.getBackground());
			}
		});

		JButton btnNewButton_1_1 = new JButton("Help");
		btnNewButton_1_1.setBorderPainted(false);
		btnNewButton_1_1.setFocusPainted(false);
		btnNewButton_1_1.setForeground(Color.WHITE);
		btnNewButton_1_1.setBackground(panel_1.getBackground());
		btnNewButton_1_1.setBounds(139, 0, 72, 27);
		panel_1.add(btnNewButton_1_1);
		btnNewButton_1_1.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				btnNewButton_1_1.setBackground(Color.decode("#0351DA"));
			}

			@Override
			public void mouseExited(MouseEvent e) {
				btnNewButton_1_1.setBackground(panel_1.getBackground());
			}
		});

		quitButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				quitButton.setBackground(enterColor);
			}

			@Override
			public void mouseExited(MouseEvent e) {
				quitButton.setBackground(buttonColor);
			}
		});
		quitButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});
		remoteButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				remoteButton.setBackground(enterColor);
			}

			@Override
			public void mouseExited(MouseEvent e) {
				remoteButton.setBackground(buttonColor);
			}
		});
		remoteButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				jButtonRemoteDesktopActionPerformed(e);
			}
		});
		screenRecordButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				screenRecordButton.setBackground(enterColor);
			}

			@Override
			public void mouseExited(MouseEvent e) {
				screenRecordButton.setBackground(buttonColor);
			}
		});
		screenRecordButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				jButtonRecordActionPerformed(e);
			}
		});
		captureScreenButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				captureScreenButton.setBackground(enterColor);
			}

			@Override
			public void mouseExited(MouseEvent e) {
				captureScreenButton.setBackground(buttonColor);
			}
		});
		captureScreenButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				jButtonCaptureActionPerformed(e);
			}
		});
		sendFileButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				sendFileButton.setBackground(enterColor);
			}

			@Override
			public void mouseExited(MouseEvent e) {
				sendFileButton.setBackground(buttonColor);
			}
		});
		sendFileButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				jButtonSendFileActionPerformed(e);
			}
		});
		chatButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				chatButton.setBackground(enterColor);
			}

			@Override
			public void mouseExited(MouseEvent e) {
				chatButton.setBackground(buttonColor);
			}
		});
		chatButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				jButtonChatActionPerformed(e);
			}
		});
		messageButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				messageButton.setBackground(enterColor);
			}

			@Override
			public void mouseExited(MouseEvent e) {
				messageButton.setBackground(buttonColor);
			}
		});
		messageButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				jButtonMessageActionPerformed(e);
			}
		});
	}
}
