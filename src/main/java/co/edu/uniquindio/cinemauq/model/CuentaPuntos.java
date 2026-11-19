package co.edu.uniquindio.cinemauq.model;

import co.edu.uniquindio.cinemauq.exceptions.PuntosInsuficientesException;
import co.edu.uniquindio.cinemauq.model.enums.TipoMovimientoPuntos;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Cuenta de puntos CinemaUQ. Guarda los lotes con su fecha de obtención.
 * Al redimir se usan primero los lotes más próximos a vencer y nunca los vencidos.
 */
public class CuentaPuntos {

    private final List<LotePuntos> lotes = new ArrayList<>();
    private final List<MovimientoPuntos> movimientos = new ArrayList<>();

    public void acumular(int puntos, LocalDateTime fecha, String descripcion) {
        if (puntos <= 0) {
            return;
        }
        lotes.add(new LotePuntos(puntos, fecha.toLocalDate()));
        movimientos.add(new MovimientoPuntos(fecha, TipoMovimientoPuntos.ACUMULACION, puntos, descripcion));
    }

    public int getPuntosVigentes(LocalDate hoy) {
        return lotes.stream().filter(l -> l.isVigente(hoy)).mapToInt(LotePuntos::getPuntosDisponibles).sum();
    }

    public int getPuntosVencidos(LocalDate hoy) {
        return lotes.stream().filter(l -> !l.isVigente(hoy)).mapToInt(LotePuntos::getPuntosDisponibles).sum();
    }

    public void redimir(int puntos, LocalDateTime fecha, String descripcion) throws PuntosInsuficientesException {
        int vigentes = getPuntosVigentes(fecha.toLocalDate());
        if (puntos > vigentes) {
            throw new PuntosInsuficientesException("Puntos insuficientes. Necesita " + puntos
                    + " puntos y tiene " + vigentes + " puntos vigentes.");
        }
        consumirPrimeroLosProximosAVencer(puntos, fecha.toLocalDate());
        movimientos.add(new MovimientoPuntos(fecha, TipoMovimientoPuntos.REDENCION, puntos, descripcion));
    }

    /** Devuelve puntos (por ejemplo cuando se reembolsa una compra pagada con puntos). */
    public void devolver(int puntos, LocalDateTime fecha, String descripcion) {
        if (puntos <= 0) {
            return;
        }
        lotes.add(new LotePuntos(puntos, fecha.toLocalDate()));
        movimientos.add(new MovimientoPuntos(fecha, TipoMovimientoPuntos.DEVOLUCION, puntos, descripcion));
    }

    /** Quita los puntos que generó una compra reembolsada (solo los que el cliente aún tenga disponibles). */
    public int anular(int puntos, LocalDateTime fecha, String descripcion) {
        int aQuitar = Math.min(puntos, getPuntosVigentes(fecha.toLocalDate()));
        if (aQuitar > 0) {
            consumirPrimeroLosProximosAVencer(aQuitar, fecha.toLocalDate());
            movimientos.add(new MovimientoPuntos(fecha, TipoMovimientoPuntos.ANULACION, aQuitar, descripcion));
        }
        return aQuitar;
    }

    private void consumirPrimeroLosProximosAVencer(int puntos, LocalDate hoy) {
        List<LotePuntos> vigentes = lotes.stream()
                .filter(l -> l.isVigente(hoy) && l.getPuntosDisponibles() > 0)
                .sorted(Comparator.comparing(LotePuntos::getFechaVencimiento))
                .toList();
        int restante = puntos;
        for (LotePuntos lote : vigentes) {
            int usar = Math.min(restante, lote.getPuntosDisponibles());
            lote.consumir(usar);
            restante -= usar;
            if (restante == 0) {
                break;
            }
        }
    }

    public List<LotePuntos> getLotes() { return Collections.unmodifiableList(lotes); }
    public List<MovimientoPuntos> getMovimientos() { return Collections.unmodifiableList(movimientos); }
}
