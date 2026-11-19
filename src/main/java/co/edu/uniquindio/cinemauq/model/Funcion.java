package co.edu.uniquindio.cinemauq.model;

import co.edu.uniquindio.cinemauq.exceptions.AsientoNoDisponibleException;
import co.edu.uniquindio.cinemauq.model.enums.EstadoFuncion;
import co.edu.uniquindio.cinemauq.notificacion.ObservadorFuncion;
import co.edu.uniquindio.cinemauq.util.Formato;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Función de cine: una película en una sala, en una fecha y hora.
 * - Prototype: clonar() permite programar la misma función en otra fecha.
 * - Observer (sujeto): avisa a sus observadores cuando es cancelada.
 */
public class Funcion implements Prototipo<Funcion> {

    private String codigo;
    private final Pelicula pelicula;
    private final Sala sala;
    private LocalDateTime fechaHora;
    private final double precioBase;
    private EstadoFuncion estado = EstadoFuncion.PROGRAMADA;
    private final Set<String> asientosOcupados = new LinkedHashSet<>();
    private final List<ObservadorFuncion> observadores = new ArrayList<>();

    public Funcion(String codigo, Pelicula pelicula, Sala sala, LocalDateTime fechaHora, double precioBase) {
        this.codigo = codigo;
        this.pelicula = pelicula;
        this.sala = sala;
        this.fechaHora = fechaHora;
        this.precioBase = precioBase;
    }

    public boolean isAsientoDisponible(Asiento asiento) {
        return !asientosOcupados.contains(asiento.getCodigo());
    }

    /** Un mismo asiento no puede venderse dos veces para la misma función. */
    public void ocuparAsiento(Asiento asiento) throws AsientoNoDisponibleException {
        if (!isAsientoDisponible(asiento)) {
            throw new AsientoNoDisponibleException("El asiento " + asiento + " ya está ocupado en esta función.");
        }
        asientosOcupados.add(asiento.getCodigo());
    }

    public void liberarAsiento(Asiento asiento) {
        asientosOcupados.remove(asiento.getCodigo());
    }

    public double getOcupacion() {
        return (double) asientosOcupados.size() / sala.getCapacidad();
    }

    public LocalDateTime getFechaHoraFin() {
        return fechaHora.plusMinutes(pelicula.getDuracionMinutos());
    }

    public boolean isProgramada() { return estado == EstadoFuncion.PROGRAMADA; }

    // ---------- Observer ----------
    public void agregarObservador(ObservadorFuncion observador) {
        if (!observadores.contains(observador)) {
            observadores.add(observador);
        }
    }

    public void quitarObservador(ObservadorFuncion observador) { observadores.remove(observador); }

    public void notificarObservadores(String mensaje) {
        for (ObservadorFuncion o : observadores) {
            o.actualizar(this, mensaje);
        }
    }

    public void cancelar() {
        estado = EstadoFuncion.CANCELADA;
        notificarObservadores("La función de " + pelicula.getTitulo() + " del " + Formato.fechaHora(fechaHora)
                + " fue cancelada por el cine. Se devolvió el 100 % del valor pagado.");
    }

    // ---------- Prototype ----------
    @Override
    public Funcion clonar() {
        // Copia película, sala y precio; NO copia asientos ocupados ni observadores.
        return new Funcion(codigo, pelicula, sala, fechaHora, precioBase);
    }

    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }
    public Pelicula getPelicula() { return pelicula; }
    public Sala getSala() { return sala; }
    public LocalDateTime getFechaHora() { return fechaHora; }
    public void setFechaHora(LocalDateTime fechaHora) { this.fechaHora = fechaHora; }
    public double getPrecioBase() { return precioBase; }
    public EstadoFuncion getEstado() { return estado; }
    public Set<String> getAsientosOcupados() { return Collections.unmodifiableSet(asientosOcupados); }
    public int getCantidadOcupados() { return asientosOcupados.size(); }

    @Override
    public String toString() {
        return pelicula.getTitulo() + " | " + Formato.fechaHora(fechaHora) + " | " + sala.getNombre();
    }
}
