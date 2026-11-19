package co.edu.uniquindio.cinemauq.reembolso;

public class SinReembolso implements PoliticaReembolso {
    @Override public double getPorcentaje() { return 0; }
    @Override public String getDescripcion() { return "Faltan menos de 2 horas: no se permite cancelar"; }
}
