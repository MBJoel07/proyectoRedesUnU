package proyectoRedesJava;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class interfazGUI extends JFrame {

    public JTextField ipInicio;
    public JTextField ipFin;
    public JTextField timeout;
    public JButton botonEscanear;
	
    
    public String getIpInicio() {
		return ipInicio.getText().trim();
	}

	public void setIpInicioField(JTextField ipInicio) {
		this.ipInicio = ipInicio;
	}

	public String getIpFin() {
		return ipFin.getText().trim();
	}

	public void setIpFin(JTextField ipFin) {
		this.ipFin = ipFin;
	}

	public String getTimeout() {
		return timeout.getText().trim();
	}

	public void setTimeout(JTextField timeout) {
		this.timeout = timeout;
	}

	public JButton getBotonEscanear() {
		return botonEscanear;
	}

	public void setBotonEscanear(JButton botonEscanear) {
		this.botonEscanear = botonEscanear;
	}

	public interfazGUI() {
    	
        ipInicio = new JTextField();
        ipFin = new JTextField();
        timeout = new JTextField();
        botonEscanear = new JButton("Iniciar escaneo");
    	
        setTitle("Escáner de Red");
        setSize(800, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Centrar ventana

        setLayout(new BorderLayout());

        // Panel superior con campos de entrada y botones
        JPanel inputPanel = new JPanel(new GridLayout(4, 2, 10, 10));
        inputPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        inputPanel.add(new JLabel("IP de inicio:"));
        inputPanel.add(ipInicio);

        inputPanel.add(new JLabel("IP de fin:"));
        inputPanel.add(ipFin);

        inputPanel.add(new JLabel("Tiempo de espera (ms):"));
        inputPanel.add(timeout);

        inputPanel.add(new JButton("Iniciar Escaneo"));
        inputPanel.add(botonEscanear);

        
        inputPanel.add(new JButton("Limpiar"));

        add(inputPanel, BorderLayout.NORTH);

        // 🔹 Tabla de resultados en el centro
        String[] columnas = {"Dirección IP", "Nombre del equipo", "Conectado", "Tiempo de respuesta (ms)"};
        JTable table = new JTable(new DefaultTableModel(columnas, 0));
        JScrollPane scrollPane = new JScrollPane(table);

        add(scrollPane, BorderLayout.CENTER);

        // 🔹 Barra de progreso abajo
        JProgressBar progressBar = new JProgressBar();
        progressBar.setStringPainted(true); // Mostrar texto
        add(progressBar, BorderLayout.SOUTH);
    }
}
