package co.edu.uniquindio.cinemauq.model.entradas;

import co.edu.uniquindio.cinemauq.model.Asiento;
import co.edu.uniquindio.cinemauq.model.Cobrable;
import co.edu.uniquindio.cinemauq.model.Funcion;

/** Entrada a una función para un asiento. Las subclases definen el precio (polimorfismo). */
public abstract class Entrada implements Cobrable {

    private String codigo;
    private final Funcion funcion;
    private final Asiento asiento;

    protected Entrada(String codigo, Funcion funcion, Asiento asiento) {
        this.codigo = codigo;
        this.funcion = funcion;
        this.asiento = asiento;
    }

    public abstract String getTipo();

    @Override
    public String getDescripcion() {
        return "Entrada " + getTipo() + " " + asiento + " - " + funcion.getPelicula().getTitulo();
    }

    public String getCodigo() { return codigo; }
    public void asignarCodigo(String codigo) { this.codigo = codigo; }
    public Funcion getFuncion() { return funcion; }
    public Asiento getAsiento() { return asiento; }
}
