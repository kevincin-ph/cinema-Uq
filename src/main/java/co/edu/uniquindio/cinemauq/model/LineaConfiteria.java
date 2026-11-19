package co.edu.uniquindio.cinemauq.model;

import co.edu.uniquindio.cinemauq.model.confiteria.ItemConfiteria;

/** Línea de la compra con un producto o combo y su cantidad. */
public class LineaConfiteria {

    private final ItemConfiteria item;
    private final int cantidad;
    private final Cobrable cobrable; // puede venir decorado con una promoción

    public LineaConfiteria(ItemConfiteria item, int cantidad, Cobrable cobrable) {
        this.item = item;
        this.cantidad = cantidad;
        this.cobrable = cobrable;
    }

    public double getSubtotal() { return cobrable.getPrecio() * cantidad; }
    public double getSubtotalSinDescuento() { return item.getPrecio() * cantidad; }
    public ItemConfiteria getItem() { return item; }
    public int getCantidad() { return cantidad; }
    public String getDescripcion() { return cantidad + " x " + cobrable.getDescripcion(); }
}
