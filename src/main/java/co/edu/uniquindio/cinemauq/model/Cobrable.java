package co.edu.uniquindio.cinemauq.model;

/** Cualquier cosa que se pueda cobrar en una compra (entradas, productos, combos). ISP: interfaz pequeña. */
public interface Cobrable {
    String getDescripcion();
    double getPrecio();
}
