package codigoScanner;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

public class Scanner {
    private int timeout;

    public Scanner(int timeout) {
        this.timeout = timeout;
    }

    // Verifica si la IP escrita tiene un formato válido
    public boolean ipValida(String ip) {
        String ipPattern =
                "^((25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}" +
                "(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$";
        return ip.matches(ipPattern);
    }

    // Convierte IP en formato "x.x.x.x" a número largo
    private long ipToLong(String ip) {
        String[] parts = ip.split("\\.");
        long result = 0;
        for (String part : parts) {
            result = result * 256 + Integer.parseInt(part);
        }
        return result;
    }

    // Convierte número largo a formato "x.x.x.x"
    private String longToIp(long ip) {
        return String.format("%d.%d.%d.%d",
                (ip >> 24) & 0xFF,
                (ip >> 16) & 0xFF,
                (ip >> 8) & 0xFF,
                ip & 0xFF);
    }

    // Escanea un rango de IPs
    public List<InfoDeHost> escanear(String ipInicio, String ipFin) {
        List<InfoDeHost> resultados = new ArrayList<>();

        long startIP = ipToLong(ipInicio);
        long endIP = ipToLong(ipFin);

        // Recorremos todas las IPs del rango
        for (long currentIP = startIP; currentIP <= endIP; currentIP++) {
            String ip = longToIp(currentIP);

            try {
                // Ejecutar ping en Windows CMD (1 intento con tiempo de espera definido)
                ProcessBuilder pb = new ProcessBuilder("cmd.exe", "/c",
                        "ping -n 1 -w " + timeout + " " + ip);
                Process process = pb.start();

                BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                String line;
                boolean conectado = false;
                long tiempoRespuesta = -1;

                // Analiza la salida del ping
                while ((line = reader.readLine()) != null) {
                    if (line.contains("Respuesta desde") || line.contains("Reply from")) {
                        conectado = true;
                        // Busca el tiempo de respuesta en la línea
                        String[] partes = line.split(" ");
                        for (String parte : partes) {
                            if (parte.contains("tiempo") || parte.contains("time")) {
                                tiempoRespuesta = Long.parseLong(parte.replaceAll("\\D+", ""));
                            }
                        }
                    }
                }

                // Intentar obtener el nombre del equipo
                String nombreEquipo = "Desconocido";
                if (conectado) {
                    try {
                        nombreEquipo = InetAddress.getByName(ip).getHostName();
                        // Si no pudo resolver y solo devuelve la IP, lo dejamos como "Desconocido"
                        if (nombreEquipo.equals(ip)) {
                            nombreEquipo = "Desconocido";
                        }
                    } catch (Exception ignored) {}
                }

                // Guardar resultados
                resultados.add(new InfoDeHost(ip, nombreEquipo, conectado, tiempoRespuesta));

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return resultados;
    }
}
