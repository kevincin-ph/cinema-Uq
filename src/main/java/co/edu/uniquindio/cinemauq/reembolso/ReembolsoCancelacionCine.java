package co.edu.uniquindio.cinemauq.reembolso;

public class ReembolsoCancelacionCine implements PoliticaReembolso {
    @Override public double getPorcentaje() { return 100; }
    @Override public String getDescripcion() { return "Función cancelada por el cine: se devuelve el 100 %"; }
}
