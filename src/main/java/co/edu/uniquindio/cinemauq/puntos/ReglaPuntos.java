package co.edu.uniquindio.cinemauq.puntos;

/**
 * Regla de acumulación y equivalencia de puntos.
 * OCP/DIP: si el cine cambia la regla se crea otra implementación sin tocar CompraService.
 */
public interface ReglaPuntos {
    int calcularPuntosGanados(double montoPagado);
    int puntosNecesarios(double valor);
    String getDescripcion();
}
