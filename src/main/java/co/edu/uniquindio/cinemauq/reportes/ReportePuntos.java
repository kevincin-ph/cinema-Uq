package co.edu.uniquindio.cinemauq.reportes;

import co.edu.uniquindio.cinemauq.model.Cliente;
import co.edu.uniquindio.cinemauq.model.MovimientoPuntos;
import co.edu.uniquindio.cinemauq.model.enums.TipoMovimientoPuntos;
import co.edu.uniquindio.cinemauq.service.EstadisticasService;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ReportePuntos extends ReporteBase {

    private int acumulados;
    private int redimidos;

    public ReportePuntos(EstadisticasService e) { super(e); }

    @Override protected String getTitulo() { return "Puntos acumulados y redimidos"; }

    @Override
    protected String getEncabezadoColumnas() {
        return String.format("%-28s %12s %12s %12s", "Cliente", "Acumulados", "Redimidos", "Vigentes hoy");
    }

    @Override
    protected List<String> construirFilas(LocalDate desde, LocalDate hasta) {
        List<String> filas = new ArrayList<>();
        acumulados = 0;
        redimidos = 0;
        for (Cliente c : estadisticas.clientes()) {
            int acu = 0;
            int red = 0;
            for (MovimientoPuntos m : c.getCuentaPuntos().getMovimientos()) {
                if (EstadisticasService.enPeriodo(m.getFecha(), desde, hasta)) {
                    if (m.getTipo() == TipoMovimientoPuntos.ACUMULACION) {
                        acu += m.getPuntos();
                    } else if (m.getTipo() == TipoMovimientoPuntos.REDENCION) {
                        red += m.getPuntos();
                    }
                }
            }
            filas.add(String.format("%-28s %12d %12d %12d", ReporteVentas.corto(c.getNombre(), 28), acu, red,
                    c.getCuentaPuntos().getPuntosVigentes(LocalDate.now())));
            acumulados += acu;
            redimidos += red;
        }
        return filas;
    }

    @Override
    protected String construirTotales() {
        return "TOTAL PUNTOS ACUMULADOS: " + acumulados + "\nTOTAL PUNTOS REDIMIDOS: " + redimidos;
    }
}
