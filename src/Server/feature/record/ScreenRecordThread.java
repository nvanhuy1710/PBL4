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

public class ScreenRecordThread extends JFrame implements Runnable{
  private int port;
  private JPanel contentPane;

	public ScreenRecordThread(int tr) {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 80, 1000, 600);
        this.setLayout(new BorderLayout());
        contentPane = new JPanel();
        this.add(contentPane, BorderLayout.CENTER);
        this.port = tr;
        this.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent evt) {
            	  Thread.currentThread().interrupt();           
            }
        });
	}
	
	@Override
	public void run() {
		setLocationRelativeTo (null);
	    setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    this.setVisible(true);
        try {
            final ServerSocket server = new ServerSocket(port);
            System.out.println("da mo cong "+port);
            while (true) {
                Socket socket;
                try {
                    socket = server.accept();
                    System.out.println("da ket noi vs record");
                    InputStream  is = socket.getInputStream();
                    
                    new Thread(new ScreenReceiver(is, contentPane)).start();
                  
//                    new Thread(new ReceiveForm(socket)).start();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    
		
	}
	
	

}
