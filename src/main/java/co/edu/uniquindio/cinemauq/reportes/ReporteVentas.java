package co.edu.uniquindio.cinemauq.reportes;

import co.edu.uniquindio.cinemauq.model.Compra;
import co.edu.uniquindio.cinemauq.service.EstadisticasService;
import co.edu.uniquindio.cinemauq.util.Formato;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ReporteVentas extends ReporteBase {

    private double total;
    private double descuentos;
    private double reembolsado;

    public ReporteVentas(EstadisticasService e) { super(e); }

    @Override protected String getTitulo() { return "Ventas realizadas por periodo"; }

    @Override
    protected String getEncabezadoColumnas() {
        return String.format("%-8s %-17s %-18s %-16s %-7s %12s", "Código", "Fecha", "Cliente", "Película", "Estado", "Total");
    }

    @Override
    protected List<String> construirFilas(LocalDate desde, LocalDate hasta) {
        List<String> filas = new ArrayList<>();
        total = 0;
        descuentos = 0;
        reembolsado = 0;
        for (Compra c : estadisticas.comprasEnPeriodo(desde, hasta)) {
            filas.add(String.format("%-8s %-17s %-18s %-16s %-7s %12s", c.getCodigo(), Formato.fechaHora(c.getFecha()),
                    corto(c.getCliente().getNombre(), 18), corto(c.getFuncion().getPelicula().getTitulo(), 16),
                    c.isReembolsada() ? "REEMB." : "PAGADA", Formato.moneda(c.getTotal())));
            if (c.isReembolsada()) {
                reembolsado += c.getReembolso().getMonto();
            }
            total += c.getTotal();
            descuentos += c.getDescuento();
        }
        return filas;
    }

    @Override
    protected String construirTotales() {
        return "Total descuentos aplicados: " + Formato.moneda(descuentos)
                + "\nTOTAL VENTAS (BRUTO): " + Formato.moneda(total)
                + "\nReembolsado de estas ventas: " + Formato.moneda(reembolsado)
                + "\nVENTAS NETAS: " + Formato.moneda(total - reembolsado);
    }

    static String corto(String s, int max) { return s.length() <= max ? s : s.substring(0, max - 1) + "."; }
}
