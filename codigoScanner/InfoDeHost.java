package codigoScanner;

public class InfoDeHost {
    private String direccionIP;
    private String nombreEquipo;
    private boolean conectado;
    private long tiempoRespuesta;

    public InfoDeHost(String direccionIP, String nombreEquipo, boolean conectado, long tiempoRespuesta) {
        this.direccionIP = direccionIP;
        this.nombreEquipo = nombreEquipo;
        this.conectado = conectado;
        this.tiempoRespuesta = tiempoRespuesta;
    }

    public String getDireccionIP() { return direccionIP; }
    public String getNombreEquipo() { return nombreEquipo; }
    public boolean isConectado() { return conectado; }
    public long getTiempoRespuesta() { return tiempoRespuesta; }
}
