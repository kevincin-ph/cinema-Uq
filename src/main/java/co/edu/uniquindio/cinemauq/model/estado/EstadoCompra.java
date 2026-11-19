package co.edu.uniquindio.cinemauq.model.estado;

import co.edu.uniquindio.cinemauq.exceptions.CinemaException;
import co.edu.uniquindio.cinemauq.model.Compra;
import co.edu.uniquindio.cinemauq.model.Reembolso;

/** Patrón State: el comportamiento de la compra depende de su estado. */
public interface EstadoCompra {
    String getNombre();
    void pagar(Compra compra) throws CinemaException;
    void reembolsar(Compra compra, Reembolso reembolso) throws CinemaException;
    boolean permiteCancelacion();
}
