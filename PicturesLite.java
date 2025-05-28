import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class PicturesPro extends JFrame {

    private BufferedImage bild;
    private JLabel bildAnzeige;
    private Point startPunkt;
    private Color farbe = Color.BLACK; // Standardfarbe

    public PicturesPro() {
        setTitle("PicturesPro");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panel = new JPanel();

        JButton ladenButton = new JButton("Bild laden");
        ladenButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                bildLaden();
            }
        });
        panel.add(ladenButton);

        JButton speichernButton = new JButton("Bild speichern");
        speichernButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                bildSpeichern();
            }
        });
        panel.add(speichernButton);

        // Farbpalette
        JButton farbeButton = new JButton("Farbe wählen");
        farbeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                farbe = JColorChooser.showDialog(PicturesPro.this, "Farbe wählen", farbe);
            }
        });
        panel.add(farbeButton);

        bildAnzeige = new JLabel();
        bildAnzeige.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                startPunkt = e.getPoint();
            }
        });
        bildAnzeige.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (bild != null) {
                    Graphics2D g2d = bild.createGraphics();
                    g2d.setColor(farbe); // Farbe setzen
                    g2d.drawLine(startPunkt.x, startPunkt.y, e.getX(), e.getY());
                    g2d.dispose();
                    bildAnzeige.setIcon(new ImageIcon(bild));
                    startPunkt = e.getPoint();
                }
            }
        });

        add(panel, BorderLayout.NORTH);
        add(bildAnzeige, BorderLayout.CENTER);

        setVisible(true);
    }

    private void bildLaden() {
        JFileChooser dateiAuswahl = new JFileChooser();
        int rueckgabeWert = dateiAuswahl.showOpenDialog(this);

        if (rueckgabeWert == JFileChooser.APPROVE_OPTION) {
            File ausgewaehlteDatei = dateiAuswahl.getSelectedFile();
            try {
                bild = ImageIO.read(ausgewaehlteDatei);
                bildAnzeige.setIcon(new ImageIcon(bild));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void bildSpeichern() {
        JFileChooser dateiAuswahl = new JFileChooser();
        int rueckgabeWert = dateiAuswahl.showSaveDialog(this);

        if (rueckgabeWert == JFileChooser.APPROVE_OPTION) {
            File ausgewaehlteDatei = dateiAuswahl.getSelectedFile();
            try {
                ImageIO.write(bild, "png", ausgewaehlteDatei);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        new PicturesPro();
    }
}