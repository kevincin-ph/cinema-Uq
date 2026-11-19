package co.edu.uniquindio.cinemauq.reportes;

import co.edu.uniquindio.cinemauq.service.EstadisticasService;
import co.edu.uniquindio.cinemauq.util.Formato;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Patrón Template Method: generar() define los pasos fijos de cualquier reporte
 * (encabezado, columnas, filas, registros encontrados, totales) y cada subclase llena su parte.
 */
public abstract class ReporteBase {

    protected static final String LINEA = "=".repeat(78);
    protected static final String SEPARADOR = "-".repeat(78);

    protected final EstadisticasService estadisticas;

    protected ReporteBase(EstadisticasService estadisticas) {
        this.estadisticas = estadisticas;
    }

    /** Método plantilla (final para que las subclases no cambien el orden). */
    public final String generar(LocalDate desde, LocalDate hasta) {
        StringBuilder sb = new StringBuilder();
        sb.append(LINEA).append('\n');
        sb.append("CINEMAUQ - ").append(getTitulo().toUpperCase()).append('\n');
        sb.append(LINEA).append('\n');
        sb.append("Fecha de generación: ").append(Formato.fechaHora(LocalDateTime.now())).append('\n');
        sb.append("Periodo consultado: ").append(desde == null ? "inicio" : Formato.fecha(desde))
          .append(" a ").append(hasta == null ? "hoy" : Formato.fecha(hasta)).append('\n');
        sb.append(SEPARADOR).append('\n');
        sb.append(getEncabezadoColumnas()).append('\n');
        sb.append(SEPARADOR).append('\n');
        List<String> filas = construirFilas(desde, hasta);
        if (filas.isEmpty()) {
            sb.append("(No se encontraron registros en el periodo)\n");
        }
        filas.forEach(f -> sb.append(f).append('\n'));
        sb.append(SEPARADOR).append('\n');
        sb.append("Registros encontrados: ").append(filas.size()).append('\n');
        sb.append(construirTotales()).append('\n');
        sb.append(LINEA).append('\n');
        return sb.toString();
    }

    protected abstract String getTitulo();

    protected abstract String getEncabezadoColumnas();

    /** Construye las filas y guarda internamente los datos para los totales. */
    protected abstract List<String> construirFilas(LocalDate desde, LocalDate hasta);

    protected abstract String construirTotales();
}
