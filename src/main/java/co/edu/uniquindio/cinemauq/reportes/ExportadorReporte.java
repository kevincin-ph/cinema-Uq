package co.edu.uniquindio.cinemauq.reportes;

import java.io.File;
import java.io.IOException;

/** OCP: para exportar a otro formato (por ejemplo PDF) se agrega otra implementación. */
public interface ExportadorReporte {
    File exportar(String contenido, File destino) throws IOException;
    String getExtension();
}
