package codigoScanner;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        interfazGUI gui = new interfazGUI();

        gui.botonEscanear.addActionListener(e -> {
            String ipInicio = gui.getIpInicio();
            String ipFin = gui.getIpFin();
            int timeout;

            try {
                timeout = Integer.parseInt(gui.getTimeout());
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(gui, "El tiempo de espera no es válido");
                return;
            }

            Scanner escaner = new Scanner(timeout);

            if (!escaner.ipValida(ipInicio) || !escaner.ipValida(ipFin)) {
                JOptionPane.showMessageDialog(gui, "Una o ambas IPs no son válidas");
                return;
            }

            List<InfoDeHost> resultados = escaner.escanear(ipInicio, ipFin);

            DefaultTableModel model = (DefaultTableModel) gui.getTabla().getModel();
            model.setRowCount(0);

            for (InfoDeHost host : resultados) {
                model.addRow(new Object[]{
                        host.getDireccionIP(),
                        host.getNombreEquipo(),
                        host.isConectado() ? "Sí" : "No",
                        host.getTiempoRespuesta()
                });
            }
        });

        gui.botonLimpiar.addActionListener(e -> {
            gui.getTabla().setModel(new DefaultTableModel(
                    new Object[]{"IP", "Nombre", "Estado", "Tiempo respuesta"}, 0
            ));
        });
    }
}
