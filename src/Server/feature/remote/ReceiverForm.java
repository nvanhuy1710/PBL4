package Server.feature.remote;

import javax.swing.*;
import java.awt.*;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.PrintWriter;
import java.net.Socket;

public class ReceiverForm extends JFrame implements Runnable {

	private JPanel contentPane;

	private Socket socket;
	private ScreenReceiver screenReceiver;
	private EventSender eventSender;

	private Rectangle clientScreenDim = null;

	/**
	 * Create the frame.
	 */
	public ReceiverForm(Socket socket, Rectangle clientScreenDim) {
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setPreferredSize(new java.awt.Dimension(800, 600));
		addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowClosing(java.awt.event.WindowEvent evt) {
				closeForm(evt);
			}
		});
		contentPane = new JPanel();
		contentPane.setPreferredSize(new java.awt.Dimension(1366, 768));

		this.getContentPane().add(contentPane, BorderLayout.CENTER);

		this.clientScreenDim = clientScreenDim;
		this.socket = socket;

		pack();
	}

	@Override
	public void run() {
		ObjectInputStream ois = null;
		InputStream is = null;
		PrintWriter pw = null;
		// Rectangle clientScreenDim = null;
		try {
			pw = new PrintWriter(socket.getOutputStream());
			is = socket.getInputStream();
			// ois = new ObjectInputStream(socket.getInputStream());
			this.setVisible(true);
			// clientScreenDim = (Rectangle) ois.readObject();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		// Start recieveing screenshots
		screenReceiver = new ScreenReceiver(is, contentPane);
		eventSender = new EventSender(pw, contentPane, clientScreenDim);
		new Thread(screenReceiver).start();
	}

	private void closeForm(java.awt.event.WindowEvent evt) {
		screenReceiver.stop();
	}
}
