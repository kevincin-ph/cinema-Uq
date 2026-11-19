package co.edu.uniquindio.cinemauq.reportes;

import co.edu.uniquindio.cinemauq.service.EstadisticasService;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ReporteEntradas extends ReporteBase {

    private int total;

    public ReporteEntradas(EstadisticasService e) { super(e); }

    @Override protected String getTitulo() { return "Entradas vendidas por película"; }

    @Override protected String getEncabezadoColumnas() { return String.format("%-50s %15s", "Película", "Entradas"); }

    @Override
    protected List<String> construirFilas(LocalDate desde, LocalDate hasta) {
        List<String> filas = new ArrayList<>();
        total = 0;
        for (Map.Entry<String, Integer> e : estadisticas.entradasPorPelicula(desde, hasta).entrySet()) {
            filas.add(String.format("%-50s %15d", e.getKey(), e.getValue()));
            total += e.getValue();
        }
        return filas;
    }

    @Override protected String construirTotales() { return "TOTAL ENTRADAS VENDIDAS: " + total; }
}
