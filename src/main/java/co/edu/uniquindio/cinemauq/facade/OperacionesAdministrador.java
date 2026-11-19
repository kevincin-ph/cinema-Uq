package co.edu.uniquindio.cinemauq.facade;

import co.edu.uniquindio.cinemauq.exceptions.CinemaException;
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
import java.time.LocalDateTime;
import java.util.List;

/** ISP: operaciones exclusivas del administrador. */
public interface OperacionesAdministrador {
    List<Pelicula> getPeliculas();
    void agregarPelicula(String titulo, Genero genero, int duracion, String clasificacion, String sinopsis) throws CinemaException;
    void cambiarEstadoCartelera(Pelicula pelicula);
    void eliminarPelicula(Pelicula pelicula) throws CinemaException;

    List<Sala> getSalas();
    void agregarSala(String nombre, int filas, int columnas, int filasPreferenciales) throws CinemaException;
    void eliminarSala(Sala sala) throws CinemaException;

    List<Funcion> getFunciones();
    void programarFuncion(Pelicula p, Sala s, LocalDateTime fechaHora, double precio) throws CinemaException;
    void duplicarFuncion(Funcion f, LocalDateTime nuevaFecha) throws CinemaException;
    List<Reembolso> cancelarFuncion(Funcion f) throws CinemaException;

    List<ItemConfiteria> getConfiteria();
    void agregarProducto(String nombre, double precio, CategoriaProducto categoria) throws CinemaException;
    void crearCombo(String nombre, List<ItemConfiteria> items, double descuento) throws CinemaException;
    void eliminarItem(ItemConfiteria item) throws CinemaException;

    List<Promocion> getPromociones();
    void agregarPromocion(String nombre, double porcentaje, AplicaPromocion aplicaA) throws CinemaException;
    void cambiarEstadoPromocion(Promocion p);

    List<Cliente> getClientes();
    void recargarTarjeta(Cliente cliente, double monto) throws CinemaException;
    void cambiarEstadoTarjeta(Cliente cliente);
    List<SolicitudRecarga> getSolicitudesRecarga();
    void aprobarSolicitud(SolicitudRecarga s) throws CinemaException;
    void rechazarSolicitud(SolicitudRecarga s) throws CinemaException;

    List<Compra> getCompras();
    EstadisticasService getEstadisticas();
    String generarReporte(TipoReporte tipo, LocalDate desde, LocalDate hasta);
    File exportarReporte(String contenido, File destino) throws IOException;
    List<String> getBitacora();
}
