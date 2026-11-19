package co.edu.uniquindio.cinemauq.reembolso;

/** Patrón Strategy: cada política sabe qué porcentaje devolver. */
public interface PoliticaReembolso {
    double getPorcentaje();
    String getDescripcion();

    default boolean permiteCancelacion() { return getPorcentaje() > 0; }
}
