package co.edu.uniquindio.cinemauq.model;

import co.edu.uniquindio.cinemauq.exceptions.SaldoInsuficienteException;
import co.edu.uniquindio.cinemauq.exceptions.TarjetaInactivaException;
import co.edu.uniquindio.cinemauq.model.enums.TipoMovimiento;
import co.edu.uniquindio.cinemauq.util.Formato;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** Tarjeta virtual asociada a la cuenta del cliente. Registra cada movimiento. */
public class TarjetaVirtual {

    private final String numero;
    private double saldo;
    private boolean activa = true;
    private final List<Movimiento> movimientos = new ArrayList<>();

    public TarjetaVirtual(String numero) {
        this.numero = numero;
    }

    /** Verifica que la tarjeta esté activa y tenga saldo suficiente (antes de confirmar la compra). */
    public void verificarPago(double monto) throws TarjetaInactivaException, SaldoInsuficienteException {
        if (!activa) {
            throw new TarjetaInactivaException("La tarjeta " + numero + " está inactiva.");
        }
        if (saldo < monto) {
            throw new SaldoInsuficienteException("Saldo insuficiente. Saldo: " + Formato.moneda(saldo)
                    + ", valor a pagar: " + Formato.moneda(monto) + ". Puede solicitar una recarga.");
        }
    }

    public void debitar(double monto, String descripcion, LocalDateTime fecha)
            throws TarjetaInactivaException, SaldoInsuficienteException {
        verificarPago(monto);
        saldo -= monto;
        movimientos.add(new Movimiento(fecha, TipoMovimiento.PAGO, monto, descripcion, saldo));
    }

    public void acreditar(double monto, TipoMovimiento tipo, String descripcion, LocalDateTime fecha) {
        if (monto <= 0) {
            throw new IllegalArgumentException("El monto debe ser mayor que cero");
        }
        saldo += monto;
        movimientos.add(new Movimiento(fecha, tipo, monto, descripcion, saldo));
    }

    public String getNumero() { return numero; }
    public double getSaldo() { return saldo; }
    public boolean isActiva() { return activa; }
    public void setActiva(boolean activa) { this.activa = activa; }
    public List<Movimiento> getMovimientos() { return Collections.unmodifiableList(movimientos); }
}
