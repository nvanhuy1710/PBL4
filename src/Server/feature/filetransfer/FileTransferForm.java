package Server.feature.filetransfer;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.Socket;

public class FileTransferForm extends JFrame implements Runnable {

	Socket socket;

	JFileChooser chooser;
	private JPanel contentPane;
	private JTextField textField;
	private JButton findButton;
	private JButton sendButton;
	private JButton cancelButton;

	public FileTransferForm(Socket socket) {
		Color bkColor = Color.decode("#B9B9B9");
		this.socket = socket;
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 352, 148);
		contentPane = new JPanel();
		contentPane.setForeground(new Color(255, 128, 64));
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setBackground(bkColor);

		setContentPane(contentPane);
		contentPane.setLayout(null);

		textField = new JTextField();
		textField.setEditable(false);
		textField.setBounds(10, 33, 240, 22);
		contentPane.add(textField);
		textField.setColumns(10);

		findButton = new JButton("Chọn");
		findButton.setFocusPainted(false);
		findButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				findFileEvent(e);
			}
		});
		findButton.setFont(new Font("Tahoma", Font.PLAIN, 16));
		findButton.setBounds(260, 33, 70, 22);
		contentPane.add(findButton);

		sendButton = new JButton("Gửi");
		sendButton.setForeground(Color.WHITE);
		sendButton.setBackground(Color.decode("#BFFAB8"));
		sendButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icon/checked.png")));
		sendButton.setFont(new Font("Tahoma", Font.PLAIN, 16));
		sendButton.setForeground(new Color(0, 0, 0));
		sendButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				sendFileEvent(e);
			}
		});
		sendButton.setBounds(30, 73, 80, 29);
		contentPane.add(sendButton);

		cancelButton = new JButton("Hủy");
		cancelButton.setForeground(Color.WHITE);
		cancelButton.setBackground(Color.decode("#C9CAC9"));
		cancelButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icon/cancel.png")));
		cancelButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});
		cancelButton.setFont(new Font("Tahoma", Font.PLAIN, 16));
		cancelButton.setForeground(new Color(0, 0, 0));
		cancelButton.setBounds(179, 73, 89, 29);
		contentPane.add(cancelButton);

		JLabel lblNewLabel = new JLabel("Chọn File");
		lblNewLabel.setHorizontalAlignment(JLabel.CENTER);
		lblNewLabel.setForeground(new Color(0, 0, 0));
		lblNewLabel.setFont(new Font("Times New Roman", Font.BOLD, 20));
		lblNewLabel.setBounds(66, 0, 152, 41);
		contentPane.add(lblNewLabel);
		this.setVisible(true);
	}

	private void findFileEvent(ActionEvent evt) {
		chooser = new JFileChooser();
		int status = chooser.showOpenDialog(null);
		if (status == JFileChooser.APPROVE_OPTION) {
			File f = chooser.getSelectedFile();
			textField.setText(f.getPath());
		}
	}

	private void sendFileEvent(ActionEvent evt) {
		try {
			if (textField.getText().isEmpty()) {
				JOptionPane.showMessageDialog(null, "Bạn chưa chọn tập tin để gởi!");
				return;
			}
			File myFile = new File(textField.getText());
			byte[] mybytearray = new byte[(int) myFile.length()];
			FileInputStream fis = new FileInputStream(myFile);
			BufferedInputStream bis = new BufferedInputStream(fis);

			DataInputStream dis = new DataInputStream(bis);
			dis.readFully(mybytearray, 0, mybytearray.length);

			OutputStream os = socket.getOutputStream();

			DataOutputStream dos = new DataOutputStream(os);
			String filename = chooser.getSelectedFile().getName();
			dos.writeUTF(filename);
			dos.writeLong(mybytearray.length);
			dos.write(mybytearray, 0, mybytearray.length);
			dos.flush();
			socket.close();
			dispose();
		} catch (Exception ex) {
		}
	}

	@Override
	public void run() {

	}
}
