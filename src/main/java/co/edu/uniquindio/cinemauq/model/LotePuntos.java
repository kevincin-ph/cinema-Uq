package co.edu.uniquindio.cinemauq.model;

import java.time.LocalDate;

/** Grupo de puntos obtenidos en una misma fecha. Vence un año después de obtenidos. */
public class LotePuntos {

    private final int puntosObtenidos;
    private int puntosDisponibles;
    private final LocalDate fechaObtencion;
    private final LocalDate fechaVencimiento;

    public LotePuntos(int puntos, LocalDate fechaObtencion) {
        this.puntosObtenidos = puntos;
        this.puntosDisponibles = puntos;
        this.fechaObtencion = fechaObtencion;
        this.fechaVencimiento = fechaObtencion.plusYears(1);
    }

    /** Un lote es vigente si todavía no llega a su fecha de vencimiento. */
    public boolean isVigente(LocalDate hoy) {
        return hoy.isBefore(fechaVencimiento);
    }

    public void consumir(int puntos) {
        if (puntos > puntosDisponibles) {
            throw new IllegalArgumentException("El lote no tiene tantos puntos");
        }
        puntosDisponibles -= puntos;
    }

    public int getPuntosObtenidos() { return puntosObtenidos; }
    public int getPuntosDisponibles() { return puntosDisponibles; }
    public LocalDate getFechaObtencion() { return fechaObtencion; }
    public LocalDate getFechaVencimiento() { return fechaVencimiento; }
}
