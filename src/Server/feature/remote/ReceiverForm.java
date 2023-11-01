package Server.feature.remote;

import javax.swing.*;
import java.awt.*;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.PrintWriter;
import java.net.Socket;

public class ReceiverForm extends JFrame implements Runnable{

    private JPanel contentPane;

    private Socket socket;
    private ScreenReceiver screenReceiver;
    private EventSender eventSender;

    private double width;
    private double height;

    /**
     * Create the frame.
     */
    public ReceiverForm(Socket socket, String w, String h) {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 80, 1000, 600);
        this.setLayout(new BorderLayout());
        contentPane = new JPanel();
        this.add(contentPane, BorderLayout.CENTER);

        this.width = Double.parseDouble(w.trim());
        this.height = Double.parseDouble(h.trim());
        this.socket = socket;
    }

    @Override
    public void run() {
        ObjectInputStream ois = null;
        InputStream is = null;
        PrintWriter pw = null;
        //Rectangle clientScreenDim = null;
        try {
            pw = new PrintWriter(socket.getOutputStream());
            is = socket.getInputStream();
            //ois = new ObjectInputStream(socket.getInputStream());
            this.setVisible(true);
            //clientScreenDim = (Rectangle) ois.readObject();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        //Start recieveing screenshots
        screenReceiver = new ScreenReceiver(is, contentPane);
        eventSender = new EventSender(pw, contentPane, width, height);
        new Thread(screenReceiver).start();
    }
}
