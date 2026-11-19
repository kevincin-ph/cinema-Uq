package co.edu.uniquindio.cinemauq.service;

import co.edu.uniquindio.cinemauq.data.BaseDatos;
import co.edu.uniquindio.cinemauq.exceptions.CancelacionNoPermitidaException;
import co.edu.uniquindio.cinemauq.exceptions.CinemaException;
import co.edu.uniquindio.cinemauq.exceptions.FuncionNoDisponibleException;
import co.edu.uniquindio.cinemauq.model.Cliente;
import co.edu.uniquindio.cinemauq.model.Compra;
import co.edu.uniquindio.cinemauq.model.Funcion;
import co.edu.uniquindio.cinemauq.model.Reembolso;
import co.edu.uniquindio.cinemauq.model.entradas.Entrada;
import co.edu.uniquindio.cinemauq.model.enums.MetodoPago;
import co.edu.uniquindio.cinemauq.pagos.MedioPago;
import co.edu.uniquindio.cinemauq.pagos.PuntosAdapter;
import co.edu.uniquindio.cinemauq.pagos.TarjetaVirtualAdapter;
import co.edu.uniquindio.cinemauq.puntos.ReglaPuntos;
import co.edu.uniquindio.cinemauq.reembolso.PoliticaReembolso;
import co.edu.uniquindio.cinemauq.reembolso.SelectorPoliticaReembolso;
import co.edu.uniquindio.cinemauq.util.Formato;
import co.edu.uniquindio.cinemauq.util.GeneradorCodigo;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * SRP: confirma compras, cancela compras y procesa reembolsos.
 * DIP: depende de MedioPago, PoliticaReembolso y ReglaPuntos (abstracciones), no de clases concretas.
 */
public class CompraService {

    private final BaseDatos db;
    private final ReglaPuntos reglaPuntos;

    public CompraService(BaseDatos db, ReglaPuntos reglaPuntos) {
        this.db = db;
        this.reglaPuntos = reglaPuntos;
    }

    public Compra confirmarCompra(Compra compra, MetodoPago metodo, LocalDateTime ahora) throws CinemaException {
        Funcion funcion = compra.getFuncion();
        if (!funcion.isProgramada() || !funcion.getFechaHora().isAfter(ahora)) {
            throw new FuncionNoDisponibleException("La función ya no está disponible para la venta.");
        }
        // 1. Verificar asientos (nadie los compró mientras el cliente elegía)
        for (Entrada e : compra.getEntradas()) {
            if (!funcion.isAsientoDisponible(e.getAsiento())) {
                throw new co.edu.uniquindio.cinemauq.exceptions.AsientoNoDisponibleException("El asiento " + e.getAsiento() + " acaba de ser vendido.");
            }
        }
        // 2. Verificar y cobrar con el medio de pago (tarjeta activa y saldo suficiente, o puntos suficientes)
        Cliente cliente = compra.getCliente();
        MedioPago medio = crearMedioPago(cliente, metodo);
        medio.validar(compra.getTotal(), ahora);
        medio.cobrar(compra.getTotal(), "Compra de entradas - " + funcion.getPelicula().getTitulo(), ahora);

        // 3. Ocupar asientos y marcar la compra como pagada (State)
        for (Entrada e : compra.getEntradas()) {
            funcion.ocuparAsiento(e.getAsiento());
        }
        compra.asignarCodigo(GeneradorCodigo.siguiente("COM"));
        for (Entrada e : compra.getEntradas()) {
            e.asignarCodigo(GeneradorCodigo.siguiente("ENT"));
        }
        compra.setFecha(ahora);
        compra.setMetodoPago(metodo);
        compra.pagar();

        // 4. Puntos: se ganan solo con pagos en dinero
        if (metodo == MetodoPago.TARJETA_VIRTUAL) {
            int puntos = reglaPuntos.calcularPuntosGanados(compra.getTotal());
            cliente.getCuentaPuntos().acumular(puntos, ahora, "Compra " + compra.getCodigo());
            compra.setPuntosGanados(puntos);
        } else {
            compra.setPuntosUsados(reglaPuntos.puntosNecesarios(compra.getTotal()));
        }

        // 5. El cliente queda suscrito a la función (Observer)
        funcion.agregarObservador(cliente);
        db.getCompras().add(compra);
        cliente.agregarCompra(compra);
        cliente.agregarNotificacion("Compra " + compra.getCodigo() + " confirmada por " + Formato.moneda(compra.getTotal())
                + (compra.getPuntosGanados() > 0 ? ". Ganó " + compra.getPuntosGanados() + " puntos." : "."));
        return compra;
    }

    /** Muestra al cliente qué política aplicaría si cancela ahora. */
    public PoliticaReembolso consultarPolitica(Compra compra, LocalDateTime ahora) {
        return SelectorPoliticaReembolso.seleccionar(compra.getFuncion(), ahora, false);
    }

    /** Cancelación solicitada por el cliente. */
    public Reembolso cancelarCompra(Compra compra, LocalDateTime ahora) throws CinemaException {
        if (!compra.getEstado().permiteCancelacion()) {
            throw new CancelacionNoPermitidaException("La compra " + compra.getCodigo() + " no se puede cancelar (estado "
                    + compra.getEstado().getNombre() + ").");
        }
        if (!compra.getFuncion().getFechaHora().isAfter(ahora)) {
            throw new CancelacionNoPermitidaException("La función ya se realizó, no se puede cancelar.");
        }
        PoliticaReembolso politica = consultarPolitica(compra, ahora);
        if (!politica.permiteCancelacion()) {
            throw new CancelacionNoPermitidaException(politica.getDescripcion() + ".");
        }
        return procesarReembolso(compra, politica, ahora, "Cancelación solicitada por el cliente");
    }

    /** Cuando el cine cancela una función se devuelve el 100 % a todos los compradores. */
    public List<Reembolso> cancelarFuncion(Funcion funcion, LocalDateTime ahora) throws CinemaException {
        if (!funcion.isProgramada()) {
            throw new CinemaException("La función ya estaba cancelada.");
        }
        List<Reembolso> reembolsos = new ArrayList<>();
        PoliticaReembolso politica = SelectorPoliticaReembolso.seleccionar(funcion, ahora, true);
        for (Compra c : new ArrayList<>(db.getCompras())) {
            if (c.getFuncion() == funcion && c.getEstado().permiteCancelacion()) {
                reembolsos.add(procesarReembolso(c, politica, ahora, "Función cancelada por el cine"));
            }
        }
        funcion.cancelar(); // notifica a los observadores (clientes y bitácora)
        return reembolsos;
    }

    private Reembolso procesarReembolso(Compra compra, PoliticaReembolso politica, LocalDateTime ahora, String motivo)
            throws CinemaException {
        double monto = compra.getTotal() * politica.getPorcentaje() / 100.0;
        Reembolso reembolso = new Reembolso(GeneradorCodigo.siguiente("REE"), ahora, politica.getPorcentaje(), monto, motivo);
        // Primero el estado: si ya estaba reembolsada lanza ReembolsoDuplicadoException y no se mueve dinero.
        compra.reembolsar(reembolso);

        Cliente cliente = compra.getCliente();
        MedioPago medio = crearMedioPago(cliente, compra.getMetodoPago());
        medio.devolver(monto, "Reembolso compra " + compra.getCodigo(), ahora);
        for (Entrada e : compra.getEntradas()) {
            compra.getFuncion().liberarAsiento(e.getAsiento());
        }
        if (compra.getPuntosGanados() > 0) {
            cliente.getCuentaPuntos().anular(compra.getPuntosGanados(), ahora, "Anulación por reembolso " + compra.getCodigo());
        }
        cliente.agregarNotificacion("Reembolso de " + Formato.moneda(monto) + " (" + (int) politica.getPorcentaje()
                + "%) por la compra " + compra.getCodigo() + ".");
        return reembolso;
    }

    /** Fábrica sencilla del medio de pago (Adapter según el método). */
    private MedioPago crearMedioPago(Cliente cliente, MetodoPago metodo) {
        return metodo == MetodoPago.PUNTOS
                ? new PuntosAdapter(cliente.getCuentaPuntos(), reglaPuntos)
                : new TarjetaVirtualAdapter(cliente.getTarjeta());
    }

    public List<Compra> listarCompras() { return db.getCompras(); }

    public ReglaPuntos getReglaPuntos() { return reglaPuntos; }
}
