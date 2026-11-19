package co.edu.uniquindio.cinemauq.controller;

import co.edu.uniquindio.cinemauq.builder.CompraBuilder;
import co.edu.uniquindio.cinemauq.exceptions.CinemaException;
import co.edu.uniquindio.cinemauq.model.Asiento;
import co.edu.uniquindio.cinemauq.model.Cliente;
import co.edu.uniquindio.cinemauq.model.Compra;
import co.edu.uniquindio.cinemauq.model.Funcion;
import co.edu.uniquindio.cinemauq.model.Pelicula;
import co.edu.uniquindio.cinemauq.model.Promocion;
import co.edu.uniquindio.cinemauq.model.Reembolso;
import co.edu.uniquindio.cinemauq.model.confiteria.ItemConfiteria;
import co.edu.uniquindio.cinemauq.model.enums.MetodoPago;
import co.edu.uniquindio.cinemauq.facade.OperacionesCliente;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

/** Controlador de las pantallas del cliente. Depende solo de OperacionesCliente (ISP). */
public class ClienteController {

    private final OperacionesCliente ops;
    private final Cliente cliente;

    public ClienteController(OperacionesCliente ops, Cliente cliente) {
        this.ops = ops;
        this.cliente = cliente;
    }

    public Cliente getCliente() { return cliente; }
    public List<Pelicula> getCartelera() { return ops.getCartelera(); }
    public List<Funcion> getFunciones(Pelicula p) { return ops.getFuncionesDisponibles(p); }
    public List<ItemConfiteria> getConfiteria() { return ops.getConfiteria(); }
    public List<Promocion> getPromociones() { return ops.getPromocionesActivas(); }

    /** Usa el Builder para armar la compra con lo que el cliente seleccionó en pantalla. */
    public Compra armarCompra(Funcion funcion, List<Asiento> asientos, Map<ItemConfiteria, Integer> items, Promocion promo)
            throws CinemaException {
        CompraBuilder builder = ops.nuevaCompra(cliente, funcion).conPromocion(promo);
        for (Asiento a : asientos) {
            builder.conAsiento(a);
        }
        items.forEach(builder::conItem);
        return builder.build();
    }

    public Compra confirmarCompra(Compra compra, MetodoPago metodo) throws CinemaException {
        if (metodo == null) {
            throw new co.edu.uniquindio.cinemauq.exceptions.DatoInvalidoException("Seleccione el método de pago.");
        }
        return ops.confirmarCompra(compra, metodo);
    }

    public int puntosNecesarios(double valor) { return ops.puntosNecesarios(valor); }

    public String politicaCancelacion(Compra compra) { return ops.consultarPoliticaCancelacion(compra).getDescripcion(); }

    public Reembolso cancelar(Compra compra) throws CinemaException { return ops.cancelarCompra(compra); }

    public void solicitarRecarga(String montoTexto) throws CinemaException {
        ops.solicitarRecarga(cliente, Validador.decimal(montoTexto, "Monto"));
    }

    public int getPuntosVigentes() { return ops.getPuntosVigentes(cliente); }

    public String getReglaPuntos() { return ops.getReglaPuntos(); }

    public List<Compra> getCompras() {
        List<Compra> lista = new ArrayList<>(cliente.getCompras());
        lista.sort(Comparator.comparing(Compra::getFecha).reversed());
        return lista;
    }
}
