package co.edu.uniquindio.cinemauq.service;

import co.edu.uniquindio.cinemauq.data.BaseDatos;
import co.edu.uniquindio.cinemauq.exceptions.CinemaException;
import co.edu.uniquindio.cinemauq.exceptions.DatoInvalidoException;
import co.edu.uniquindio.cinemauq.model.Cliente;
import co.edu.uniquindio.cinemauq.model.SolicitudRecarga;
import co.edu.uniquindio.cinemauq.model.enums.EstadoSolicitud;
import co.edu.uniquindio.cinemauq.model.enums.TipoMovimiento;
import co.edu.uniquindio.cinemauq.util.Formato;
import co.edu.uniquindio.cinemauq.util.GeneradorCodigo;

import java.time.LocalDateTime;
import java.util.List;

/** SRP: recargas de tarjetas virtuales y solicitudes de recarga. */
public class TarjetaService {

    public static final double RECARGA_MINIMA = 5_000;
    public static final double RECARGA_MAXIMA = 2_000_000;

    private final BaseDatos db;

    public TarjetaService(BaseDatos db) {
        this.db = db;
    }

    public SolicitudRecarga solicitarRecarga(Cliente cliente, double monto, LocalDateTime ahora) throws DatoInvalidoException {
        validarMonto(monto);
        SolicitudRecarga s = new SolicitudRecarga(GeneradorCodigo.siguiente("SOL"), cliente, monto, ahora);
        db.getSolicitudesRecarga().add(s);
        return s;
    }

    /** Solo el administrador puede recargar. */
    public void recargar(Cliente cliente, double monto, LocalDateTime ahora) throws DatoInvalidoException {
        validarMonto(monto);
        cliente.getTarjeta().acreditar(monto, TipoMovimiento.RECARGA, "Recarga realizada por el administrador", ahora);
        cliente.agregarNotificacion("Su tarjeta fue recargada con " + Formato.moneda(monto) + ".");
    }

    public void aprobarSolicitud(SolicitudRecarga s, LocalDateTime ahora) throws CinemaException {
        if (s.getEstado() != EstadoSolicitud.PENDIENTE) {
            throw new CinemaException("La solicitud ya fue atendida.");
        }
        recargar(s.getCliente(), s.getMonto(), ahora);
        s.setEstado(EstadoSolicitud.APROBADA);
    }

    public void rechazarSolicitud(SolicitudRecarga s) throws CinemaException {
        if (s.getEstado() != EstadoSolicitud.PENDIENTE) {
            throw new CinemaException("La solicitud ya fue atendida.");
        }
        s.setEstado(EstadoSolicitud.RECHAZADA);
        s.getCliente().agregarNotificacion("Su solicitud de recarga " + s.getCodigo() + " fue rechazada.");
    }

    public void cambiarEstadoTarjeta(Cliente cliente) {
        cliente.getTarjeta().setActiva(!cliente.getTarjeta().isActiva());
    }

    public List<SolicitudRecarga> listarSolicitudes() { return db.getSolicitudesRecarga(); }

    private void validarMonto(double monto) throws DatoInvalidoException {
        if (monto < RECARGA_MINIMA || monto > RECARGA_MAXIMA) {
            throw new DatoInvalidoException("La recarga debe estar entre " + Formato.moneda(RECARGA_MINIMA)
                    + " y " + Formato.moneda(RECARGA_MAXIMA) + ".");
        }
    }
}
