package Server.feature.message;

import Server.ServerIndex;
import packages.MessagePackage;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;

public class SendMessageForm extends JFrame {

    private JPanel contentPane;
    private JTextArea textArea;

    private List<Socket> sockets;

    /**
     * Create the frame.
     */
    public SendMessageForm(List<Socket> sockets) {
        this.sockets = sockets;

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setBounds(100, 100, 367, 207);
        contentPane = new JPanel();
        contentPane.setForeground(new Color(0, 0, 255));
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));

        setContentPane(contentPane);
        contentPane.setLayout(null);

        JLabel lblNewLabel = new JLabel("Nhập thông điệp");
        lblNewLabel.setHorizontalAlignment(SwingConstants.CENTER);
        lblNewLabel.setForeground(new Color(0, 0, 0));
        lblNewLabel.setFont(new Font("Times New Roman", Font.BOLD, 20));
        lblNewLabel.setBounds(103, 0, 147, 43);
        contentPane.add(lblNewLabel);

        JButton btnNewButton_1 = new JButton("Gởi");
        btnNewButton_1.setFont(new Font("Tahoma", Font.PLAIN, 16));
        btnNewButton_1.setForeground(new Color(0, 0, 0));
        btnNewButton_1.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                handleMessageButton(e);
            }
        });
        btnNewButton_1.setBounds(129, 124, 89, 31);
        contentPane.add(btnNewButton_1);

        textArea = new JTextArea();
        textArea.setBounds(10, 48, 343, 71);
        contentPane.add(textArea);

        this.setVisible(true);
    }

    public void handleMessageButton(ActionEvent e) {
        String message = textArea.getText();
        for(Socket socket : sockets) {
            MessagePackage messagePackage = new MessagePackage();
            messagePackage.setMessage(message);
            PrintWriter outputToClient;
            try {
                outputToClient = new PrintWriter(socket.getOutputStream(), true);
                outputToClient.println(messagePackage);
            } catch (IOException ex) {
            }
            JOptionPane.showMessageDialog(null, "Đã gửi");
            dispose();
        }
    }
}
