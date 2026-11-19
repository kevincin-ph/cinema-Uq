package co.edu.uniquindio.cinemauq.pagos;

import co.edu.uniquindio.cinemauq.exceptions.CinemaException;
import co.edu.uniquindio.cinemauq.exceptions.PuntosInsuficientesException;
import co.edu.uniquindio.cinemauq.model.CuentaPuntos;
import co.edu.uniquindio.cinemauq.model.enums.MetodoPago;
import co.edu.uniquindio.cinemauq.puntos.ReglaPuntos;

import java.time.LocalDateTime;

/**
 * Adapter: la CuentaPuntos trabaja en puntos, pero MedioPago trabaja en pesos.
 * Este adaptador convierte pesos a puntos usando la regla de equivalencia.
 */
public class PuntosAdapter implements MedioPago {

    private final CuentaPuntos cuenta;
    private final ReglaPuntos regla;

    public PuntosAdapter(CuentaPuntos cuenta, ReglaPuntos regla) {
        this.cuenta = cuenta;
        this.regla = regla;
    }

    @Override
    public void validar(double monto, LocalDateTime fecha) throws CinemaException {
        int necesarios = regla.puntosNecesarios(monto);
        int vigentes = cuenta.getPuntosVigentes(fecha.toLocalDate());
        if (vigentes < necesarios) {
            throw new PuntosInsuficientesException("Necesita " + necesarios + " puntos y tiene " + vigentes + " puntos vigentes.");
        }
    }

    @Override
    public void cobrar(double monto, String concepto, LocalDateTime fecha) throws CinemaException {
        cuenta.redimir(regla.puntosNecesarios(monto), fecha, concepto);
    }

    @Override
    public void devolver(double monto, String concepto, LocalDateTime fecha) {
        cuenta.devolver(regla.puntosNecesarios(monto), fecha, concepto);
    }

    @Override
    public MetodoPago getMetodo() { return MetodoPago.PUNTOS; }
}
