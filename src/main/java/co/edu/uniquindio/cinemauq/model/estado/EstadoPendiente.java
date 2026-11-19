package co.edu.uniquindio.cinemauq.model.estado;

import co.edu.uniquindio.cinemauq.exceptions.CancelacionNoPermitidaException;
import co.edu.uniquindio.cinemauq.model.Compra;
import co.edu.uniquindio.cinemauq.model.Reembolso;

public class EstadoPendiente implements EstadoCompra {

    @Override public String getNombre() { return "PENDIENTE"; }

    @Override
    public void pagar(Compra compra) {
        compra.cambiarEstado(new EstadoPagada());
    }

    @Override
    public void reembolsar(Compra compra, Reembolso reembolso) throws CancelacionNoPermitidaException {
        throw new CancelacionNoPermitidaException("La compra aún no ha sido pagada.");
    }

    @Override public boolean permiteCancelacion() { return false; }
}
