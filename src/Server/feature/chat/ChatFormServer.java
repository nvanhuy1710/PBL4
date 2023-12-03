package Server.feature.chat;

import packages.ChatPackage;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

import javax.swing.*;
import javax.swing.border.Border;


public class ChatFormServer implements Runnable{
	
	JTextField chatField;
	JButton sendButton;
	JTextArea chatLog;
	JScrollPane chatLogScroll;
	JPanel chatPanel, statusPanel, panel;
	JFrame frame;

	Socket socket;
	private boolean isContinue = true;

	public ChatFormServer(Socket socket)
	{
		this.socket = socket;
		init();
	}

	@Override
	public void run() {
		try {
			String s;
			while(isContinue)
			{
				if(socket != null) {
					BufferedReader dis = new BufferedReader(new InputStreamReader(socket.getInputStream()));
					String msg = dis.readLine();
					s = socket.getInetAddress().getHostName() + ": " + msg + "\n";
					System.out.println("Server receive: " + s + "\n" + "In thread: " + Thread.currentThread());
					if(!msg.equals("")) {
						chatLog.append(s);
					}
				}
			}
		} catch (Exception e) {
		}
	}

	void init()
	{
		frame = new JFrame("Server Chat");
		Border grayLine = BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1);
		Border blackLine = BorderFactory.createLineBorder(Color.BLACK, 1);


		statusPanel = new JPanel();
		statusPanel.setBounds(3, 3, 477, 250);
		statusPanel.setLayout(new BorderLayout(0, 0));
		chatLogScroll = new JScrollPane();
		chatLogScroll.setBorder(blackLine);
		statusPanel.add(chatLogScroll, BorderLayout.EAST);


		chatPanel = new JPanel();
		chatPanel.setBounds(7, 260, 472, 30);
		chatPanel.setLayout(new BorderLayout());
		chatField = new JTextField(36);
		chatField.setBorder(blackLine);
		chatPanel.add(chatField, BorderLayout.WEST);
		sendButton = new JButton("Gởi");
		sendButton.setPreferredSize(new Dimension(100, 0));
		sendButton.setFocusable(false);
		sendButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				System.out.println("Sending");
				ChatPackage chatPackage = new ChatPackage();
				String mes = chatField.getText();
				chatPackage.setMessage(mes);
				if(mes.equals("")) return;
				chatField.setText("");
				PrintWriter outputToClient;
				chatLog.append("You: " + mes + "\n");
				try {
					outputToClient = new PrintWriter(socket.getOutputStream(), true);
					outputToClient.println(chatPackage);
				} catch (IOException ex) {
				}
				System.out.println("Sent");
			}
		});
		chatPanel.add(sendButton, BorderLayout.EAST);

		panel = new JPanel();
		panel.setBounds(2, 2, 485, 300);
		panel.setLayout(null);
		panel.setBorder(grayLine);

		panel.add(statusPanel);
		chatLog = new JTextArea(15, 47);
		JScrollPane scrollPane = new JScrollPane(chatLog);
		statusPanel.add(scrollPane, BorderLayout.CENTER);
		chatLog.setEditable(false);
		panel.add(chatPanel);

		frame.getContentPane().add(panel);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.setSize(505, 350);
		frame.setResizable(true);
		frame.getContentPane().setLayout(null);
		frame.setLocation(200, 300);
		frame.setVisible(true);

		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				isContinue = false;
				socket = null;
			}
		});
	}
}
