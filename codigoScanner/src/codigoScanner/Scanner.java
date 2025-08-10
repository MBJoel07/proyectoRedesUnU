package codigoScanner;
public class Scanner {
    private int timeout;

    public Scanner(int timeout) {
        this.timeout = timeout;
    }

    public boolean ipValida(String ip) {
    	String[] partes = ip.split("\\.");

        // 2. Verificamos que tenga exactamente 4 partes
        if (partes.length != 4) return false;

        // 3. Validamos cada parte
        for (String parte : partes) {
            try {
                int numero = Integer.parseInt(parte);
                if (numero < 0 || numero > 255) return false;
            } catch (NumberFormatException e) {
                // Si no se puede convertir a número, no es válido
                return false;
            }
        }

        // 4. Si todo está bien, es válida
        return true;
    }

    
}

