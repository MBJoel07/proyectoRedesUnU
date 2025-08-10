package codigoScanner;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class Main {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
        interfazGUI gui = new interfazGUI();
        gui.setVisible(true);

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

            // Acá instanciás Scanner y lo usás
            Scanner escaner = new Scanner(timeout);
            if (!escaner.ipValida(ipInicio) || !escaner.ipValida(ipFin)) {
                JOptionPane.showMessageDialog(gui, "Una o ambas IPs no son válidas");
                return;
            }

            // Si está todo bien, podés empezar el escaneo...
            // escaner.escanear(ipInicio, ipFin); etc.
        });
	}

}
