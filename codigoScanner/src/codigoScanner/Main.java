package codigoScanner;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.InetAddress;

public class Main {
    public static void main(String[] args) {
        interfazGUI gui = new interfazGUI();

        // Acción botón escanear
        gui.botonEscanear.addActionListener(e -> {
            String ipInicio = gui.getIpInicio();
            String ipFin = gui.getIpFin();
            int timeout;

            try {
                timeout = Integer.parseInt(gui.getTimeout());
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(gui, "Tiempo de espera inválido.");
                return;
            }

            String[] partesInicio = ipInicio.split("\\.");
            String[] partesFin = ipFin.split("\\.");

            if (partesInicio.length != 4 || partesFin.length != 4) {
                JOptionPane.showMessageDialog(gui, "Formato de IP inválido.");
                return;
            }

            int inicio = Integer.parseInt(partesInicio[3]);
            int fin = Integer.parseInt(partesFin[3]);

            String baseIP = partesInicio[0] + "." + partesInicio[1] + "." + partesInicio[2] + ".";

            gui.getBarraProgreso().setMaximum(fin - inicio + 1);
            gui.getBarraProgreso().setValue(0);

            DefaultTableModel modelo = (DefaultTableModel) gui.getTabla().getModel();
            modelo.setRowCount(0);

            // Escaneo en un hilo separado
            new Thread(() -> {
                for (int i = inicio; i <= fin; i++) {
                    String ipActual = baseIP + i;
                    String estado = "Desconocido";
                    String nombreHost = "-";
                    long tiempoRespuesta = -1;

                    try {
                        long inicioTiempo = System.currentTimeMillis();
                        ProcessBuilder pb = new ProcessBuilder(
                                "ping", "-n", "1", "-w", String.valueOf(timeout), ipActual
                        );
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
                                    if (!tiempoStr.isEmpty()) {
                                        tiempoRespuesta = Long.parseLong(tiempoStr);
                                    }
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
                        }

                    } catch (Exception ex) {
                        estado = "Error";
                    }

                    String finalEstado = estado;
                    String finalNombreHost = nombreHost;
                    long finalTiempoRespuesta = tiempoRespuesta;

                    SwingUtilities.invokeLater(() -> {
                        modelo.addRow(new Object[]{
                                ipActual,
                                finalNombreHost,
                                finalEstado,
                                finalTiempoRespuesta == -1 ? "-" : finalTiempoRespuesta + " ms"
                        });
                        gui.getBarraProgreso().setValue(gui.getBarraProgreso().getValue() + 1);
                    });
                }
            }).start();
        });

        // Acción botón limpiar
        gui.botonLimpiar.addActionListener(e -> {
            ((DefaultTableModel) gui.getTabla().getModel()).setRowCount(0);
            gui.getBarraProgreso().setValue(0);
        });
    }
}
