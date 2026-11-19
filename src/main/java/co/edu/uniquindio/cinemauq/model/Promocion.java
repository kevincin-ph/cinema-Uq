package co.edu.uniquindio.cinemauq.model;

import co.edu.uniquindio.cinemauq.model.enums.AplicaPromocion;

public class Promocion {

    private final String codigo;
    private final String nombre;
    private final double porcentaje;
    private final AplicaPromocion aplicaA;
    private boolean activa = true;

    public Promocion(String codigo, String nombre, double porcentaje, AplicaPromocion aplicaA) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.porcentaje = porcentaje;
        this.aplicaA = aplicaA;
    }

    public boolean aplicaAEntradas() { return aplicaA == AplicaPromocion.ENTRADAS || aplicaA == AplicaPromocion.TODO; }
    public boolean aplicaAConfiteria() { return aplicaA == AplicaPromocion.CONFITERIA || aplicaA == AplicaPromocion.TODO; }

    public String getCodigo() { return codigo; }
    public String getNombre() { return nombre; }
    public double getPorcentaje() { return porcentaje; }
    public AplicaPromocion getAplicaA() { return aplicaA; }
    public boolean isActiva() { return activa; }
    public void setActiva(boolean activa) { this.activa = activa; }

    @Override
    public String toString() { return nombre + " (-" + (int) porcentaje + "% " + aplicaA + ")"; }
}
