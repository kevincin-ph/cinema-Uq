package co.edu.uniquindio.cinemauq.model.confiteria;

import co.edu.uniquindio.cinemauq.model.enums.CategoriaProducto;

/** Composite (hoja): producto individual de confitería. */
public class ProductoConfiteria implements ItemConfiteria {

    private final String codigo;
    private final String nombre;
    private final double precio;
    private final CategoriaProducto categoria;

    public ProductoConfiteria(String codigo, String nombre, double precio, CategoriaProducto categoria) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.precio = precio;
        this.categoria = categoria;
    }

    @Override public String getCodigo() { return codigo; }
    @Override public String getNombre() { return nombre; }
    @Override public CategoriaProducto getCategoria() { return categoria; }
    @Override public double getPrecio() { return precio; }
    @Override public String getDescripcion() { return nombre; }

    @Override
    public String toString() { return nombre; }
}
