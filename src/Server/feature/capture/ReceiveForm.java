package Server.feature.capture;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import packages.CapturePackage;

import javax.imageio.ImageIO;

import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.awt.event.ActionEvent;

public class ReceiveForm extends JFrame implements Runnable {

	private JPanel contentPane;
	private Image image1;

	public ReceiveForm(Socket socket) {
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 1064, 556);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));

		setContentPane(contentPane);
		contentPane.setLayout(null);

		JButton btnNewButton = new JButton("Chụp ảnh");
		btnNewButton.setBounds(319, 26, 85, 21);
		contentPane.add(btnNewButton);

		JButton btnNewButton_1 = new JButton("Lưu");
		btnNewButton_1.setBounds(645, 26, 85, 21);
		contentPane.add(btnNewButton_1);

		JPanel panel = new JPanel();
		panel.setBounds(10, 58, 1030, 453);
		contentPane.add(panel);

		btnNewButton_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Thread th = new Thread(() -> SavePicture());
				th.start();

			}
		});
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Thread th = new Thread(() -> CapturePicture(socket, panel));
				th.start();
			}
		});
	}

	@Override
	public void run() {
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		this.setVisible(true);
	}

	public void CapturePicture(Socket socket, JPanel panel) {
		try {
			CapturePackage cp = new CapturePackage();
			cp.setCmd("Start");
			cp.setMessage("capture");
			PrintWriter outputToClient;
			try {
				outputToClient = new PrintWriter(socket.getOutputStream(), true);
				outputToClient.println(cp);
			} catch (IOException ex) {
			}

			InputStream inputStream = socket.getInputStream();
			// Read screenshots of the client and then draw them
			byte[] byteArray = (byte[]) new ObjectInputStream(inputStream).readObject();
			image1 = ImageIO.read(new ByteArrayInputStream(byteArray));

			// Draw the received screenshots
			Graphics graphics = panel.getGraphics();
			graphics.drawImage(image1, 0, 0, panel.getWidth(), panel.getHeight(), panel);
		} catch (IOException | ClassNotFoundException ex) {
			ex.printStackTrace();
		}
	}

	public void SavePicture() {
		if (image1 != null) {
			BufferedImage bufferedImage = new BufferedImage(image1.getWidth(null), image1.getHeight(null),
					BufferedImage.TYPE_INT_ARGB);
			Graphics2D g2d = bufferedImage.createGraphics();
			g2d.drawImage(image1, 0, 0, null);
			g2d.dispose();
			JFileChooser fileChooser = new JFileChooser();
			fileChooser.setDialogTitle("Specify a file to save");
			System.out.println(bufferedImage);
			int userSelection = fileChooser.showSaveDialog(null);

			if (userSelection == JFileChooser.APPROVE_OPTION) {
				File fileToSave = fileChooser.getSelectedFile();
				System.out.println("Save as file: " + fileToSave.getAbsolutePath());

				try {
					ImageIO.write(bufferedImage, "png", fileToSave);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
