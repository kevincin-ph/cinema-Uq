package co.edu.uniquindio.cinemauq.model;

import co.edu.uniquindio.cinemauq.model.enums.TipoAsiento;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Sala del cine. Composición: los asientos se crean con la sala y no existen sin ella.
 * Las últimas filas de la sala son preferenciales.
 */
public class Sala {

    private final String codigo;
    private final String nombre;
    private final int filas;
    private final int columnas;
    private final int filasPreferenciales;
    private final List<Asiento> asientos = new ArrayList<>();

    public Sala(String codigo, String nombre, int filas, int columnas, int filasPreferenciales) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.filas = filas;
        this.columnas = columnas;
        this.filasPreferenciales = filasPreferenciales;
        generarAsientos();
    }

    private void generarAsientos() {
        for (int i = 0; i < filas; i++) {
            char letra = (char) ('A' + i);
            TipoAsiento tipo = i >= filas - filasPreferenciales ? TipoAsiento.PREFERENCIAL : TipoAsiento.GENERAL;
            for (int j = 1; j <= columnas; j++) {
                asientos.add(new Asiento(letra, j, tipo));
            }
        }
    }

    public Asiento buscarAsiento(String codigoAsiento) {
        return asientos.stream().filter(a -> a.getCodigo().equals(codigoAsiento)).findFirst().orElse(null);
    }

    public int getCapacidad() { return asientos.size(); }
    public String getCodigo() { return codigo; }
    public String getNombre() { return nombre; }
    public int getFilas() { return filas; }
    public int getColumnas() { return columnas; }
    public int getFilasPreferenciales() { return filasPreferenciales; }
    public List<Asiento> getAsientos() { return Collections.unmodifiableList(asientos); }

    @Override
    public String toString() { return nombre; }
}
