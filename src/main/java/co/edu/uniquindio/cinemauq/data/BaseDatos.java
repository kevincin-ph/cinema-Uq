package co.edu.uniquindio.cinemauq.data;

import co.edu.uniquindio.cinemauq.model.Compra;
import co.edu.uniquindio.cinemauq.model.Funcion;
import co.edu.uniquindio.cinemauq.model.Pelicula;
import co.edu.uniquindio.cinemauq.model.Promocion;
import co.edu.uniquindio.cinemauq.model.Sala;
import co.edu.uniquindio.cinemauq.model.SolicitudRecarga;
import co.edu.uniquindio.cinemauq.model.Usuario;
import co.edu.uniquindio.cinemauq.model.confiteria.ItemConfiteria;

import java.util.ArrayList;
import java.util.List;

/**
 * Patrón Singleton: una única fuente de datos en memoria para toda la aplicación.
 */
public final class BaseDatos {

    private static BaseDatos instancia;

    private final List<Usuario> usuarios = new ArrayList<>();
    private final List<Pelicula> peliculas = new ArrayList<>();
    private final List<Sala> salas = new ArrayList<>();
    private final List<Funcion> funciones = new ArrayList<>();
    private final List<ItemConfiteria> confiteria = new ArrayList<>();
    private final List<Promocion> promociones = new ArrayList<>();
    private final List<Compra> compras = new ArrayList<>();
    private final List<SolicitudRecarga> solicitudesRecarga = new ArrayList<>();
    private final List<String> bitacora = new ArrayList<>();

    private BaseDatos() { }

    public static synchronized BaseDatos getInstancia() {
        if (instancia == null) {
            instancia = new BaseDatos();
        }
        return instancia;
    }

    public List<Usuario> getUsuarios() { return usuarios; }
    public List<Pelicula> getPeliculas() { return peliculas; }
    public List<Sala> getSalas() { return salas; }
    public List<Funcion> getFunciones() { return funciones; }
    public List<ItemConfiteria> getConfiteria() { return confiteria; }
    public List<Promocion> getPromociones() { return promociones; }
    public List<Compra> getCompras() { return compras; }
    public List<SolicitudRecarga> getSolicitudesRecarga() { return solicitudesRecarga; }
    public List<String> getBitacora() { return bitacora; }
}
