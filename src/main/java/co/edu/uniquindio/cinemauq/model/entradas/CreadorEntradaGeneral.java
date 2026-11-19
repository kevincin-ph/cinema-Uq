package co.edu.uniquindio.cinemauq.model.entradas;

import co.edu.uniquindio.cinemauq.model.Asiento;
import co.edu.uniquindio.cinemauq.model.Funcion;

public class CreadorEntradaGeneral extends CreadorEntrada {
    @Override
    protected Entrada crearEntrada(String codigo, Funcion funcion, Asiento asiento) {
        return new EntradaGeneral(codigo, funcion, asiento);
    }
}
