package co.edu.uniquindio.cinemauq.model.confiteria;

import co.edu.uniquindio.cinemauq.model.Cobrable;
import co.edu.uniquindio.cinemauq.model.enums.CategoriaProducto;

/** Patrón Composite (componente): un producto o un combo se tratan igual. */
public interface ItemConfiteria extends Cobrable {
    String getCodigo();
    String getNombre();
    CategoriaProducto getCategoria();
}
