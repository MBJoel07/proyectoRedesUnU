package codigoScanner;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class FuncionesNetStat {

    public static String ejecutarComando(String opcion) {
        StringBuilder resultado = new StringBuilder();

        try {
            ProcessBuilder builder = new ProcessBuilder("netstat", opcion);
            builder.redirectErrorStream(true);
            Process proceso = builder.start();

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(proceso.getInputStream()))) {
                String linea;
                while ((linea = reader.readLine()) != null) {
                    resultado.append(linea).append("\n");
                }
            }

        } catch (IOException e) {
            resultado.append("Error al ejecutar netstat: ").append(e.getMessage());
        }

        return resultado.toString();
    }
}
