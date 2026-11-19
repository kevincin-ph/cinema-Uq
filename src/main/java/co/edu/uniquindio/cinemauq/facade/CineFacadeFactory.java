package co.edu.uniquindio.cinemauq.facade;

import co.edu.uniquindio.cinemauq.data.BaseDatos;
import co.edu.uniquindio.cinemauq.data.DatosIniciales;
import co.edu.uniquindio.cinemauq.puntos.ReglaPuntosCinemaUQ;
import co.edu.uniquindio.cinemauq.reportes.ExportadorTxt;
import co.edu.uniquindio.cinemauq.service.AuthService;
import co.edu.uniquindio.cinemauq.service.CarteleraService;
import co.edu.uniquindio.cinemauq.service.CompraService;
import co.edu.uniquindio.cinemauq.service.ConfiteriaService;
import co.edu.uniquindio.cinemauq.service.EstadisticasService;
import co.edu.uniquindio.cinemauq.service.ReporteService;
import co.edu.uniquindio.cinemauq.service.TarjetaService;

/**
 * Arma todas las dependencias en un solo lugar (DIP): aquí se decide qué
 * implementaciones concretas se usan (regla de puntos, exportador TXT, etc.).
 */
public final class CineFacadeFactory {

    private CineFacadeFactory() { }

    public static CineFacade crear(boolean cargarDatosIniciales) {
        BaseDatos db = BaseDatos.getInstancia();
        AuthService auth = new AuthService(db);
        CarteleraService cartelera = new CarteleraService(db);
        ConfiteriaService confiteria = new ConfiteriaService(db);
        CompraService compras = new CompraService(db, new ReglaPuntosCinemaUQ());
        TarjetaService tarjetas = new TarjetaService(db);
        EstadisticasService estadisticas = new EstadisticasService(db);
        ReporteService reportes = new ReporteService(db, estadisticas, new ExportadorTxt());
        if (cargarDatosIniciales && db.getUsuarios().isEmpty()) {
            new DatosIniciales(db, auth, cartelera, confiteria, compras, tarjetas).cargar();
        }
        return new CineFacade(db, auth, cartelera, confiteria, compras, tarjetas, estadisticas, reportes);
    }
}
