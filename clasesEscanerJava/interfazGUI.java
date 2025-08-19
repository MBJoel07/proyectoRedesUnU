package codigoScanner;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class interfazGUI extends JFrame {

    private JTextField ipInicioField;
    private JTextField ipFinField;
    private JTextField tiempoEsperaField;
    private JTable tabla;
    private JProgressBar barraProgreso;

    public JButton botonEscanear;
    public JButton botonLimpiar;

    public interfazGUI() {
        setTitle("Escáner de Red");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(700, 400);
        setLocationRelativeTo(null);

        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // ====== FILA 1: IP inicio ======
        gbc.gridx = 0; gbc.gridy = 0;
        add(new JLabel("IP inicio:"), gbc);

        gbc.gridx = 1;
        ipInicioField = new JTextField(15);
        add(ipInicioField, gbc);

        // ====== FILA 2: IP fin ======
        gbc.gridx = 0; gbc.gridy = 1;
        add(new JLabel("IP fin:"), gbc);

        gbc.gridx = 1;
        ipFinField = new JTextField(15);
        add(ipFinField, gbc);

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

        setVisible(true);
    }

    // Getters
    public String getIpInicio() {
        return ipInicioField.getText().trim();
    }

    public String getIpFin() {
        return ipFinField.getText().trim();
    }

    public String getTimeout() {
        return tiempoEsperaField.getText().trim();
    }

    public JTable getTabla() {
        return tabla;
    }

    public JProgressBar getBarraProgreso() {
        return barraProgreso;
    }
}
