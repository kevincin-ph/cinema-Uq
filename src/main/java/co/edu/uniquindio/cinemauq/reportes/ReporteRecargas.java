package co.edu.uniquindio.cinemauq.reportes;

import co.edu.uniquindio.cinemauq.model.Cliente;
import co.edu.uniquindio.cinemauq.model.Movimiento;
import co.edu.uniquindio.cinemauq.model.enums.TipoMovimiento;
import co.edu.uniquindio.cinemauq.service.EstadisticasService;
import co.edu.uniquindio.cinemauq.util.Formato;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ReporteRecargas extends ReporteBase {

    private double total;

    public ReporteRecargas(EstadisticasService e) { super(e); }

    @Override protected String getTitulo() { return "Recargas realizadas"; }

    @Override
    protected String getEncabezadoColumnas() {
        return String.format("%-17s %-28s %-18s %12s", "Fecha", "Cliente", "Tarjeta", "Monto");
    }

    @Override
    protected List<String> construirFilas(LocalDate desde, LocalDate hasta) {
        List<String> filas = new ArrayList<>();
        total = 0;
        for (Cliente c : estadisticas.clientes()) {
            for (Movimiento m : c.getTarjeta().getMovimientos()) {
                if (m.getTipo() == TipoMovimiento.RECARGA && EstadisticasService.enPeriodo(m.getFecha(), desde, hasta)) {
                    filas.add(String.format("%-17s %-28s %-18s %12s", Formato.fechaHora(m.getFecha()),
                            ReporteVentas.corto(c.getNombre(), 28), c.getTarjeta().getNumero(), Formato.moneda(m.getMonto())));
                    total += m.getMonto();
                }
            }
        }
        return filas;
    }

    @Override protected String construirTotales() { return "TOTAL RECARGADO: " + Formato.moneda(total); }
}
