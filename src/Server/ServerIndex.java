package Server;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import javax.swing.JTable;
import javax.swing.JButton;

public class ServerIndex extends JFrame implements Runnable{
	
	private JPanel contentPane;
	private JTable table;
    private DefaultTableModel tableModel;

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
		
		JButton btnNewButton = new JButton("New button");
		btnNewButton.setBounds(38, 60, 89, 23);
		contentPane.add(btnNewButton);
		
		JButton btnNewButton_1 = new JButton("New button");
		btnNewButton_1.setBounds(192, 60, 89, 23);
		contentPane.add(btnNewButton_1);

		JButton btnNewButton_2 = new JButton("New button");
		btnNewButton_2.setBounds(350, 60, 89, 23);
		contentPane.add(btnNewButton_2);
		
		JButton btnNewButton_3 = new JButton("New button");
		btnNewButton_3.setBounds(546, 60, 89, 23);
		contentPane.add(btnNewButton_3);
		
		JButton btnNewButton_4 = new JButton("New button");
		btnNewButton_4.setBounds(738, 60, 89, 23);
		contentPane.add(btnNewButton_4);
		
		table = new JTable(tableModel);
		
		JScrollPane scrollPane = new JScrollPane(table);
		scrollPane.setBounds(10, 113, 992, 283);
		contentPane.add(scrollPane);
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub			
		List<Socket> clients = new ArrayList<>();		
		this.setVisible(true);
		try(ServerSocket server = new ServerSocket(8888);) {
			while (true) {
			  	Socket socket;
			    try {
			        socket = server.accept();
			        clients.add(socket);
			        this.addRow(socket);
			    } catch (IOException ex) {
			        ex.printStackTrace();
			    }
			}	
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void addRow(Socket socket) {
		DefaultTableModel defaultTableModel = (DefaultTableModel) this.table.getModel();
		defaultTableModel.addRow(new Object[] {socket.getInetAddress().getHostName(), socket.getInetAddress().getHostAddress(), socket.getPort()});
	}
}
