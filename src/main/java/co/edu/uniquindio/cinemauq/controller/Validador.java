package co.edu.uniquindio.cinemauq.controller;

import co.edu.uniquindio.cinemauq.exceptions.DatoInvalidoException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;

/** Convierte y valida lo que el usuario escribe en los formularios. */
final class Validador {

    private Validador() { }

    static double decimal(String texto, String campo) throws DatoInvalidoException {
        try {
            String t = texto.trim().replace("$", "").replace(" ", "");
            if (t.matches("\\d{1,3}(\\.\\d{3})+(,\\d+)?")) {   // 12.000 o 1.500.000,5
                t = t.replace(".", "").replace(",", ".");
            } else {
                t = t.replace(",", ".");
            }
            return Double.parseDouble(t);
        } catch (NumberFormatException | NullPointerException e) {
            throw new DatoInvalidoException("El campo '" + campo + "' debe ser un número.");
        }
    }

    static int entero(String texto, String campo) throws DatoInvalidoException {
        try {
            return Integer.parseInt(texto.trim());
        } catch (NumberFormatException | NullPointerException e) {
            throw new DatoInvalidoException("El campo '" + campo + "' debe ser un número entero.");
        }
    }

    static LocalDateTime fechaHora(LocalDate fecha, String hora) throws DatoInvalidoException {
        if (fecha == null) {
            throw new DatoInvalidoException("Seleccione la fecha.");
        }
        try {
            return fecha.atTime(LocalTime.parse(hora.trim()));
        } catch (DateTimeParseException | NullPointerException e) {
            throw new DatoInvalidoException("La hora debe tener el formato HH:mm (por ejemplo 18:30).");
        }
    }
}
