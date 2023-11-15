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
    private JTextField textField;

    private List<Socket> sockets;

    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        new SendMessageForm(null);
    }

    /**
     * Create the frame.
     */
    public SendMessageForm(List<Socket> sockets) {
        this.sockets = sockets;

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setBounds(100, 100, 367, 192);
        contentPane = new JPanel();
        contentPane.setForeground(new Color(0, 0, 255));
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));

        setContentPane(contentPane);
        contentPane.setLayout(null);

        textField = new JTextField();
        textField.setBounds(10, 55, 328, 20);
        contentPane.add(textField);
        textField.setColumns(10);

        JLabel lblNewLabel = new JLabel("Nhập thông điệp cho tất cả clients");
        lblNewLabel.setForeground(new Color(153, 51, 0));
        lblNewLabel.setFont(new Font("Times New Roman", Font.BOLD, 20));
        lblNewLabel.setBounds(99, 0, 142, 43);
        contentPane.add(lblNewLabel);

        JButton btnNewButton_1 = new JButton("Send");
        btnNewButton_1.setFont(new Font("Tahoma", Font.PLAIN, 16));
        btnNewButton_1.setForeground(new Color(0, 0, 0));
        btnNewButton_1.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                handleMessageButton(e);
            }
        });
        btnNewButton_1.setBounds(129, 124, 89, 20);
        contentPane.add(btnNewButton_1);

        this.setVisible(true);
    }

    public void handleMessageButton(ActionEvent e) {
        String message = textField.getText();
        for(Socket socket : sockets) {
            MessagePackage messagePackage = new MessagePackage();
            messagePackage.setMessage(message);
            PrintWriter outputToClient;
            try {
                outputToClient = new PrintWriter(socket.getOutputStream(), true);
                outputToClient.println(messagePackage);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            JOptionPane.showMessageDialog(null, "Đã gửi");
            dispose();
        }
    }
}
