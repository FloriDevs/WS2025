import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AdvancedTable extends JFrame {

    private DefaultTableModel tableModel;
    private JTable table;
    private JFileChooser fileChooser = new JFileChooser();
    private Color darkBackgroundColor = new Color(40, 40, 40);
    private Color darkForegroundColor = new Color(200, 200, 200);
    private Map<String, FormulaEvaluator> formulaEvaluators = new HashMap<>();

    public AdvancedTable(int rows, int cols) {
        super("Erweiterte Tabellenkalkulation");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setPreferredSize(new Dimension(700, 500));

        tableModel = new DefaultTableModel(rows, cols);
        table = new JTable(tableModel);
        applyDarkMode();

        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("Datei");
        JMenuItem saveItem = new JMenuItem("Speichern");
        JMenuItem loadItem = new JMenuItem("Laden");
        JMenuItem operatorsItem = new JMenuItem("Operatoren anzeigen");

        saveItem.addActionListener(e -> saveTable());
        loadItem.addActionListener(e -> loadTable());
        operatorsItem.addActionListener(e -> showOperators());

        fileMenu.add(saveItem);
        fileMenu.add(loadItem);
        menuBar.add(fileMenu);

        JMenu helpMenu = new JMenu("Hilfe");
        helpMenu.add(operatorsItem);
        menuBar.add(helpMenu);

        setJMenuBar(menuBar);

        // Formel-Evaluatoren registrieren
        formulaEvaluators.put("SUMME", this::evaluateSum);
        formulaEvaluators.put("MITTELWERT", this::evaluateAverage);
        formulaEvaluators.put("MIN", this::evaluateMin);
        formulaEvaluators.put("MAX", this::evaluateMax);

        // Listener hinzufügen, um Formeln bei Änderungen zu verarbeiten
        table.getModel().addTableModelListener(new TableModelListener() {
            @Override
            public void tableChanged(TableModelEvent e) {
                if (e.getType() == TableModelEvent.UPDATE) {
                    // Überprüfe die gesamte Tabelle nach Formeln und werte sie neu aus
                    for (int i = 0; i < table.getRowCount(); i++) {
                        for (int j = 0; j < table.getColumnCount(); j++) {
                            Object value = table.getValueAt(i, j);
                            if (value instanceof String && ((String) value).startsWith("=")) {
                                evaluateCell(i, j, (String) value);
                            }
                        }
                    }
                }
            }
        });

        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void applyDarkMode() {
        getContentPane().setBackground(darkBackgroundColor);
        table.setBackground(darkBackgroundColor);
        table.setForeground(darkForegroundColor);
        table.getTableHeader().setBackground(darkBackgroundColor);
        table.getTableHeader().setForeground(darkForegroundColor);
    }

    private void saveTable() {
        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                // Spaltennamen speichern
                for (int i = 0; i < table.getColumnCount(); i++) {
                    writer.write(table.getColumnName(i));
                    if (i < table.getColumnCount() - 1) {
                        writer.write(",");
                    }
                }
                writer.newLine();

                // Daten speichern
                for (int i = 0; i < table.getRowCount(); i++) {
                    for (int j = 0; j < table.getColumnCount(); j++) {
                        Object value = table.getValueAt(i, j);
                        writer.write(value != null ? value.toString() : "");
                        if (j < table.getColumnCount() - 1) {
                            writer.write(",");
                        }
                    }
                    writer.newLine();
                }
                JOptionPane.showMessageDialog(this, "Tabelle gespeichert!", "Speichern", JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Fehler beim Speichern der Tabelle!", "Fehler", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        }
    }

    private void loadTable() {
        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line;
                // Spaltennamen laden
                line = reader.readLine();
                if (line != null) {
                    String[] columns = line.split(",");
                    tableModel.setColumnCount(0);
                    for (String col : columns) {
                        tableModel.addColumn(col.trim());
                    }
                    tableModel.setRowCount(0);
                    // Daten laden
                    while ((line = reader.readLine()) != null) {
                        String[] values = line.split(",");
                        tableModel.addRow(values);
                    }
                    // Nach dem Laden die Formeln einmalig auswerten
                    for (int i = 0; i < table.getRowCount(); i++) {
                        for (int j = 0; j < table.getColumnCount(); j++) {
                            Object value = table.getValueAt(i, j);
                            if (value instanceof String && ((String) value).startsWith("=")) {
                                evaluateCell(i, j, (String) value);
                            }
                        }
                    }
                    JOptionPane.showMessageDialog(this, "Tabelle geladen!", "Laden", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, "Leere Datei!", "Laden", JOptionPane.WARNING_MESSAGE);
                }
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Fehler beim Laden der Tabelle!", "Fehler", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        }
    }

    private void showOperators() {
        String operators = "Unterstützte Operatoren:\n" +
                           "- SUMME(A1:B3) - Berechnet die Summe der Werte im Bereich A1 bis B3.\n" +
                           "- MITTELWERT(A1:B3) - Berechnet den Durchschnitt der Werte im Bereich A1 bis B3.\n" +
                           "- MIN(A1:B3) - Findet den kleinsten Wert im Bereich A1 bis B3.\n" +
                           "- MAX(A1:B3) - Findet den größten Wert im Bereich A1 bis B3.";
        JOptionPane.showMessageDialog(this, operators, "Unterstützte Operatoren", JOptionPane.INFORMATION_MESSAGE);
    }

    private void evaluateCell(int row, int col, String formula) {
        Pattern pattern = Pattern.compile("=(\\w+)\\((\\w+):(\\w+)\\)");
        Matcher matcher = pattern.matcher(formula.toUpperCase());
        if (matcher.matches()) {
            String operator = matcher.group(1);
            String startCell = matcher.group(2);
            String endCell = matcher.group(3);

            FormulaEvaluator evaluator = formulaEvaluators.get(operator);
            if (evaluator != null) {
                try {
                    double result = evaluator.evaluate(startCell, endCell);
                    table.setValueAt(result, row, col);
                } catch (IllegalArgumentException e) {
                    table.setValueAt("Fehler!", row, col);
                }
            } else {
                table.setValueAt("#NAME?", row, col); // Operator nicht gefunden
            }
        } else {
            table.setValueAt("#WERT!", row, col); // Ungültige Formel
        }
    }

    private double evaluateSum(String startCell, String endCell) {
        try {
            double sum = 0;
            for (double value : getCellValues(startCell, endCell)) {
                sum += value;
            }
            return sum;
        } catch (IllegalArgumentException e) {
            throw e;
        }
    }

    private double evaluateAverage(String startCell, String endCell) {
        try {
            double sum = 0;
            java.util.List<Double> values = getCellValues(startCell, endCell);
            if (!values.isEmpty()) {
                for (double value : values) {
                    sum += value;
                }
                return sum / values.size();
            } else {
                return 0; // Oder vielleicht eine Fehlermeldung?
            }
        } catch (IllegalArgumentException e) {
            throw e;
        }
    }

    private double evaluateMin(String startCell, String endCell) {
        try {
            java.util.List<Double> values = getCellValues(startCell, endCell);
            if (!values.isEmpty()) {
                double min = values.get(0);
                for (double value : values) {
                    if (value < min) {
                        min = value;
                    }
                }
                return min;
            } else {
                throw new IllegalArgumentException("Leerer Zellbereich für MIN");
            }
        } catch (IllegalArgumentException e) {
            throw e;
        }
    }

    private double evaluateMax(String startCell, String endCell) {
        try {
            java.util.List<Double> values = getCellValues(startCell, endCell);
            if (!values.isEmpty()) {
                double max = values.get(0);
                for (double value : values) {
                    if (value > max) {
                        max = value;
                    }
                }
                return max;
            } else {
                throw new IllegalArgumentException("Leerer Zellbereich für MAX");
            }
        } catch (IllegalArgumentException e) {
            throw e;
        }
    }

    private java.util.List<Double> getCellValues(String startCell, String endCell) {
        java.util.List<Double> values = new java.util.ArrayList<>();
        try {
            int startCol = colToIndex(startCell.substring(0, 1));
            int startRow = Integer.parseInt(startCell.substring(1)) - 1;
            int endCol = colToIndex(endCell.substring(0, 1));
            int endRow = Integer.parseInt(endCell.substring(1)) - 1;

            if (startRow > endRow || startCol > endCol || startRow < 0 || startCol < 0 || endRow >= table.getRowCount() || endCol >= table.getColumnCount()) {
                throw new IllegalArgumentException("Ungültiger Zellbereich");
            }

            for (int i = startRow; i <= endRow; i++) {
                for (int j = startCol; j <= endCol; j++) {
                    Object value = table.getValueAt(i, j);
                    if (value != null) {
                        try {
                            values.add(Double.parseDouble(value.toString()));
                        } catch (NumberFormatException e) {
                            // Ignoriere nicht-numerische Werte
                        }
                    }
                }
            }
            return values;
        } catch (NumberFormatException | StringIndexOutOfBoundsException e) {
            throw new IllegalArgumentException("Ungültige Zellreferenz");
        }
    }

    private int colToIndex(String col) {
        int index = 0;
        for (char c : col.toCharArray()) {
            index = index * 26 + (c - 'A' + 1);
        }
        return index - 1;
    }

    @FunctionalInterface
    private interface FormulaEvaluator {
        double evaluate(String startCell, String endCell);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new AdvancedTable(10, 10));
    }
}
