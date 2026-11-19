package co.edu.uniquindio.cinemauq.reembolso;

import co.edu.uniquindio.cinemauq.model.Funcion;

import java.time.Duration;
import java.time.LocalDateTime;

/** Escoge la estrategia de reembolso según el tiempo que falta para la función. */
public final class SelectorPoliticaReembolso {

    private SelectorPoliticaReembolso() { }

    public static PoliticaReembolso seleccionar(Funcion funcion, LocalDateTime ahora, boolean canceladaPorCine) {
        if (canceladaPorCine) {
            return new ReembolsoCancelacionCine();
        }
        long minutos = Duration.between(ahora, funcion.getFechaHora()).toMinutes();
        if (minutos > 24 * 60) {
            return new ReembolsoTotal();
        }
        if (minutos >= 2 * 60) {
            return new ReembolsoParcial();
        }
        return new SinReembolso();
    }
}
