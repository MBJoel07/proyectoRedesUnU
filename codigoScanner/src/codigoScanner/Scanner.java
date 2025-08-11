package codigoScanner;

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
                "^((25[0-5]|2[0-4]\\d|[01]?\\d\\d?)\\.){3}(25[0-5]|2[0-4]\\d|[01]?\\d\\d?)$";
        return ip.matches(ipPattern);
    }

    public List<InfoDeHost> escanear(String ipInicio, String ipFin) {
        List<InfoDeHost> lista = new ArrayList<>();

        try {
            String[] iniPartes = ipInicio.split("\\.");
            String[] finPartes = ipFin.split("\\.");

            int inicio = Integer.parseInt(iniPartes[3]);
            int fin = Integer.parseInt(finPartes[3]);
            String base = iniPartes[0] + "." + iniPartes[1] + "." + iniPartes[2] + ".";

            for (int i = inicio; i <= fin; i++) {
                String ip = base + i;
                long t1 = System.currentTimeMillis();
                boolean ok = InetAddress.getByName(ip).isReachable(timeout);
                long t2 = System.currentTimeMillis();

                lista.add(new InfoDeHost(
                        ip,
                        "Host-" + i,
                        ok,
                        t2 - t1
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return lista;
    }
}

