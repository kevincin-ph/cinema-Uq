package co.edu.uniquindio.cinemauq.model.estado;

import co.edu.uniquindio.cinemauq.exceptions.CinemaException;
import co.edu.uniquindio.cinemauq.model.Compra;
import co.edu.uniquindio.cinemauq.model.Reembolso;

public class EstadoPagada implements EstadoCompra {

    @Override public String getNombre() { return "PAGADA"; }

    @Override
    public void pagar(Compra compra) throws CinemaException {
        throw new CinemaException("La compra ya fue pagada.");
    }

    @Override
    public void reembolsar(Compra compra, Reembolso reembolso) {
        compra.registrarReembolso(reembolso);
        compra.cambiarEstado(new EstadoReembolsada());
    }

    @Override public boolean permiteCancelacion() { return true; }
}
