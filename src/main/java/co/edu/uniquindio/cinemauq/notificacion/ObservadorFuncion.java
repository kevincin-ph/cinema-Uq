package co.edu.uniquindio.cinemauq.notificacion;

import co.edu.uniquindio.cinemauq.model.Funcion;

/** Patrón Observer: quien quiera enterarse de cambios en una función implementa esta interfaz. */
public interface ObservadorFuncion {
    void actualizar(Funcion funcion, String mensaje);
}
