package co.edu.uniquindio.cinemauq.builder;

import co.edu.uniquindio.cinemauq.exceptions.AsientoNoDisponibleException;
import co.edu.uniquindio.cinemauq.exceptions.DatoInvalidoException;
import co.edu.uniquindio.cinemauq.model.Asiento;
import co.edu.uniquindio.cinemauq.model.Cliente;
import co.edu.uniquindio.cinemauq.model.Cobrable;
import co.edu.uniquindio.cinemauq.model.Compra;
import co.edu.uniquindio.cinemauq.model.Funcion;
import co.edu.uniquindio.cinemauq.model.LineaConfiteria;
import co.edu.uniquindio.cinemauq.model.Promocion;
import co.edu.uniquindio.cinemauq.model.PromocionDecorator;
import co.edu.uniquindio.cinemauq.model.confiteria.ItemConfiteria;
import co.edu.uniquindio.cinemauq.model.entradas.CreadorEntrada;
import co.edu.uniquindio.cinemauq.model.entradas.Entrada;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Patrón Builder: arma la compra paso a paso (asientos, confitería, promoción)
 * y al final valida y calcula los totales.
 */
public class CompraBuilder {

    public static final int MAX_ENTRADAS = 10;

    private final Cliente cliente;
    private final Funcion funcion;
    private final List<Asiento> asientos = new ArrayList<>();
    private final Map<ItemConfiteria, Integer> confiteria = new LinkedHashMap<>();
    private Promocion promocion;

    public CompraBuilder(Cliente cliente, Funcion funcion) {
        this.cliente = cliente;
        this.funcion = funcion;
    }

    public CompraBuilder conAsiento(Asiento asiento) throws AsientoNoDisponibleException {
        if (!funcion.isAsientoDisponible(asiento) || asientos.contains(asiento)) {
            throw new AsientoNoDisponibleException("El asiento " + asiento + " no está disponible.");
        }
        asientos.add(asiento);
        return this;
    }

    public CompraBuilder conItem(ItemConfiteria item, int cantidad) {
        if (cantidad > 0) {
            confiteria.merge(item, cantidad, Integer::sum);
        }
        return this;
    }

    public CompraBuilder conPromocion(Promocion promocion) {
        this.promocion = promocion != null && promocion.isActiva() ? promocion : null;
        return this;
    }

    public Compra build() throws DatoInvalidoException {
        if (asientos.isEmpty()) {
            throw new DatoInvalidoException("Debe seleccionar al menos un asiento.");
        }
        if (asientos.size() > MAX_ENTRADAS) {
            throw new DatoInvalidoException("Máximo " + MAX_ENTRADAS + " entradas por compra.");
        }
        List<Entrada> entradas = new ArrayList<>();
        double subtotalEntradas = 0;
        double totalSinDescuento = 0;
        for (Asiento a : asientos) {
            Entrada entrada = CreadorEntrada.para(a.getTipo()).crear(funcion, a); // Factory Method
            entradas.add(entrada);
            Cobrable cobrable = promocion != null && promocion.aplicaAEntradas()
                    ? new PromocionDecorator(entrada, promocion) : entrada; // Decorator
            subtotalEntradas += cobrable.getPrecio();
            totalSinDescuento += entrada.getPrecio();
        }
        List<LineaConfiteria> lineas = new ArrayList<>();
        double subtotalConfiteria = 0;
        for (Map.Entry<ItemConfiteria, Integer> e : confiteria.entrySet()) {
            Cobrable cobrable = promocion != null && promocion.aplicaAConfiteria()
                    ? new PromocionDecorator(e.getKey(), promocion) : e.getKey();
            LineaConfiteria linea = new LineaConfiteria(e.getKey(), e.getValue(), cobrable);
            lineas.add(linea);
            subtotalConfiteria += linea.getSubtotal();
            totalSinDescuento += linea.getSubtotalSinDescuento();
        }
        return new Compra("POR-CONFIRMAR", cliente, funcion, entradas, lineas,
                promocion, subtotalEntradas, subtotalConfiteria, totalSinDescuento);
    }
}
