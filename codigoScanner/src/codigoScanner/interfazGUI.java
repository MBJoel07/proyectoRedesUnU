package codigoScanner;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import java.awt.*;
import java.io.FileWriter;

public class interfazGUI extends JFrame {

    private JTextField ipInicioField;
    private JTextField ipFinField;
    private JTextField tiempoEsperaField;
    private JTable tabla;
    private JProgressBar barraProgreso;

    public JButton botonEscanear;
    public JButton botonLimpiar;
    public JButton botonExportar; 

    private Scanner scanner; // Para validación en tiempo real

    public interfazGUI() {
        setTitle("Escáner de Red");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(750, 450);
        setLocationRelativeTo(null);

        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        scanner = new Scanner(1000); // Timeout default solo para validar

        // ====== FILA 1: IP inicio ======
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        add(new JLabel("IP inicio:"), gbc);

        gbc.gridx = 1;
        ipInicioField = new JTextField(15);
        add(ipInicioField, gbc);
        addValidacionTiempoReal(ipInicioField);

        // ====== FILA 2: IP fin ======
        gbc.gridx = 0; gbc.gridy = 1;
        add(new JLabel("IP fin:"), gbc);

        gbc.gridx = 1;
        ipFinField = new JTextField(15);
        add(ipFinField, gbc);
        addValidacionTiempoReal(ipFinField);

        // ====== FILA 3: Tiempo de espera ======
        gbc.gridx = 0; gbc.gridy = 2;
        add(new JLabel("Tiempo de espera (ms):"), gbc);

        gbc.gridx = 1;
        tiempoEsperaField = new JTextField(15);
        add(tiempoEsperaField, gbc);

        // ====== FILA 4: Botones ======
        gbc.gridx = 0; gbc.gridy = 3;
        botonEscanear = new JButton("Iniciar Escaneo");
        add(botonEscanear, gbc);

        gbc.gridx = 1;
        botonLimpiar = new JButton("Limpiar");
        add(botonLimpiar, gbc);

        // ====== FILA 5: Tabla ======
        gbc.gridx = 0; gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1; gbc.weighty = 1;

        tabla = new JTable(new DefaultTableModel(
                new Object[]{"IP", "Nombre", "Estado", "Tiempo respuesta (ms)"}, 0
        ));
        JScrollPane scroll = new JScrollPane(tabla);
        add(scroll, gbc);

        // ====== FILA 6: Barra de progreso ======
        gbc.gridy = 5;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weighty = 0;
        barraProgreso = new JProgressBar();
        add(barraProgreso, gbc);

     // ====== FILA 7: Botón Exportar debajo de la barra ======
        JPanel panelBotonExportar = new JPanel(new FlowLayout(FlowLayout.CENTER));
        botonExportar = new JButton("Exportar CSV");
        botonExportar.setEnabled(false);
        panelBotonExportar.add(botonExportar);

        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.gridwidth = 3;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weighty = 0; // importante, no darle espacio vertical extra
        gbc.anchor = GridBagConstraints.CENTER;

        add(panelBotonExportar, gbc);

        // Acción Exportar
        botonExportar.addActionListener(e -> exportarCSV());

        // 🔹 Listener: habilitar/deshabilitar Exportar según contenido de la tabla
        ((DefaultTableModel) tabla.getModel()).addTableModelListener(e -> {
            botonExportar.setEnabled(tabla.getRowCount() > 0);
        });

        setVisible(true);
    }

    // ===== VALIDACIÓN EN TIEMPO REAL =====
    private void addValidacionTiempoReal(JTextField campo) {
        campo.getDocument().addDocumentListener(new DocumentListener() {
            private void validar() {
                String texto = campo.getText().trim();
                if (!texto.isEmpty() && !scanner.ipValida(texto)) {
                    campo.setBackground(new Color(255, 150, 150)); // rojo claro si inválido
                } else {
                    campo.setBackground(Color.WHITE); // normal si válido
                }
            }

            public void insertUpdate(DocumentEvent e) { validar(); }
            public void removeUpdate(DocumentEvent e) { validar(); }
            public void changedUpdate(DocumentEvent e) { validar(); }
        });
    }

    // ===== EXPORTAR CSV =====
    private void exportarCSV() {
        try {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Guardar resultados");
            int userSelection = fileChooser.showSaveDialog(this);

            if (userSelection == JFileChooser.APPROVE_OPTION) {
                String filePath = fileChooser.getSelectedFile().getAbsolutePath();
                if (!filePath.endsWith(".csv")) {
                    filePath += ".csv";
                }

                FileWriter writer = new FileWriter(filePath);
                DefaultTableModel model = (DefaultTableModel) tabla.getModel();

                // Escribir cabecera
                for (int i = 0; i < model.getColumnCount(); i++) {
                    writer.write(model.getColumnName(i) + (i < model.getColumnCount() - 1 ? "," : ""));
                }
                writer.write("\n");

                // Escribir filas
                for (int i = 0; i < model.getRowCount(); i++) {
                    for (int j = 0; j < model.getColumnCount(); j++) {
                        writer.write(model.getValueAt(i, j).toString() + (j < model.getColumnCount() - 1 ? "," : ""));
                    }
                    writer.write("\n");
                }

                writer.close();
                JOptionPane.showMessageDialog(this, "Resultados exportados a " + filePath);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al exportar: " + e.getMessage());
        }
    }

    // Getters
    public String getIpInicio() { return ipInicioField.getText().trim(); }
    public String getIpFin() { return ipFinField.getText().trim(); }
    public String getTimeout() { return tiempoEsperaField.getText().trim(); }
    public JTable getTabla() { return tabla; }
    public JProgressBar getBarraProgreso() { return barraProgreso; }
}
