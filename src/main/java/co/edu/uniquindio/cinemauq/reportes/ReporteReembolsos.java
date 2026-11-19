package co.edu.uniquindio.cinemauq.reportes;

import co.edu.uniquindio.cinemauq.data.BaseDatos;
import co.edu.uniquindio.cinemauq.model.Compra;
import co.edu.uniquindio.cinemauq.service.EstadisticasService;
import co.edu.uniquindio.cinemauq.util.Formato;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ReporteReembolsos extends ReporteBase {

    private final BaseDatos db;
    private double total;

    public ReporteReembolsos(EstadisticasService e, BaseDatos db) {
        super(e);
        this.db = db;
    }

    @Override protected String getTitulo() { return "Cancelaciones y reembolsos"; }

    @Override
    protected String getEncabezadoColumnas() {
        return String.format("%-8s %-17s %-18s %5s %12s  %s", "Compra", "Fecha", "Cliente", "%", "Devuelto", "Motivo");
    }

    @Override
    protected List<String> construirFilas(LocalDate desde, LocalDate hasta) {
        List<String> filas = new ArrayList<>();
        total = 0;
        for (Compra c : db.getCompras()) {
            if (c.getReembolso() != null && EstadisticasService.enPeriodo(c.getReembolso().getFecha(), desde, hasta)) {
                filas.add(String.format("%-8s %-17s %-18s %5d %12s  %s", c.getCodigo(), Formato.fechaHora(c.getReembolso().getFecha()),
                        ReporteVentas.corto(c.getCliente().getNombre(), 18), (int) c.getReembolso().getPorcentaje(),
                        Formato.moneda(c.getReembolso().getMonto()), c.getReembolso().getMotivo()));
                total += c.getReembolso().getMonto();
            }
        }
        return filas;
    }

    @Override protected String construirTotales() { return "TOTAL REEMBOLSADO: " + Formato.moneda(total); }
}
