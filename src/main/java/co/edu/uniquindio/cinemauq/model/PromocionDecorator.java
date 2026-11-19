package co.edu.uniquindio.cinemauq.model;

/**
 * Patrón Decorator: envuelve cualquier Cobrable (entrada, producto o combo) y le aplica
 * el descuento de una promoción sin modificar la clase original.
 */
public class PromocionDecorator implements Cobrable {

    private final Cobrable cobrable;
    private final Promocion promocion;

    public PromocionDecorator(Cobrable cobrable, Promocion promocion) {
        this.cobrable = cobrable;
        this.promocion = promocion;
    }

    @Override
    public String getDescripcion() {
        return cobrable.getDescripcion() + " [" + promocion.getNombre() + " -" + (int) promocion.getPorcentaje() + "%]";
    }

    @Override
    public double getPrecio() {
        return cobrable.getPrecio() * (1 - promocion.getPorcentaje() / 100.0);
    }
}
