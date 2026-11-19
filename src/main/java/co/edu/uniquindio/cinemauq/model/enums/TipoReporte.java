package co.edu.uniquindio.cinemauq.model.enums;

public enum TipoReporte {
    VENTAS("Ventas realizadas por periodo"),
    ENTRADAS_PELICULA("Entradas vendidas por película"),
    OCUPACION_SALAS("Ocupación de las salas"),
    CONFITERIA("Productos y combos vendidos"),
    RECARGAS("Recargas realizadas"),
    REEMBOLSOS("Cancelaciones y reembolsos"),
    PUNTOS("Puntos acumulados y redimidos");

    private final String titulo;

    TipoReporte(String titulo) { this.titulo = titulo; }

    public String getTitulo() { return titulo; }

    @Override
    public String toString() { return titulo; }
}
