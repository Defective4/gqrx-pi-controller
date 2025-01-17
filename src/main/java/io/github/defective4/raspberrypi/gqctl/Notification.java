package io.github.defective4.raspberrypi.gqctl;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;

public class Notification {
    private static JDialog dial;

    private static long dismiss = System.currentTimeMillis();
    static {
        new Timer(true).scheduleAtFixedRate(new TimerTask() {

            @Override
            public void run() {
                if (dial != null && System.currentTimeMillis() > dismiss) {
                    dial.dispose();
                    dial = null;
                }
            }
        }, 1000, 1000);
    }

    private Notification() {}

    public static void show(String message) {
        boolean nw = false;
        if (dial == null) {
            nw = true;
            dial = new JDialog();
            dial.setUndecorated(true);
            dial.setAlwaysOnTop(true);
            dial.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
            dial.setFocusableWindowState(false);
        }
        JLabel text = new JLabel(message) {

            @Override
            protected void paintComponent(Graphics g) {
                g.setColor(Color.black);
                g.fillRect(0, 0, getWidth(), getHeight());
                super.paintComponent(g);
            }
        };
        text.setFont(text.getFont().deriveFont(32f));
        text.setForeground(Color.white);
        text.setBorder(new EmptyBorder(8, 16, 8, 16));
        dial.setContentPane(text);
        dial.pack();
        dismiss = System.currentTimeMillis() + 2000;
        text.invalidate();
        dial.setVisible(true);
        Dimension size = Toolkit.getDefaultToolkit().getScreenSize();
        dial.setLocation(size.width / 2 - dial.getWidth() / 2, size.height / 2 - dial.getHeight() / 2);
    }
}
