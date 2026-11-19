package co.edu.uniquindio.cinemauq.model.entradas;

import co.edu.uniquindio.cinemauq.model.Asiento;
import co.edu.uniquindio.cinemauq.model.Funcion;
import co.edu.uniquindio.cinemauq.model.enums.TipoAsiento;

/**
 * Patrón Factory Method (creador abstracto).
 * Cada subclase decide qué tipo de Entrada crear. El resto del sistema solo usa crear().
 */
public abstract class CreadorEntrada {

    public Entrada crear(Funcion funcion, Asiento asiento) {
        return crearEntrada("POR-CONFIRMAR", funcion, asiento);
    }

    /** Método fábrica. */
    protected abstract Entrada crearEntrada(String codigo, Funcion funcion, Asiento asiento);

    /** Devuelve el creador adecuado según el tipo de asiento. */
    public static CreadorEntrada para(TipoAsiento tipo) {
        return tipo == TipoAsiento.PREFERENCIAL ? new CreadorEntradaPreferencial() : new CreadorEntradaGeneral();
    }
}
