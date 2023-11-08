package Client.feature.filetransfer;

import javax.swing.*;
import java.io.*;
import java.net.Socket;

public class FileReceive implements Runnable {

	Socket socket;

	public FileReceive(Socket socket) {
		this.socket = socket;
	}

	@Override
	public void run() {
		JFileChooser chooser = new JFileChooser();
		chooser.setMultiSelectionEnabled(false);
		chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		if (chooser.showSaveDialog(null) != JOptionPane.OK_OPTION) {
			return;
		}
		int bytesRead;
		try {

			InputStream in = socket.getInputStream();
			DataInputStream clientData = new DataInputStream(in);
			String fileName = clientData.readUTF();
			String fullPath = chooser.getSelectedFile().getPath() + "\\" + fileName;

			OutputStream output = new FileOutputStream(fullPath);

			long size = clientData.readLong();
			byte[] buffer = new byte[3024];
			while (size > 0 && (bytesRead = clientData.read(buffer, 0, (int) Math.min(buffer.length, size))) != -1) {
				output.write(buffer, 0, bytesRead);
				size -= bytesRead;
			}
			output.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}
