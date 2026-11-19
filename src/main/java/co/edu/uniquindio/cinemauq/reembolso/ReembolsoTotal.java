package co.edu.uniquindio.cinemauq.reembolso;

public class ReembolsoTotal implements PoliticaReembolso {
    @Override public double getPorcentaje() { return 100; }
    @Override public String getDescripcion() { return "Faltan más de 24 horas: se devuelve el 100 %"; }
}
