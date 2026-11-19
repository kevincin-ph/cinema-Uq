package co.edu.uniquindio.cinemauq.model;

import co.edu.uniquindio.cinemauq.model.enums.TipoMovimiento;

import java.time.LocalDateTime;

/** Movimiento de la tarjeta virtual (recarga, pago o reembolso). */
public class Movimiento {

    private final LocalDateTime fecha;
    private final TipoMovimiento tipo;
    private final double monto;
    private final String descripcion;
    private final double saldoResultante;

    public Movimiento(LocalDateTime fecha, TipoMovimiento tipo, double monto, String descripcion, double saldoResultante) {
        this.fecha = fecha;
        this.tipo = tipo;
        this.monto = monto;
        this.descripcion = descripcion;
        this.saldoResultante = saldoResultante;
    }

    public LocalDateTime getFecha() { return fecha; }
    public TipoMovimiento getTipo() { return tipo; }
    public double getMonto() { return monto; }
    public String getDescripcion() { return descripcion; }
    public double getSaldoResultante() { return saldoResultante; }
}
