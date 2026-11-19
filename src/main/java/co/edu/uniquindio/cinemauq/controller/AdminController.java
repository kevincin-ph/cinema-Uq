package co.edu.uniquindio.cinemauq.controller;

import co.edu.uniquindio.cinemauq.exceptions.CinemaException;
import co.edu.uniquindio.cinemauq.exceptions.DatoInvalidoException;
import co.edu.uniquindio.cinemauq.facade.OperacionesAdministrador;
import co.edu.uniquindio.cinemauq.model.Administrador;
import co.edu.uniquindio.cinemauq.model.Cliente;
import co.edu.uniquindio.cinemauq.model.Compra;
import co.edu.uniquindio.cinemauq.model.Funcion;
import co.edu.uniquindio.cinemauq.model.Pelicula;
import co.edu.uniquindio.cinemauq.model.Promocion;
import co.edu.uniquindio.cinemauq.model.Reembolso;
import co.edu.uniquindio.cinemauq.model.Sala;
import co.edu.uniquindio.cinemauq.model.SolicitudRecarga;
import co.edu.uniquindio.cinemauq.model.confiteria.ItemConfiteria;
import co.edu.uniquindio.cinemauq.model.enums.AplicaPromocion;
import co.edu.uniquindio.cinemauq.model.enums.CategoriaProducto;
import co.edu.uniquindio.cinemauq.model.enums.Genero;
import co.edu.uniquindio.cinemauq.model.enums.TipoReporte;
import co.edu.uniquindio.cinemauq.service.EstadisticasService;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/** Controlador de las pantallas del administrador. Depende solo de OperacionesAdministrador (ISP). */
public class AdminController {

    private final OperacionesAdministrador ops;
    private final Administrador admin;

    public AdminController(OperacionesAdministrador ops, Administrador admin) {
        this.ops = ops;
        this.admin = admin;
    }

    public Administrador getAdmin() { return admin; }

    // Películas
    public List<Pelicula> getPeliculas() { return ops.getPeliculas(); }

    public void agregarPelicula(String titulo, Genero genero, String duracion, String clasificacion, String sinopsis)
            throws CinemaException {
        ops.agregarPelicula(titulo, genero, Validador.entero(duracion, "Duración"), clasificacion, sinopsis);
    }

    public void cambiarCartelera(Pelicula p) throws DatoInvalidoException { ops.cambiarEstadoCartelera(requerido(p)); }
    public void eliminarPelicula(Pelicula p) throws CinemaException { ops.eliminarPelicula(requerido(p)); }

    // Salas
    public List<Sala> getSalas() { return ops.getSalas(); }

    public void agregarSala(String nombre, String filas, String columnas, String pref) throws CinemaException {
        ops.agregarSala(nombre, Validador.entero(filas, "Filas"), Validador.entero(columnas, "Columnas"),
                Validador.entero(pref, "Filas preferenciales"));
    }

    public void eliminarSala(Sala s) throws CinemaException { ops.eliminarSala(requerido(s)); }

    // Funciones
    public List<Funcion> getFunciones() { return ops.getFunciones(); }

    public void programarFuncion(Pelicula p, Sala s, LocalDate fecha, String hora, String precio) throws CinemaException {
        ops.programarFuncion(p, s, Validador.fechaHora(fecha, hora), Validador.decimal(precio, "Precio"));
    }

    public void duplicarFuncion(Funcion f, LocalDate fecha, String hora) throws CinemaException {
        ops.duplicarFuncion(requerido(f), Validador.fechaHora(fecha, hora));
    }

    public List<Reembolso> cancelarFuncion(Funcion f) throws CinemaException { return ops.cancelarFuncion(requerido(f)); }

    // Confitería y promociones
    public List<ItemConfiteria> getConfiteria() { return ops.getConfiteria(); }

    public void agregarProducto(String nombre, String precio, CategoriaProducto cat) throws CinemaException {
        ops.agregarProducto(nombre, Validador.decimal(precio, "Precio"), cat);
    }

    public void crearCombo(String nombre, List<ItemConfiteria> items, String descuento) throws CinemaException {
        ops.crearCombo(nombre, new ArrayList<>(items), Validador.decimal(descuento, "Descuento"));
    }

    public void eliminarItem(ItemConfiteria item) throws CinemaException { ops.eliminarItem(requerido(item)); }

    public List<Promocion> getPromociones() { return ops.getPromociones(); }

    public void agregarPromocion(String nombre, String porcentaje, AplicaPromocion aplica) throws CinemaException {
        ops.agregarPromocion(nombre, Validador.decimal(porcentaje, "Porcentaje"), aplica);
    }

    public void cambiarEstadoPromocion(Promocion p) throws DatoInvalidoException { ops.cambiarEstadoPromocion(requerido(p)); }

    // Clientes y tarjetas
    public List<Cliente> getClientes() { return ops.getClientes(); }

    public void recargar(Cliente c, String monto) throws CinemaException {
        ops.recargarTarjeta(requerido(c), Validador.decimal(monto, "Monto"));
    }

    public void cambiarEstadoTarjeta(Cliente c) throws DatoInvalidoException { ops.cambiarEstadoTarjeta(requerido(c)); }

    public List<SolicitudRecarga> getSolicitudes() {
        List<SolicitudRecarga> l = new ArrayList<>(ops.getSolicitudesRecarga());
        l.sort(Comparator.comparing(SolicitudRecarga::getFecha).reversed());
        return l;
    }

    public void aprobar(SolicitudRecarga s) throws CinemaException { ops.aprobarSolicitud(requerido(s)); }
    public void rechazar(SolicitudRecarga s) throws CinemaException { ops.rechazarSolicitud(requerido(s)); }

    // Compras, estadísticas y reportes
    public List<Compra> getCompras() {
        List<Compra> l = new ArrayList<>(ops.getCompras());
        l.sort(Comparator.comparing(Compra::getFecha).reversed());
        return l;
    }

    public EstadisticasService getEstadisticas() { return ops.getEstadisticas(); }

    public String generarReporte(TipoReporte tipo, LocalDate desde, LocalDate hasta) throws DatoInvalidoException {
        if (tipo == null) {
            throw new DatoInvalidoException("Seleccione el tipo de reporte.");
        }
        if (desde != null && hasta != null && desde.isAfter(hasta)) {
            throw new DatoInvalidoException("La fecha inicial no puede ser mayor que la final.");
        }
        return ops.generarReporte(tipo, desde, hasta);
    }

    public File exportarReporte(String contenido, File destino) throws IOException, DatoInvalidoException {
        if (contenido == null || contenido.isBlank()) {
            throw new DatoInvalidoException("Primero genere el reporte.");
        }
        return ops.exportarReporte(contenido, destino);
    }

    public List<String> getBitacora() { return ops.getBitacora(); }

    private <T> T requerido(T objeto) throws DatoInvalidoException {
        if (objeto == null) {
            throw new DatoInvalidoException("Primero seleccione un elemento de la tabla.");
        }
        return objeto;
    }
}
