package co.edu.uniquindio.cinemauq.model;

import co.edu.uniquindio.cinemauq.exceptions.CinemaException;
import co.edu.uniquindio.cinemauq.model.entradas.Entrada;
import co.edu.uniquindio.cinemauq.model.enums.MetodoPago;
import co.edu.uniquindio.cinemauq.model.estado.EstadoCompra;
import co.edu.uniquindio.cinemauq.model.estado.EstadoPendiente;
import co.edu.uniquindio.cinemauq.util.Formato;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Compra de un cliente: entradas + confitería. Se construye con CompraBuilder.
 * Su estado se maneja con el patrón State.
 */
public class Compra {

    private String codigo;
    private final Cliente cliente;
    private final Funcion funcion;
    private final List<Entrada> entradas;
    private final List<LineaConfiteria> lineasConfiteria;
    private final Promocion promocion;
    private final double subtotalEntradas;
    private final double subtotalConfiteria;
    private final double totalSinDescuento;

    private LocalDateTime fecha;
    private MetodoPago metodoPago;
    private int puntosGanados;
    private int puntosUsados;
    private EstadoCompra estado = new EstadoPendiente();
    private Reembolso reembolso;

    public Compra(String codigo, Cliente cliente, Funcion funcion, List<Entrada> entradas,
                  List<LineaConfiteria> lineasConfiteria, Promocion promocion,
                  double subtotalEntradas, double subtotalConfiteria, double totalSinDescuento) {
        this.codigo = codigo;
        this.cliente = cliente;
        this.funcion = funcion;
        this.entradas = entradas;
        this.lineasConfiteria = lineasConfiteria;
        this.promocion = promocion;
        this.subtotalEntradas = subtotalEntradas;
        this.subtotalConfiteria = subtotalConfiteria;
        this.totalSinDescuento = totalSinDescuento;
    }

    // ---------- State ----------
    public void pagar() throws CinemaException { estado.pagar(this); }

    public void reembolsar(Reembolso reembolso) throws CinemaException { estado.reembolsar(this, reembolso); }

    public void cambiarEstado(EstadoCompra nuevo) { this.estado = nuevo; }

    public void registrarReembolso(Reembolso reembolso) { this.reembolso = reembolso; }

    public boolean isReembolsada() { return reembolso != null; }

    // ---------- Datos calculados ----------
    public double getTotal() { return subtotalEntradas + subtotalConfiteria; }

    public double getDescuento() { return totalSinDescuento - getTotal(); }

    public String getAsientosTexto() {
        return entradas.stream().map(e -> e.getAsiento().getCodigo()).collect(Collectors.joining(", "));
    }

    public String getDetalle() {
        StringBuilder sb = new StringBuilder();
        sb.append("Compra ").append(codigo).append(" - ").append(estado.getNombre()).append('\n');
        sb.append("Función: ").append(funcion).append('\n');
        if (fecha != null) {
            sb.append("Fecha de compra: ").append(Formato.fechaHora(fecha)).append('\n');
        }
        sb.append("\nEntradas:\n");
        for (Entrada e : entradas) {
            sb.append("  ").append(e.getDescripcion()).append("  ").append(Formato.moneda(e.getPrecio())).append('\n');
        }
        if (!lineasConfiteria.isEmpty()) {
            sb.append("Confitería:\n");
            for (LineaConfiteria l : lineasConfiteria) {
                sb.append("  ").append(l.getDescripcion()).append("  ").append(Formato.moneda(l.getSubtotal())).append('\n');
            }
        }
        if (promocion != null) {
            sb.append("Promoción: ").append(promocion.getNombre()).append('\n');
        }
        sb.append("\nSubtotal: ").append(Formato.moneda(totalSinDescuento));
        sb.append("\nDescuento: ").append(Formato.moneda(getDescuento()));
        sb.append("\nTOTAL: ").append(Formato.moneda(getTotal()));
        if (metodoPago != null) {
            sb.append("\nPagado con: ").append(metodoPago);
        }
        if (puntosUsados > 0) {
            sb.append("\nPuntos usados: ").append(puntosUsados);
        }
        if (puntosGanados > 0) {
            sb.append("\nPuntos ganados: ").append(puntosGanados);
        }
        if (reembolso != null) {
            sb.append("\nReembolso: ").append(Formato.moneda(reembolso.getMonto()))
              .append(" (").append((int) reembolso.getPorcentaje()).append("%) - ").append(reembolso.getMotivo());
        }
        return sb.toString();
    }

    public String getCodigo() { return codigo; }
    public void asignarCodigo(String codigo) { this.codigo = codigo; }
    public Cliente getCliente() { return cliente; }
    public Funcion getFuncion() { return funcion; }
    public List<Entrada> getEntradas() { return Collections.unmodifiableList(entradas); }
    public List<LineaConfiteria> getLineasConfiteria() { return Collections.unmodifiableList(lineasConfiteria); }
    public Promocion getPromocion() { return promocion; }
    public double getSubtotalEntradas() { return subtotalEntradas; }
    public double getSubtotalConfiteria() { return subtotalConfiteria; }
    public double getTotalSinDescuento() { return totalSinDescuento; }
    public LocalDateTime getFecha() { return fecha; }
    public void setFecha(LocalDateTime fecha) { this.fecha = fecha; }
    public MetodoPago getMetodoPago() { return metodoPago; }
    public void setMetodoPago(MetodoPago metodoPago) { this.metodoPago = metodoPago; }
    public int getPuntosGanados() { return puntosGanados; }
    public void setPuntosGanados(int puntosGanados) { this.puntosGanados = puntosGanados; }
    public int getPuntosUsados() { return puntosUsados; }
    public void setPuntosUsados(int puntosUsados) { this.puntosUsados = puntosUsados; }
    public EstadoCompra getEstado() { return estado; }
    public Reembolso getReembolso() { return reembolso; }
}
