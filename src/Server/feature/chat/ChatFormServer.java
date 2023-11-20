package Server.feature.chat;

import packages.ChatPackage;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.Border;


public class ChatFormServer implements ActionListener, Runnable{
	
	JTextField chatField;
	JButton sendButton;
	JTextArea chatLog;
	JScrollPane chatLogScroll;
	JPanel chatPanel, statusPanel, panel;

	Socket socket;
	DataOutputStream output;
	DataInputStream input;
	
	public ChatFormServer(Socket socket)
	{
		this.socket = socket;
		init();
	}
	

	@Override
	public void run() {
		try {
			String s;
			input = new DataInputStream(socket.getInputStream());
			while(true)
			{
				if(socket != null) {
					s = "Client: " + input.readUTF() + "\n";
					System.out.println("Server receive: " + s + "\n");
					chatLog.append(s);
				}
			}
		} catch (Exception e) {
		}
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == sendButton) {
			System.out.println("Sending");
			ChatPackage chatPackage = new ChatPackage();
			String mes = chatField.getText();
			chatPackage.setMessage(mes);
			chatField.setText("");
			PrintWriter outputToClient;
			try {
				outputToClient = new PrintWriter(socket.getOutputStream(), true);
				outputToClient.println(chatPackage);
			} catch (IOException ex) {
				ex.printStackTrace();
			}
			System.out.println("Sent");
		}
	}
	
	void init()
	{
		JFrame frame = new JFrame("Server Chat");
		Border grayLine = BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1);
		Border blackLine = BorderFactory.createLineBorder(Color.BLACK, 1);
		
		
		statusPanel = new JPanel();
		statusPanel.setBounds(3, 3, 477, 250);
		chatLog = new JTextArea(15, 47);	
		chatLog.setEditable(false);
		chatLogScroll = new JScrollPane(chatLog);
		chatLogScroll.setBorder(blackLine);
		statusPanel.add(chatLogScroll);	
		
		
		chatPanel = new JPanel();
		chatPanel.setBounds(7, 260, 472, 30);
		chatPanel.setLayout(new BorderLayout());
		chatField = new JTextField(36);
		chatField.setBorder(blackLine);
		chatPanel.add(chatField, BorderLayout.WEST);
		sendButton = new JButton("Send");
		sendButton.setPreferredSize(new Dimension(100, 0));
		sendButton.setFocusable(false);	
		sendButton.addActionListener(this);
		chatPanel.add(sendButton, BorderLayout.EAST);
		
		panel = new JPanel();
		panel.setBounds(2, 2, 485, 300);
		panel.setLayout(null);
		panel.setBorder(grayLine);

		panel.add(statusPanel);
		panel.add(chatPanel);
		
		frame.add(panel);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.setSize(505, 350);
		frame.setResizable(true);
		frame.setLayout(null);
		frame.setLocation(200, 300);
		frame.setVisible(true);
	}
}
