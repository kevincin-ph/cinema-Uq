package co.edu.uniquindio.cinemauq.service;

import co.edu.uniquindio.cinemauq.data.BaseDatos;
import co.edu.uniquindio.cinemauq.exceptions.CinemaException;
import co.edu.uniquindio.cinemauq.exceptions.CruceHorarioException;
import co.edu.uniquindio.cinemauq.exceptions.DatoInvalidoException;
import co.edu.uniquindio.cinemauq.model.Funcion;
import co.edu.uniquindio.cinemauq.model.Pelicula;
import co.edu.uniquindio.cinemauq.model.Sala;
import co.edu.uniquindio.cinemauq.model.enums.Genero;
import co.edu.uniquindio.cinemauq.notificacion.ObservadorBitacora;
import co.edu.uniquindio.cinemauq.util.GeneradorCodigo;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

/** SRP: gestiona películas, salas y funciones. */
public class CarteleraService {

    /** Minutos de limpieza entre dos funciones de la misma sala. */
    public static final int MINUTOS_LIMPIEZA = 15;

    private final BaseDatos db;

    public CarteleraService(BaseDatos db) {
        this.db = db;
    }

    // ---------- Películas ----------
    public Pelicula agregarPelicula(String titulo, Genero genero, int duracion, String clasificacion, String sinopsis)
            throws DatoInvalidoException {
        if (titulo == null || titulo.isBlank() || genero == null) {
            throw new DatoInvalidoException("El título y el género son obligatorios.");
        }
        if (duracion < 30 || duracion > 300) {
            throw new DatoInvalidoException("La duración debe estar entre 30 y 300 minutos.");
        }
        Pelicula p = new Pelicula(GeneradorCodigo.siguiente("PEL"), titulo.trim(), genero, duracion, clasificacion, sinopsis);
        db.getPeliculas().add(p);
        return p;
    }

    public void cambiarEstadoCartelera(Pelicula p) { p.setEnCartelera(!p.isEnCartelera()); }

    public void eliminarPelicula(Pelicula p) throws CinemaException {
        boolean tieneFunciones = db.getFunciones().stream().anyMatch(f -> f.getPelicula() == p);
        if (tieneFunciones) {
            throw new CinemaException("No se puede eliminar: la película tiene funciones. Puede quitarla de cartelera.");
        }
        db.getPeliculas().remove(p);
    }

    public List<Pelicula> listarPeliculas() { return db.getPeliculas(); }

    public List<Pelicula> listarCartelera() {
        return db.getPeliculas().stream().filter(Pelicula::isEnCartelera).toList();
    }

    // ---------- Salas ----------
    public Sala agregarSala(String nombre, int filas, int columnas, int filasPreferenciales) throws DatoInvalidoException {
        if (nombre == null || nombre.isBlank()) {
            throw new DatoInvalidoException("El nombre de la sala es obligatorio.");
        }
        if (filas < 1 || filas > 20 || columnas < 1 || columnas > 25) {
            throw new DatoInvalidoException("Filas entre 1 y 20, columnas entre 1 y 25.");
        }
        if (filasPreferenciales < 0 || filasPreferenciales > filas) {
            throw new DatoInvalidoException("Las filas preferenciales no pueden superar el total de filas.");
        }
        Sala s = new Sala(GeneradorCodigo.siguiente("SAL"), nombre.trim(), filas, columnas, filasPreferenciales);
        db.getSalas().add(s);
        return s;
    }

    public void eliminarSala(Sala s) throws CinemaException {
        if (db.getFunciones().stream().anyMatch(f -> f.getSala() == s)) {
            throw new CinemaException("No se puede eliminar: la sala tiene funciones asociadas.");
        }
        db.getSalas().remove(s);
    }

    public List<Sala> listarSalas() { return db.getSalas(); }

    // ---------- Funciones ----------
    public Funcion programarFuncion(Pelicula pelicula, Sala sala, LocalDateTime fechaHora, double precio, LocalDateTime ahora)
            throws CinemaException {
        if (pelicula == null || sala == null || fechaHora == null) {
            throw new DatoInvalidoException("Película, sala y fecha son obligatorias.");
        }
        if (precio <= 0) {
            throw new DatoInvalidoException("El precio debe ser mayor que cero.");
        }
        Funcion f = new Funcion(GeneradorCodigo.siguiente("FUN"), pelicula, sala, fechaHora, precio);
        registrar(f, ahora);
        return f;
    }

    /** Usa el patrón Prototype para copiar una función existente a otra fecha. */
    public Funcion duplicarFuncion(Funcion original, LocalDateTime nuevaFecha, LocalDateTime ahora) throws CinemaException {
        Funcion copia = original.clonar();
        copia.setCodigo(GeneradorCodigo.siguiente("FUN"));
        copia.setFechaHora(nuevaFecha);
        registrar(copia, ahora);
        return copia;
    }

    private void registrar(Funcion f, LocalDateTime ahora) throws CinemaException {
        if (!f.getFechaHora().isAfter(ahora)) {
            throw new DatoInvalidoException("La función debe programarse en una fecha futura.");
        }
        validarCruce(f);
        f.agregarObservador(new ObservadorBitacora(db.getBitacora()));
        db.getFunciones().add(f);
    }

    /** Regla adicional: dos funciones no pueden cruzarse en la misma sala (se deja tiempo de limpieza). */
    private void validarCruce(Funcion nueva) throws CruceHorarioException {
        LocalDateTime ini = nueva.getFechaHora();
        LocalDateTime fin = nueva.getFechaHoraFin().plusMinutes(MINUTOS_LIMPIEZA);
        for (Funcion f : db.getFunciones()) {
            if (f.getSala() == nueva.getSala() && f.isProgramada()) {
                LocalDateTime fIni = f.getFechaHora();
                LocalDateTime fFin = f.getFechaHoraFin().plusMinutes(MINUTOS_LIMPIEZA);
                if (ini.isBefore(fFin) && fIni.isBefore(fin)) {
                    throw new CruceHorarioException("La sala " + f.getSala() + " ya tiene la función " + f + " en ese horario.");
                }
            }
        }
    }

    public List<Funcion> listarFunciones() {
        return db.getFunciones().stream().sorted(Comparator.comparing(Funcion::getFechaHora)).toList();
    }

    /** Funciones de una película que todavía se pueden comprar. */
    public List<Funcion> funcionesDisponibles(Pelicula pelicula, LocalDateTime ahora) {
        return db.getFunciones().stream()
                .filter(f -> f.getPelicula() == pelicula && f.isProgramada() && f.getFechaHora().isAfter(ahora))
                .sorted(Comparator.comparing(Funcion::getFechaHora))
                .toList();
    }
}
