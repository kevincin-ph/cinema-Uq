package co.edu.uniquindio.cinemauq.puntos;

/**
 * Regla definida por el grupo:
 * - Acumulación: 1 punto por cada $100 pagados con la tarjeta virtual.
 * - Redención: cada punto equivale a $10 (entradas, productos o combos).
 *   Ej.: una entrada de $12.000 cuesta 1.200 puntos.
 */
public class ReglaPuntosCinemaUQ implements ReglaPuntos {

    public static final double PESOS_POR_PUNTO_GANADO = 100;
    public static final double VALOR_PUNTO = 10;

    @Override
    public int calcularPuntosGanados(double montoPagado) {
        return (int) Math.floor(montoPagado / PESOS_POR_PUNTO_GANADO);
    }

    @Override
    public int puntosNecesarios(double valor) {
        return (int) Math.ceil(valor / VALOR_PUNTO);
    }

    @Override
    public String getDescripcion() {
        return "Gana 1 punto por cada $100 pagados con tarjeta. Cada punto vale $10 al redimir. "
                + "Los puntos vencen 1 año después de obtenidos y se usan primero los más próximos a vencer.";
    }
}
