package codigoScanner;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.InetAddress;

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

        // ====== FILA 1: IP de inicio ======
        gbc.gridx = 0; gbc.gridy = 0;
        add(new JLabel("IP inicio:"), gbc);

        gbc.gridx = 1;
        ipInicioField = new JTextField(15);
        add(ipInicioField, gbc);

        // ====== FILA 2: IP de fin ======
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

        // Acción del botón Escanear
        botonEscanear.addActionListener(e -> iniciarEscaneo());

        // Acción del botón Limpiar
        botonLimpiar.addActionListener(e -> {
            ((DefaultTableModel) tabla.getModel()).setRowCount(0);
            barraProgreso.setValue(0);
        });

        setVisible(true);
    }

    private void iniciarEscaneo() {
        String ipInicio = getIpInicio();
        String ipFin = getIpFin();
        int timeout;

        try {
            timeout = Integer.parseInt(getTimeout());
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Tiempo de espera inválido.");
            return;
        }

        String[] partesInicio = ipInicio.split("\\.");
        String[] partesFin = ipFin.split("\\.");

        if (partesInicio.length != 4 || partesFin.length != 4) {
            JOptionPane.showMessageDialog(this, "Formato de IP inválido.");
            return;
        }

        int inicio = Integer.parseInt(partesInicio[3]);
        int fin = Integer.parseInt(partesFin[3]);

        String baseIP = partesInicio[0] + "." + partesInicio[1] + "." + partesInicio[2] + ".";

        barraProgreso.setMaximum(fin - inicio + 1);
        barraProgreso.setValue(0);

        DefaultTableModel modelo = (DefaultTableModel) tabla.getModel();
        modelo.setRowCount(0);

        new Thread(() -> {
            for (int i = inicio; i <= fin; i++) {
                String ipActual = baseIP + i;
                String estado = "Desconocido";
                String nombreHost = "";
                long tiempoRespuesta = -1;

                try {
                    long inicioTiempo = System.currentTimeMillis();
                    ProcessBuilder pb = new ProcessBuilder("ping", "-n", "1", "-w", String.valueOf(timeout), ipActual);
                    Process p = pb.start();

                    BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
                    String linea;
                    boolean respuestaOK = false;

                    while ((linea = reader.readLine()) != null) {
                        if (linea.contains("tiempo") || linea.contains("time")) {
                            respuestaOK = true;
                            if (linea.contains("ms")) {
                                String[] partes = linea.split("=");
                                String tiempoStr = partes[partes.length - 1].replaceAll("[^0-9]", "");
                                tiempoRespuesta = Long.parseLong(tiempoStr);
                            }
                        }
                    }

                    p.waitFor();
                    long finTiempo = System.currentTimeMillis();

                    if (tiempoRespuesta == -1 && respuestaOK) {
                        tiempoRespuesta = finTiempo - inicioTiempo;
                    }

                    if (respuestaOK) {
                        estado = "Activo";
                        nombreHost = InetAddress.getByName(ipActual).getHostName();
                    } else {
                        estado = "Inactivo";
                        nombreHost = "-";
                    }

                } catch (Exception ex) {
                    estado = "Error";
                    nombreHost = "-";
                }

                modelo.addRow(new Object[]{ipActual, nombreHost, estado, tiempoRespuesta == -1 ? "-" : tiempoRespuesta + " ms"});

                int progreso = i - inicio + 1;
                barraProgreso.setValue(progreso);
            }
        }).start();
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

    public static void main(String[] args) {
        SwingUtilities.invokeLater(interfazGUI::new);
    }
}
