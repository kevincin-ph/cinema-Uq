package co.edu.uniquindio.cinemauq.pagos;

import co.edu.uniquindio.cinemauq.exceptions.CinemaException;
import co.edu.uniquindio.cinemauq.model.TarjetaVirtual;
import co.edu.uniquindio.cinemauq.model.enums.MetodoPago;
import co.edu.uniquindio.cinemauq.model.enums.TipoMovimiento;

import java.time.LocalDateTime;

/** Adapter: adapta TarjetaVirtual (debitar/acreditar) a la interfaz MedioPago. */
public class TarjetaVirtualAdapter implements MedioPago {

    private final TarjetaVirtual tarjeta;

    public TarjetaVirtualAdapter(TarjetaVirtual tarjeta) {
        this.tarjeta = tarjeta;
    }

    @Override
    public void validar(double monto, LocalDateTime fecha) throws CinemaException {
        tarjeta.verificarPago(monto);
    }

    @Override
    public void cobrar(double monto, String concepto, LocalDateTime fecha) throws CinemaException {
        tarjeta.debitar(monto, concepto, fecha);
    }

    @Override
    public void devolver(double monto, String concepto, LocalDateTime fecha) {
        if (monto > 0) {
            tarjeta.acreditar(monto, TipoMovimiento.REEMBOLSO, concepto, fecha);
        }
    }

    @Override
    public MetodoPago getMetodo() { return MetodoPago.TARJETA_VIRTUAL; }
}
