package co.edu.uniquindio.cinemauq.pagos;

import co.edu.uniquindio.cinemauq.exceptions.CinemaException;
import co.edu.uniquindio.cinemauq.model.enums.MetodoPago;

import java.time.LocalDateTime;

/**
 * Patrón Adapter (interfaz objetivo). El servicio de compras solo conoce esta interfaz (DIP).
 * Todo se maneja en pesos, aunque el medio real trabaje con otra unidad (por ejemplo puntos).
 */
public interface MedioPago {
    void validar(double monto, LocalDateTime fecha) throws CinemaException;
    void cobrar(double monto, String concepto, LocalDateTime fecha) throws CinemaException;
    void devolver(double monto, String concepto, LocalDateTime fecha);
    MetodoPago getMetodo();
}
