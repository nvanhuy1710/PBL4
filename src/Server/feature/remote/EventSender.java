package Server.feature.remote;

import javax.swing.*;
import java.awt.event.*;
import java.io.PrintWriter;

public class EventSender implements KeyListener, MouseListener, MouseMotionListener {

    private final PrintWriter pw;
    private final JPanel panel;
    private final double w;
    private final double h;

    public EventSender(PrintWriter pw, JPanel panel, double w, double h) {
        this.pw = pw;
        this.panel = panel;
        this.w = w;
        this.h = h;

        panel.addKeyListener(this);
        panel.addMouseListener(this);
        panel.addMouseMotionListener(this);
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
        pw.println(-3);
        pw.println(e.getKeyCode());
        pw.flush();
    }

    @Override
    public void keyReleased(KeyEvent e) {
        pw.println(-4);
        pw.println(e.getKeyCode());
        pw.flush();
    }

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {
        pw.println(-1);
        int button = e.getButton();
        int xButton = 16;
        if (button == 3) {
            xButton = 4;
        }
        pw.println(xButton);
        pw.flush();
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        pw.println(-2);
        int button = e.getButton();
        int xButton = 16;
        if (button == 3) {
            xButton = 4;
        }
        pw.println(xButton);
        pw.flush();
    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    @Override
    public void mouseDragged(MouseEvent e) {
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        double xScale = (double) w / panel.getWidth();
        double yScale = (double) h / panel.getHeight();
        pw.println(-5);
        pw.println((int) (e.getX() * xScale));
        pw.println((int) (e.getY() * yScale));
        pw.flush();
    }
}
