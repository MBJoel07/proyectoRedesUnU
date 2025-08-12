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

    public boolean ipValida(String ip) {
        String ipPattern =
                "^((25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}" +
                "(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$";
        return ip.matches(ipPattern);
    }

    private long ipToLong(String ip) {
        String[] parts = ip.split("\\.");
        long result = 0;
        for (String part : parts) {
            result = result * 256 + Integer.parseInt(part);
        }
        return result;
    }

    private String longToIp(long ip) {
        return String.format("%d.%d.%d.%d",
                (ip >> 24) & 0xFF,
                (ip >> 16) & 0xFF,
                (ip >> 8) & 0xFF,
                ip & 0xFF);
    }

    public List<InfoDeHost> escanear(String ipInicio, String ipFin) {
        List<InfoDeHost> resultados = new ArrayList<>();

        long startIP = ipToLong(ipInicio);
        long endIP = ipToLong(ipFin);

        for (long currentIP = startIP; currentIP <= endIP; currentIP++) {
            String ip = longToIp(currentIP);

            try {
                // Ejecutar ping en CMD
                ProcessBuilder pb = new ProcessBuilder("cmd.exe", "/c",
                        "ping -n 1 -w " + timeout + " " + ip);
                Process process = pb.start();

                BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                String line;
                boolean conectado = false;
                long tiempoRespuesta = -1;

                while ((line = reader.readLine()) != null) {
                    if (line.contains("Respuesta desde") || line.contains("Reply from")) {
                        conectado = true;
                        // Extraer tiempo de respuesta (en ms)
                        String[] partes = line.split(" ");
                        for (String parte : partes) {
                            if (parte.startsWith("tiempo=") || parte.startsWith("time=")) {
                                tiempoRespuesta = Long.parseLong(parte.replaceAll("\\D+", ""));
                            }
                        }
                    }
                }

                String nombreEquipo = "Desconocido";
                if (conectado) {
                    try {
                        nombreEquipo = InetAddress.getByName(ip).getHostName();
                    } catch (Exception ignored) {}
                }

                resultados.add(new InfoDeHost(ip, nombreEquipo, conectado, tiempoRespuesta));

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return resultados;
    }
}
