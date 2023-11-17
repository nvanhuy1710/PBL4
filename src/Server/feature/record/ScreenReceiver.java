package Server.feature.record;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Base64;

public class ScreenReceiver implements Runnable{

    private InputStream inputStream;
    private JPanel panel;
    Image image1 = null;

    public ScreenReceiver(InputStream inputStream, JPanel panel) {
        this.inputStream = inputStream;
        this.panel = panel;
    }

    @Override
    public void run() {
        while (true) {
            try {
                //Read screenshots of the client and then draw them
                while (true) {
                    byte[] byteArray = (byte[]) new ObjectInputStream(inputStream).readObject();

                    image1 = ImageIO.read(new ByteArrayInputStream(byteArray));
                    image1 =
                            image1.getScaledInstance(
                                    panel.getWidth(),
                                    panel.getHeight(),
                                    Image.SCALE_FAST
                            );

                    //Draw the received screenshots

                    Graphics graphics = panel.getGraphics();
                    graphics.drawImage(
                            image1,
                            0,
                            0,
                            panel.getWidth(),
                            panel.getHeight(),
                            panel
                    );
                }
            } catch (IOException | ClassNotFoundException ex) {
            	System.out.println("Ko hien thi duoc");
                ex.printStackTrace();
            }
        }
    }

    public void displayImage(byte[] imageData) {
        try {
            // Chuyển dữ liệu byte thành hình ảnh BufferedImage
            BufferedImage img = ImageIO.read(new ByteArrayInputStream(imageData));

            // Hiển thị hình ảnh trên JLabel
            panel.getGraphics().drawImage(img, 0, 0, panel.getWidth(), panel.getHeight(), panel);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
