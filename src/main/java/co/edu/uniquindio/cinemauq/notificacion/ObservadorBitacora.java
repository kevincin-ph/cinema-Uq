package co.edu.uniquindio.cinemauq.notificacion;

import co.edu.uniquindio.cinemauq.model.Funcion;
import co.edu.uniquindio.cinemauq.util.Formato;

import java.time.LocalDateTime;
import java.util.List;

/** Observador que deja registro en la bitácora del administrador. */
public class ObservadorBitacora implements ObservadorFuncion {

    private final List<String> bitacora;

    public ObservadorBitacora(List<String> bitacora) {
        this.bitacora = bitacora;
    }

    @Override
    public void actualizar(Funcion funcion, String mensaje) {
        bitacora.add(0, Formato.fechaHora(LocalDateTime.now()) + " [" + funcion.getCodigo() + "] " + mensaje);
    }
}
