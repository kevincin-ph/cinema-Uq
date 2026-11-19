package co.edu.uniquindio.cinemauq.reportes;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

public class ExportadorTxt implements ExportadorReporte {

    @Override
    public File exportar(String contenido, File destino) throws IOException {
        File archivo = destino.getName().toLowerCase().endsWith(".txt") ? destino : new File(destino.getPath() + ".txt");
        Files.writeString(archivo.toPath(), contenido, StandardCharsets.UTF_8);
        return archivo;
    }

    @Override
    public String getExtension() { return "txt"; }
}
