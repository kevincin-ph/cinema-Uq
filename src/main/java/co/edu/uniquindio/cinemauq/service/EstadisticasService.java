package co.edu.uniquindio.cinemauq.service;

import co.edu.uniquindio.cinemauq.data.BaseDatos;
import co.edu.uniquindio.cinemauq.model.Cliente;
import co.edu.uniquindio.cinemauq.model.Compra;
import co.edu.uniquindio.cinemauq.model.Funcion;
import co.edu.uniquindio.cinemauq.model.LineaConfiteria;
import co.edu.uniquindio.cinemauq.model.Movimiento;
import co.edu.uniquindio.cinemauq.model.Sala;
import co.edu.uniquindio.cinemauq.model.enums.EstadoFuncion;
import co.edu.uniquindio.cinemauq.model.enums.TipoMovimiento;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

/** SRP: calcula estadísticas para el administrador y los reportes visuales. */
public class EstadisticasService {

    private final BaseDatos db;

    public EstadisticasService(BaseDatos db) {
        this.db = db;
    }

    public List<Compra> comprasEnPeriodo(LocalDate desde, LocalDate hasta) {
        return db.getCompras().stream().filter(c -> enPeriodo(c.getFecha(), desde, hasta)).toList();
    }

    public double totalVentas(LocalDate desde, LocalDate hasta) {
        return comprasEnPeriodo(desde, hasta).stream().mapToDouble(Compra::getTotal).sum();
    }

    public double totalReembolsos(LocalDate desde, LocalDate hasta) {
        return db.getCompras().stream().filter(c -> c.getReembolso() != null && enPeriodo(c.getReembolso().getFecha(), desde, hasta))
                .mapToDouble(c -> c.getReembolso().getMonto()).sum();
    }

    public long cantidadReembolsos(LocalDate desde, LocalDate hasta) {
        return db.getCompras().stream().filter(c -> c.getReembolso() != null && enPeriodo(c.getReembolso().getFecha(), desde, hasta)).count();
    }

    /** Entradas vendidas (no reembolsadas) por película, de mayor a menor. */
    public Map<String, Integer> entradasPorPelicula(LocalDate desde, LocalDate hasta) {
        Map<String, Integer> mapa = new TreeMap<>();
        for (Compra c : comprasEnPeriodo(desde, hasta)) {
            if (!c.isReembolsada()) {
                mapa.merge(c.getFuncion().getPelicula().getTitulo(), c.getEntradas().size(), Integer::sum);
            }
        }
        return ordenarDesc(mapa);
    }

    /** Productos y combos vendidos (unidades), de mayor a menor. */
    public Map<String, Integer> productosVendidos(LocalDate desde, LocalDate hasta) {
        Map<String, Integer> mapa = new TreeMap<>();
        for (Compra c : comprasEnPeriodo(desde, hasta)) {
            if (!c.isReembolsada()) {
                for (LineaConfiteria l : c.getLineasConfiteria()) {
                    mapa.merge(l.getItem().getNombre(), l.getCantidad(), Integer::sum);
                }
            }
        }
        return ordenarDesc(mapa);
    }

    /** Ocupación de cada sala (asientos vendidos / capacidad) en las funciones del periodo. */
    public Map<String, Double> ocupacionPorSala(LocalDate desde, LocalDate hasta) {
        Map<String, Double> mapa = new LinkedHashMap<>();
        for (Sala s : db.getSalas()) {
            List<Funcion> funciones = db.getFunciones().stream()
                    .filter(f -> f.getSala() == s && f.getEstado() != EstadoFuncion.CANCELADA && enPeriodo(f.getFechaHora(), desde, hasta))
                    .toList();
            int capacidad = funciones.size() * s.getCapacidad();
            int ocupados = funciones.stream().mapToInt(Funcion::getCantidadOcupados).sum();
            mapa.put(s.getNombre(), capacidad == 0 ? 0 : (double) ocupados / capacidad);
        }
        return mapa;
    }

    public double ocupacionPromedio(LocalDate desde, LocalDate hasta) {
        List<Funcion> funciones = db.getFunciones().stream()
                .filter(f -> f.getEstado() != EstadoFuncion.CANCELADA && enPeriodo(f.getFechaHora(), desde, hasta)).toList();
        return funciones.stream().mapToDouble(Funcion::getOcupacion).average().orElse(0);
    }

    /** Ventas por categoría (entradas, bebidas, comidas, dulces, combos). */
    public Map<String, Double> ventasPorCategoria(LocalDate desde, LocalDate hasta) {
        Map<String, Double> mapa = new LinkedHashMap<>();
        for (Compra c : comprasEnPeriodo(desde, hasta)) {
            mapa.merge("ENTRADAS", c.getSubtotalEntradas(), Double::sum);
            for (LineaConfiteria l : c.getLineasConfiteria()) {
                mapa.merge(l.getItem().getCategoria().name(), l.getSubtotal(), Double::sum);
            }
        }
        return mapa;
    }

    public Map<LocalDate, Double> ventasPorDia(LocalDate desde, LocalDate hasta) {
        Map<LocalDate, Double> mapa = new TreeMap<>();
        for (Compra c : comprasEnPeriodo(desde, hasta)) {
            mapa.merge(c.getFecha().toLocalDate(), c.getTotal(), Double::sum);
        }
        return mapa;
    }

    public Map<LocalDate, Double> recargasPorDia(LocalDate desde, LocalDate hasta) {
        Map<LocalDate, Double> mapa = new TreeMap<>();
        for (Movimiento m : movimientosRecarga(desde, hasta)) {
            mapa.merge(m.getFecha().toLocalDate(), m.getMonto(), Double::sum);
        }
        return mapa;
    }

    public List<Movimiento> movimientosRecarga(LocalDate desde, LocalDate hasta) {
        return clientes().stream()
                .flatMap(c -> c.getTarjeta().getMovimientos().stream())
                .filter(m -> m.getTipo() == TipoMovimiento.RECARGA && enPeriodo(m.getFecha(), desde, hasta))
                .toList();
    }

    public List<Cliente> clientes() {
        return db.getUsuarios().stream().filter(u -> u instanceof Cliente).map(u -> (Cliente) u).toList();
    }

    public static boolean enPeriodo(LocalDateTime fecha, LocalDate desde, LocalDate hasta) {
        if (fecha == null) {
            return false;
        }
        LocalDate d = fecha.toLocalDate();
        return (desde == null || !d.isBefore(desde)) && (hasta == null || !d.isAfter(hasta));
    }

    private static Map<String, Integer> ordenarDesc(Map<String, Integer> mapa) {
        return mapa.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (a, b) -> a, LinkedHashMap::new));
    }
}
