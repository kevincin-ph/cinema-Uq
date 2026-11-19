package co.edu.uniquindio.cinemauq.reportes;

import co.edu.uniquindio.cinemauq.data.BaseDatos;
import co.edu.uniquindio.cinemauq.model.enums.TipoReporte;
import co.edu.uniquindio.cinemauq.service.EstadisticasService;

/** Crea el reporte según el tipo que escoja el administrador. */
public final class ReporteFactory {

    private ReporteFactory() { }

    public static ReporteBase crear(TipoReporte tipo, EstadisticasService est, BaseDatos db) {
        return switch (tipo) {
            case VENTAS -> new ReporteVentas(est);
            case ENTRADAS_PELICULA -> new ReporteEntradas(est);
            case OCUPACION_SALAS -> new ReporteOcupacion(est);
            case CONFITERIA -> new ReporteConfiteria(est);
            case RECARGAS -> new ReporteRecargas(est);
            case REEMBOLSOS -> new ReporteReembolsos(est, db);
            case PUNTOS -> new ReportePuntos(est);
        };
    }
}
