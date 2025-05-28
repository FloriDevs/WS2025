import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;

public class Texteditor extends JFrame {
    private JTextArea textArea;

    public Texteditor() {
        setTitle("FloriDevs Text Editor Pro");
        setSize(1200, 800);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        textArea = new JTextArea();
        textArea.setBackground(new Color (0, 0, 0));
        textArea.setForeground(new Color (255, 255, 255));
        JScrollPane scrollPane = new JScrollPane(textArea);
        add(scrollPane, BorderLayout.CENTER);

        createMenuBar();

        setVisible(true);
    }

    private void createMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        JMenu fileMenu = new JMenu("File");
        JMenuItem openItem = new JMenuItem("Open");
        JMenuItem saveItem = new JMenuItem("Save");
        JMenuItem exitItem = new JMenuItem("Exit");

        JMenu aboutmenu = new JMenu ("About");
        JMenuItem About = new  JMenuItem("About Text Editor Pro");

        openItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                openFile();
            }
        });

        About.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JFrame frame = new JFrame("About FloriDevs Work Sweet");
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setSize(300, 400);


                JPanel panel = new JPanel();


                JLabel label = new JLabel("FloriDevs 2025 Sweet Pro open");
                JLabel label2 = new JLabel("Version 2025");
                JLabel label3 = new JLabel("Made by FloriDevs(tudios) :-D");


                ImageIcon imageIcon = new ImageIcon("icon.png");
                JLabel imageLabel = new JLabel(imageIcon);

                // Füge die JLabel-Objekte und das Bild zum Panel hinzu
                panel.add(imageLabel);
                panel.add(label);
                panel.add(label2);
                panel.add(label3);

                // Füge das Panel zum Frame hinzu
                frame.add(panel);

                // Setze das Frame sichtbar
                frame.setVisible(true);
            }
        });

        saveItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                saveFile();
            }
        });

        exitItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });

        fileMenu.add(openItem);
        fileMenu.add(saveItem);
        aboutmenu.add(About);
        fileMenu.addSeparator();
        fileMenu.add(exitItem);
        menuBar.add(fileMenu);
        menuBar.add (aboutmenu);
        setJMenuBar(menuBar);
    }

    private void openFile() {
        JFileChooser fileChooser = new JFileChooser();


        javax.swing.filechooser.FileFilter txtFilter = new javax.swing.filechooser.FileFilter() {
            @Override
            public boolean accept(File f) {
                return f.isDirectory() || f.getName().toLowerCase().endsWith(".fst");
            }

            @Override
            public String getDescription() {
                return "FloriDevsTextfiles (*.fst)";
            }
        };

        // FileFilter für .bat Dateien erstellen
        javax.swing.filechooser.FileFilter batFilter = new javax.swing.filechooser.FileFilter() {
            @Override
            public boolean accept(File f) {
                return f.isDirectory() || f.getName().toLowerCase().endsWith(".bat");
            }

            @Override
            public String getDescription() {
                return "Batch-Dateien (*.bat)";
            }
        };

        // Filter hinzufügen
        fileChooser.addChoosableFileFilter(txtFilter);
        fileChooser.addChoosableFileFilter(batFilter);
        fileChooser.setAcceptAllFileFilterUsed(false); // Optional: Deaktiviert den "Alle Dateien"-Filter

        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                textArea.setText("");
                String line;
                while ((line = br.readLine()) != null) {
                    textArea.append(line + "\n");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    private void saveFile() {
        JFileChooser fileChooser = new JFileChooser();

        // FileFilter für .fst Dateien erstellen
        fileChooser.setFileFilter(new javax.swing.filechooser.FileFilter() {
            @Override
            public boolean accept(File f) {
                return f.isDirectory() || f.getName().toLowerCase().endsWith(".fst");
            }

            @Override
            public String getDescription() {
                return "FloriDevsTextEditor Files (*.fst)";
            }
        });

        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();

            // Überprüfen, ob die Endung .fst vorhanden ist, und sie hinzufügen, falls nicht
            if (!file.getName().toLowerCase().endsWith(".fst")) {
                file = new File(file.getAbsolutePath() + ".fst");
            }

            try (BufferedWriter bw = new BufferedWriter(new FileWriter(file))) {
                bw.write(textArea.getText());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Texteditor());
    }
}
