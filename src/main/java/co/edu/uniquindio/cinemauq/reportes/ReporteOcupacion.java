package co.edu.uniquindio.cinemauq.reportes;

import co.edu.uniquindio.cinemauq.service.EstadisticasService;
import co.edu.uniquindio.cinemauq.util.Formato;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ReporteOcupacion extends ReporteBase {

    private double promedio;

    public ReporteOcupacion(EstadisticasService e) { super(e); }

    @Override protected String getTitulo() { return "Ocupación de las salas"; }

    @Override protected String getEncabezadoColumnas() { return String.format("%-50s %15s", "Sala", "Ocupación"); }

    @Override
    protected List<String> construirFilas(LocalDate desde, LocalDate hasta) {
        List<String> filas = new ArrayList<>();
        for (Map.Entry<String, Double> e : estadisticas.ocupacionPorSala(desde, hasta).entrySet()) {
            filas.add(String.format("%-50s %15s", e.getKey(), Formato.porcentaje(e.getValue())));
        }
        promedio = estadisticas.ocupacionPromedio(desde, hasta);
        return filas;
    }

    @Override protected String construirTotales() { return "OCUPACIÓN PROMEDIO POR FUNCIÓN: " + Formato.porcentaje(promedio); }
}
