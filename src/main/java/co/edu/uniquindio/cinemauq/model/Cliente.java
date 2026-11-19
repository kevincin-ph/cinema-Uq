package co.edu.uniquindio.cinemauq.model;

import co.edu.uniquindio.cinemauq.notificacion.ObservadorFuncion;
import co.edu.uniquindio.cinemauq.util.Formato;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Cliente del cine. Tiene una tarjeta virtual, una cuenta de puntos y su historial de compras.
 * Patrón Observer: el cliente es observador de las funciones que compró, así se entera si el cine las cancela.
 */
public class Cliente extends Usuario implements ObservadorFuncion {

    private final String documento;
    private final String telefono;
    private final TarjetaVirtual tarjeta;
    private final CuentaPuntos cuentaPuntos = new CuentaPuntos();
    private final List<Compra> compras = new ArrayList<>();
    private final List<String> notificaciones = new ArrayList<>();

    public Cliente(String id, String nombre, String email, String password,
                   String documento, String telefono, TarjetaVirtual tarjeta) {
        super(id, nombre, email, password);
        this.documento = documento;
        this.telefono = telefono;
        this.tarjeta = tarjeta;
    }

    @Override
    public String getRol() { return "CLIENTE"; }

    @Override
    public void actualizar(Funcion funcion, String mensaje) {
        agregarNotificacion(mensaje);
    }

    public void agregarNotificacion(String mensaje) {
        notificaciones.add(0, Formato.fechaHora(LocalDateTime.now()) + " - " + mensaje);
    }

    public void agregarCompra(Compra compra) { compras.add(compra); }

    public String getDocumento() { return documento; }
    public String getTelefono() { return telefono; }
    public TarjetaVirtual getTarjeta() { return tarjeta; }
    public CuentaPuntos getCuentaPuntos() { return cuentaPuntos; }
    public List<Compra> getCompras() { return Collections.unmodifiableList(compras); }
    public List<String> getNotificaciones() { return Collections.unmodifiableList(notificaciones); }
}
