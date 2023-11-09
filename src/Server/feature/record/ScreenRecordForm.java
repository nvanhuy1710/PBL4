package Server.feature.record;

import Server.feature.record.ScreenReceiver;

import javax.swing.*;
import java.awt.*;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.PrintWriter;
import java.net.Socket;

public class ScreenRecordForm extends JFrame implements Runnable{

    private JPanel contentPane;

    private Socket socket;
    private ScreenReceiver screenReceiver;

    /**
     * Create the frame.
     */
    public ScreenRecordForm(Socket socket) {
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setPreferredSize(new java.awt.Dimension(800, 600));
        contentPane = new JPanel();
        contentPane.setPreferredSize(new java.awt.Dimension(1366, 768));

        this.getContentPane().add(contentPane, BorderLayout.CENTER);

        this.socket = socket;

        pack();
    }

    @Override
    public void run() {
        ObjectInputStream ois = null;
        InputStream is = null;
        try {
            is = socket.getInputStream();
            this.setVisible(true);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        screenReceiver = new ScreenReceiver(is, contentPane);
        new Thread(screenReceiver).start();
    }

}
