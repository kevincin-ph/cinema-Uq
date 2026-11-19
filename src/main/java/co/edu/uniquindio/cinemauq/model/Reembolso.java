package co.edu.uniquindio.cinemauq.model;

import java.time.LocalDateTime;

public class Reembolso {

    private final String codigo;
    private final LocalDateTime fecha;
    private final double porcentaje;
    private final double monto;
    private final String motivo;

    public Reembolso(String codigo, LocalDateTime fecha, double porcentaje, double monto, String motivo) {
        this.codigo = codigo;
        this.fecha = fecha;
        this.porcentaje = porcentaje;
        this.monto = monto;
        this.motivo = motivo;
    }

    public String getCodigo() { return codigo; }
    public LocalDateTime getFecha() { return fecha; }
    public double getPorcentaje() { return porcentaje; }
    public double getMonto() { return monto; }
    public String getMotivo() { return motivo; }
}
