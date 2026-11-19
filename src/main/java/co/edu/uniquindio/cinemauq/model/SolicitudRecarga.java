package co.edu.uniquindio.cinemauq.model;

import co.edu.uniquindio.cinemauq.model.enums.EstadoSolicitud;

import java.time.LocalDateTime;

/** El cliente pide la recarga; solo el administrador la aprueba. */
public class SolicitudRecarga {

    private final String codigo;
    private final Cliente cliente;
    private final double monto;
    private final LocalDateTime fecha;
    private EstadoSolicitud estado = EstadoSolicitud.PENDIENTE;

    public SolicitudRecarga(String codigo, Cliente cliente, double monto, LocalDateTime fecha) {
        this.codigo = codigo;
        this.cliente = cliente;
        this.monto = monto;
        this.fecha = fecha;
    }

    public String getCodigo() { return codigo; }
    public Cliente getCliente() { return cliente; }
    public double getMonto() { return monto; }
    public LocalDateTime getFecha() { return fecha; }
    public EstadoSolicitud getEstado() { return estado; }
    public void setEstado(EstadoSolicitud estado) { this.estado = estado; }
}
