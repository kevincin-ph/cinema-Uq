package co.edu.uniquindio.cinemauq.model.entradas;

import co.edu.uniquindio.cinemauq.model.Asiento;
import co.edu.uniquindio.cinemauq.model.Funcion;

public class EntradaPreferencial extends Entrada {

    /** Las sillas preferenciales cuestan 30 % más que el precio base. */
    public static final double RECARGO = 0.30;

    public EntradaPreferencial(String codigo, Funcion funcion, Asiento asiento) {
        super(codigo, funcion, asiento);
    }

    @Override
    public String getTipo() { return "Preferencial"; }

    @Override
    public double getPrecio() { return getFuncion().getPrecioBase() * (1 + RECARGO); }
}
