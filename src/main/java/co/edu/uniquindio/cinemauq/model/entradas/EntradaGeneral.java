package co.edu.uniquindio.cinemauq.model.entradas;

import co.edu.uniquindio.cinemauq.model.Asiento;
import co.edu.uniquindio.cinemauq.model.Funcion;

public class EntradaGeneral extends Entrada {

    public EntradaGeneral(String codigo, Funcion funcion, Asiento asiento) {
        super(codigo, funcion, asiento);
    }

    @Override
    public String getTipo() { return "General"; }

    @Override
    public double getPrecio() { return getFuncion().getPrecioBase(); }
}
