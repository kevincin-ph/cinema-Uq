package co.edu.uniquindio.cinemauq.exceptions;

/** Excepción base del dominio CinemaUQ. Todas las excepciones propias heredan de esta. */
public class CinemaException extends Exception {
    public CinemaException(String mensaje) { super(mensaje); }
}
