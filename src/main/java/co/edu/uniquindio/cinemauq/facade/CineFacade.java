package co.edu.uniquindio.cinemauq.facade;

import co.edu.uniquindio.cinemauq.builder.CompraBuilder;
import co.edu.uniquindio.cinemauq.data.BaseDatos;
import co.edu.uniquindio.cinemauq.exceptions.CinemaException;
import co.edu.uniquindio.cinemauq.model.Cliente;
import co.edu.uniquindio.cinemauq.model.Compra;
import co.edu.uniquindio.cinemauq.model.Funcion;
import co.edu.uniquindio.cinemauq.model.Pelicula;
import co.edu.uniquindio.cinemauq.model.Promocion;
import co.edu.uniquindio.cinemauq.model.Reembolso;
import co.edu.uniquindio.cinemauq.model.Sala;
import co.edu.uniquindio.cinemauq.model.SolicitudRecarga;
import co.edu.uniquindio.cinemauq.model.Usuario;
import co.edu.uniquindio.cinemauq.model.confiteria.ItemConfiteria;
import co.edu.uniquindio.cinemauq.model.enums.AplicaPromocion;
import co.edu.uniquindio.cinemauq.model.enums.CategoriaProducto;
import co.edu.uniquindio.cinemauq.model.enums.Genero;
import co.edu.uniquindio.cinemauq.model.enums.MetodoPago;
import co.edu.uniquindio.cinemauq.model.enums.TipoReporte;
import co.edu.uniquindio.cinemauq.reembolso.PoliticaReembolso;
import co.edu.uniquindio.cinemauq.service.AuthService;
import co.edu.uniquindio.cinemauq.service.CarteleraService;
import co.edu.uniquindio.cinemauq.service.CompraService;
import co.edu.uniquindio.cinemauq.service.ConfiteriaService;
import co.edu.uniquindio.cinemauq.service.EstadisticasService;
import co.edu.uniquindio.cinemauq.service.ReporteService;
import co.edu.uniquindio.cinemauq.service.TarjetaService;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Patrón Facade: los controladores de la interfaz solo hablan con esta clase,
 * que oculta los servicios internos (cartelera, confitería, compras, tarjetas, reportes).
 */
public class CineFacade implements OperacionesAcceso, OperacionesCliente, OperacionesAdministrador {

    private final BaseDatos db;
    private final AuthService auth;
    private final CarteleraService cartelera;
    private final ConfiteriaService confiteria;
    private final CompraService compras;
    private final TarjetaService tarjetas;
    private final EstadisticasService estadisticas;
    private final ReporteService reportes;

    public CineFacade(BaseDatos db, AuthService auth, CarteleraService cartelera, ConfiteriaService confiteria,
                      CompraService compras, TarjetaService tarjetas, EstadisticasService estadisticas, ReporteService reportes) {
        this.db = db;
        this.auth = auth;
        this.cartelera = cartelera;
        this.confiteria = confiteria;
        this.compras = compras;
        this.tarjetas = tarjetas;
        this.estadisticas = estadisticas;
        this.reportes = reportes;
    }

    private LocalDateTime ahora() { return LocalDateTime.now(); }

    // ================= Acceso =================
    @Override
    public Usuario iniciarSesion(String email, String password) throws CinemaException {
        return auth.iniciarSesion(email, password);
    }

    @Override
    public Cliente registrarCliente(String nombre, String email, String password, String documento, String telefono)
            throws CinemaException {
        return auth.registrarCliente(nombre, email, password, documento, telefono);
    }

    // ================= Cliente =================
    @Override public List<Pelicula> getCartelera() { return cartelera.listarCartelera(); }
    @Override public List<Funcion> getFuncionesDisponibles(Pelicula p) { return cartelera.funcionesDisponibles(p, ahora()); }
    @Override public List<ItemConfiteria> getConfiteria() { return confiteria.listarItems(); }
    @Override public List<Promocion> getPromocionesActivas() { return confiteria.listarPromocionesActivas(); }
    @Override public CompraBuilder nuevaCompra(Cliente cliente, Funcion funcion) { return new CompraBuilder(cliente, funcion); }

    @Override
    public Compra confirmarCompra(Compra compra, MetodoPago metodo) throws CinemaException {
        return compras.confirmarCompra(compra, metodo, ahora());
    }

    @Override public int puntosNecesarios(double valor) { return compras.getReglaPuntos().puntosNecesarios(valor); }
    @Override public PoliticaReembolso consultarPoliticaCancelacion(Compra c) { return compras.consultarPolitica(c, ahora()); }
    @Override public Reembolso cancelarCompra(Compra c) throws CinemaException { return compras.cancelarCompra(c, ahora()); }

    @Override
    public void solicitarRecarga(Cliente cliente, double monto) throws CinemaException {
        tarjetas.solicitarRecarga(cliente, monto, ahora());
    }

    @Override public int getPuntosVigentes(Cliente c) { return c.getCuentaPuntos().getPuntosVigentes(LocalDate.now()); }
    @Override public String getReglaPuntos() { return compras.getReglaPuntos().getDescripcion(); }

    // ================= Administrador =================
    @Override public List<Pelicula> getPeliculas() { return cartelera.listarPeliculas(); }

    @Override
    public void agregarPelicula(String titulo, Genero genero, int duracion, String clasificacion, String sinopsis)
            throws CinemaException {
        cartelera.agregarPelicula(titulo, genero, duracion, clasificacion, sinopsis);
    }

    @Override public void cambiarEstadoCartelera(Pelicula p) { cartelera.cambiarEstadoCartelera(p); }
    @Override public void eliminarPelicula(Pelicula p) throws CinemaException { cartelera.eliminarPelicula(p); }

    @Override public List<Sala> getSalas() { return cartelera.listarSalas(); }

    @Override
    public void agregarSala(String nombre, int filas, int columnas, int pref) throws CinemaException {
        cartelera.agregarSala(nombre, filas, columnas, pref);
    }

    @Override public void eliminarSala(Sala s) throws CinemaException { cartelera.eliminarSala(s); }

    @Override public List<Funcion> getFunciones() { return cartelera.listarFunciones(); }

    @Override
    public void programarFuncion(Pelicula p, Sala s, LocalDateTime fechaHora, double precio) throws CinemaException {
        cartelera.programarFuncion(p, s, fechaHora, precio, ahora());
    }

    @Override
    public void duplicarFuncion(Funcion f, LocalDateTime nuevaFecha) throws CinemaException {
        cartelera.duplicarFuncion(f, nuevaFecha, ahora());
    }

    @Override public List<Reembolso> cancelarFuncion(Funcion f) throws CinemaException { return compras.cancelarFuncion(f, ahora()); }

    @Override
    public void agregarProducto(String nombre, double precio, CategoriaProducto categoria) throws CinemaException {
        confiteria.agregarProducto(nombre, precio, categoria);
    }

    @Override
    public void crearCombo(String nombre, List<ItemConfiteria> items, double descuento) throws CinemaException {
        confiteria.crearCombo(nombre, items, descuento);
    }

    @Override public void eliminarItem(ItemConfiteria item) throws CinemaException { confiteria.eliminarItem(item); }

    @Override public List<Promocion> getPromociones() { return confiteria.listarPromociones(); }

    @Override
    public void agregarPromocion(String nombre, double porcentaje, AplicaPromocion aplicaA) throws CinemaException {
        confiteria.agregarPromocion(nombre, porcentaje, aplicaA);
    }

    @Override public void cambiarEstadoPromocion(Promocion p) { confiteria.cambiarEstadoPromocion(p); }

    @Override public List<Cliente> getClientes() { return estadisticas.clientes(); }

    @Override
    public void recargarTarjeta(Cliente cliente, double monto) throws CinemaException {
        tarjetas.recargar(cliente, monto, ahora());
    }

    @Override public void cambiarEstadoTarjeta(Cliente c) { tarjetas.cambiarEstadoTarjeta(c); }
    @Override public List<SolicitudRecarga> getSolicitudesRecarga() { return tarjetas.listarSolicitudes(); }
    @Override public void aprobarSolicitud(SolicitudRecarga s) throws CinemaException { tarjetas.aprobarSolicitud(s, ahora()); }
    @Override public void rechazarSolicitud(SolicitudRecarga s) throws CinemaException { tarjetas.rechazarSolicitud(s); }

    @Override public List<Compra> getCompras() { return compras.listarCompras(); }
    @Override public EstadisticasService getEstadisticas() { return estadisticas; }

    @Override
    public String generarReporte(TipoReporte tipo, LocalDate desde, LocalDate hasta) {
        return reportes.generar(tipo, desde, hasta);
    }

    @Override
    public File exportarReporte(String contenido, File destino) throws IOException {
        return reportes.exportar(contenido, destino);
    }

    @Override public List<String> getBitacora() { return db.getBitacora(); }
}
