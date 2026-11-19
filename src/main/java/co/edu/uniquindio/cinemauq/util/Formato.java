package co.edu.uniquindio.cinemauq.util;

import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/** Utilidades para mostrar dinero y fechas siempre con el mismo formato. */
public final class Formato {

    private static final NumberFormat NUMERO = NumberFormat.getNumberInstance(Locale.forLanguageTag("es-CO"));
    public static final DateTimeFormatter FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    public static final DateTimeFormatter FECHA_HORA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    static {
        NUMERO.setMaximumFractionDigits(0);
    }

    private Formato() { }

    public static String moneda(double valor) { return "$ " + NUMERO.format(Math.round(valor)); }

    public static String numero(double valor) { return NUMERO.format(valor); }

    public static String fecha(LocalDate fecha) { return fecha == null ? "" : fecha.format(FECHA); }

    public static String fechaHora(LocalDateTime fecha) { return fecha == null ? "" : fecha.format(FECHA_HORA); }

    public static String porcentaje(double fraccion) { return String.format("%.1f %%", fraccion * 100); }
}
