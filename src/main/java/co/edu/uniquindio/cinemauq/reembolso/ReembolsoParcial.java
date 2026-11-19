package co.edu.uniquindio.cinemauq.reembolso;

public class ReembolsoParcial implements PoliticaReembolso {
    @Override public double getPorcentaje() { return 80; }
    @Override public String getDescripcion() { return "Faltan entre 2 y 24 horas: se devuelve el 80 %"; }
}
