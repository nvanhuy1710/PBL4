package Client.feature.chat;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.Border;


public class ChatFormClient extends JFrame implements ActionListener{
	JTextField chatField;
	JButton sendButton;
	JTextArea chatLog;
	JScrollPane chatLogScroll;
	JPanel chatPanel, statusPanel, panel;
	
	Socket socket;
	DataOutputStream output;
	DataInputStream input;
	
	public ChatFormClient(Socket socket)
	{
		this.socket = socket;
		init();
	}

	public void receiveMessage(String body) {
		String s = "Server: " + body + "\n";
		System.out.println("Client receive: " + s + "\n");
		chatLog.append(s);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == sendButton) {
			try {
				output = new DataOutputStream(socket.getOutputStream());
				output.writeUTF(chatField.getText());
				output.flush();
				chatField.setText("");
			} catch (Exception e2) {
			}
		}
	}
	
	private void init() {
		this.setName("Client chat");
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
		
		this.add(panel);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setSize(505, 350);
		this.setResizable(true);
		this.setLayout(null);
		this.setLocation(700, 300);;
		this.setVisible(true);
	}
}
