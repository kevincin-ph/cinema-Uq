package co.edu.uniquindio.cinemauq.model.entradas;

import co.edu.uniquindio.cinemauq.model.Asiento;
import co.edu.uniquindio.cinemauq.model.Funcion;

public class CreadorEntradaPreferencial extends CreadorEntrada {
    @Override
    protected Entrada crearEntrada(String codigo, Funcion funcion, Asiento asiento) {
        return new EntradaPreferencial(codigo, funcion, asiento);
    }
}
