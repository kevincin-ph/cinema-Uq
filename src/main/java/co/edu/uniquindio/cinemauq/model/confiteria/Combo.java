package co.edu.uniquindio.cinemauq.model.confiteria;

import co.edu.uniquindio.cinemauq.model.enums.CategoriaProducto;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Composite (compuesto): un combo contiene productos u otros combos.
 * Su precio es la suma de sus partes con un descuento.
 */
public class Combo implements ItemConfiteria {

    private final String codigo;
    private final String nombre;
    private final double porcentajeDescuento;
    private final List<ItemConfiteria> items = new ArrayList<>();

    public Combo(String codigo, String nombre, double porcentajeDescuento) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.porcentajeDescuento = porcentajeDescuento;
    }

    public void agregar(ItemConfiteria item) { items.add(item); }

    public void quitar(ItemConfiteria item) { items.remove(item); }

    public List<ItemConfiteria> getItems() { return Collections.unmodifiableList(items); }

    @Override
    public double getPrecio() {
        double suma = items.stream().mapToDouble(ItemConfiteria::getPrecio).sum();
        return suma * (1 - porcentajeDescuento / 100.0);
    }

    @Override
    public String getDescripcion() {
        return nombre + " (" + items.stream().map(ItemConfiteria::getNombre).collect(Collectors.joining(" + ")) + ")";
    }

    @Override public String getCodigo() { return codigo; }
    @Override public String getNombre() { return nombre; }
    @Override public CategoriaProducto getCategoria() { return CategoriaProducto.COMBO; }
    public double getPorcentajeDescuento() { return porcentajeDescuento; }

    @Override
    public String toString() { return nombre; }
}
