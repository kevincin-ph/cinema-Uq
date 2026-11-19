package co.edu.uniquindio.cinemauq.service;

import co.edu.uniquindio.cinemauq.data.BaseDatos;
import co.edu.uniquindio.cinemauq.exceptions.CinemaException;
import co.edu.uniquindio.cinemauq.exceptions.DatoInvalidoException;
import co.edu.uniquindio.cinemauq.model.Promocion;
import co.edu.uniquindio.cinemauq.model.confiteria.Combo;
import co.edu.uniquindio.cinemauq.model.confiteria.ItemConfiteria;
import co.edu.uniquindio.cinemauq.model.confiteria.ProductoConfiteria;
import co.edu.uniquindio.cinemauq.model.enums.AplicaPromocion;
import co.edu.uniquindio.cinemauq.model.enums.CategoriaProducto;
import co.edu.uniquindio.cinemauq.util.GeneradorCodigo;

import java.util.List;

/** SRP: gestiona productos, combos y promociones. */
public class ConfiteriaService {

    private final BaseDatos db;

    public ConfiteriaService(BaseDatos db) {
        this.db = db;
    }

    public ProductoConfiteria agregarProducto(String nombre, double precio, CategoriaProducto categoria)
            throws DatoInvalidoException {
        if (nombre == null || nombre.isBlank() || categoria == null || categoria == CategoriaProducto.COMBO) {
            throw new DatoInvalidoException("Nombre y categoría (bebida, comida o dulce) son obligatorios.");
        }
        if (precio <= 0) {
            throw new DatoInvalidoException("El precio debe ser mayor que cero.");
        }
        ProductoConfiteria p = new ProductoConfiteria(GeneradorCodigo.siguiente("PRO"), nombre.trim(), precio, categoria);
        db.getConfiteria().add(p);
        return p;
    }

    public Combo crearCombo(String nombre, List<ItemConfiteria> items, double descuento) throws DatoInvalidoException {
        if (nombre == null || nombre.isBlank()) {
            throw new DatoInvalidoException("El combo necesita un nombre.");
        }
        if (items == null || items.size() < 2) {
            throw new DatoInvalidoException("Un combo debe tener al menos 2 productos.");
        }
        if (descuento < 0 || descuento > 50) {
            throw new DatoInvalidoException("El descuento del combo debe estar entre 0 y 50 %.");
        }
        Combo combo = new Combo(GeneradorCodigo.siguiente("CMB"), nombre.trim(), descuento);
        items.forEach(combo::agregar);
        db.getConfiteria().add(combo);
        return combo;
    }

    public void eliminarItem(ItemConfiteria item) throws CinemaException {
        boolean usadoEnCombo = db.getConfiteria().stream()
                .anyMatch(i -> i instanceof Combo c && c.getItems().contains(item));
        if (usadoEnCombo) {
            throw new CinemaException("No se puede eliminar: el producto hace parte de un combo.");
        }
        db.getConfiteria().remove(item);
    }

    public List<ItemConfiteria> listarItems() { return db.getConfiteria(); }

    public Promocion agregarPromocion(String nombre, double porcentaje, AplicaPromocion aplicaA) throws DatoInvalidoException {
        if (nombre == null || nombre.isBlank() || aplicaA == null) {
            throw new DatoInvalidoException("Nombre y a qué aplica son obligatorios.");
        }
        if (porcentaje <= 0 || porcentaje > 70) {
            throw new DatoInvalidoException("El porcentaje debe estar entre 1 y 70.");
        }
        Promocion p = new Promocion(GeneradorCodigo.siguiente("PRM"), nombre.trim(), porcentaje, aplicaA);
        db.getPromociones().add(p);
        return p;
    }

    public void cambiarEstadoPromocion(Promocion p) { p.setActiva(!p.isActiva()); }

    public List<Promocion> listarPromociones() { return db.getPromociones(); }

    public List<Promocion> listarPromocionesActivas() {
        return db.getPromociones().stream().filter(Promocion::isActiva).toList();
    }
}
