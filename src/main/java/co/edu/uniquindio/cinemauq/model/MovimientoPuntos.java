package co.edu.uniquindio.cinemauq.model;

import co.edu.uniquindio.cinemauq.model.enums.TipoMovimientoPuntos;

import java.time.LocalDateTime;

public class MovimientoPuntos {

    private final LocalDateTime fecha;
    private final TipoMovimientoPuntos tipo;
    private final int puntos;
    private final String descripcion;

    public MovimientoPuntos(LocalDateTime fecha, TipoMovimientoPuntos tipo, int puntos, String descripcion) {
        this.fecha = fecha;
        this.tipo = tipo;
        this.puntos = puntos;
        this.descripcion = descripcion;
    }

    public LocalDateTime getFecha() { return fecha; }
    public TipoMovimientoPuntos getTipo() { return tipo; }
    public int getPuntos() { return puntos; }
    public String getDescripcion() { return descripcion; }
}
