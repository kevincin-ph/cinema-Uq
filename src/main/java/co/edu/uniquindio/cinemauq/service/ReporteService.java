package co.edu.uniquindio.cinemauq.service;

import co.edu.uniquindio.cinemauq.data.BaseDatos;
import co.edu.uniquindio.cinemauq.model.enums.TipoReporte;
import co.edu.uniquindio.cinemauq.reportes.ExportadorReporte;
import co.edu.uniquindio.cinemauq.reportes.ReporteFactory;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;

/** SRP: genera y exporta reportes. DIP: recibe el exportador como abstracción. */
public class ReporteService {

    private final BaseDatos db;
    private final EstadisticasService estadisticas;
    private final ExportadorReporte exportador;

    public ReporteService(BaseDatos db, EstadisticasService estadisticas, ExportadorReporte exportador) {
        this.db = db;
        this.estadisticas = estadisticas;
        this.exportador = exportador;
    }

    public String generar(TipoReporte tipo, LocalDate desde, LocalDate hasta) {
        return ReporteFactory.crear(tipo, estadisticas, db).generar(desde, hasta);
    }

    public File exportar(String contenido, File destino) throws IOException {
        return exportador.exportar(contenido, destino);
    }
}
