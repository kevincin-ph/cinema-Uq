package co.edu.uniquindio.cinemauq.util;

import java.util.HashMap;
import java.util.Map;

/** Genera códigos consecutivos por prefijo: PEL-001, FUN-002, etc. */
public final class GeneradorCodigo {

    private static final Map<String, Integer> CONTADORES = new HashMap<>();

    private GeneradorCodigo() { }

    public static synchronized String siguiente(String prefijo) {
        int n = CONTADORES.merge(prefijo, 1, Integer::sum);
        return String.format("%s-%03d", prefijo, n);
    }
}
