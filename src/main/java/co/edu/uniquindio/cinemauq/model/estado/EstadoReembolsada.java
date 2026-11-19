package co.edu.uniquindio.cinemauq.model.estado;

import co.edu.uniquindio.cinemauq.exceptions.CinemaException;
import co.edu.uniquindio.cinemauq.exceptions.ReembolsoDuplicadoException;
import co.edu.uniquindio.cinemauq.model.Compra;
import co.edu.uniquindio.cinemauq.model.Reembolso;

/** Una compra reembolsada no se puede volver a reembolsar (evita el doble reembolso). */
public class EstadoReembolsada implements EstadoCompra {

    @Override public String getNombre() { return "CANCELADA/REEMBOLSADA"; }

    @Override
    public void pagar(Compra compra) throws CinemaException {
        throw new CinemaException("La compra fue cancelada, no se puede pagar.");
    }

    @Override
    public void reembolsar(Compra compra, Reembolso reembolso) throws ReembolsoDuplicadoException {
        throw new ReembolsoDuplicadoException("La compra " + compra.getCodigo() + " ya fue reembolsada.");
    }

    @Override public boolean permiteCancelacion() { return false; }
}
