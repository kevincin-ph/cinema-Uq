package co.edu.uniquindio.cinemauq.model.enums;

public enum MetodoPago {
    TARJETA_VIRTUAL("Tarjeta virtual"),
    PUNTOS("Puntos CinemaUQ");

    private final String nombre;

    MetodoPago(String nombre) { this.nombre = nombre; }

    @Override
    public String toString() { return nombre; }
}
