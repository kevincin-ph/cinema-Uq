package co.edu.uniquindio.cinemauq.data;

import co.edu.uniquindio.cinemauq.builder.CompraBuilder;
import co.edu.uniquindio.cinemauq.exceptions.CinemaException;
import co.edu.uniquindio.cinemauq.model.Administrador;
import co.edu.uniquindio.cinemauq.model.Cliente;
import co.edu.uniquindio.cinemauq.model.Compra;
import co.edu.uniquindio.cinemauq.model.Funcion;
import co.edu.uniquindio.cinemauq.model.Pelicula;
import co.edu.uniquindio.cinemauq.model.Promocion;
import co.edu.uniquindio.cinemauq.model.Sala;
import co.edu.uniquindio.cinemauq.model.confiteria.Combo;
import co.edu.uniquindio.cinemauq.model.confiteria.ItemConfiteria;
import co.edu.uniquindio.cinemauq.model.confiteria.ProductoConfiteria;
import co.edu.uniquindio.cinemauq.model.enums.AplicaPromocion;
import co.edu.uniquindio.cinemauq.model.enums.CategoriaProducto;
import co.edu.uniquindio.cinemauq.model.enums.Genero;
import co.edu.uniquindio.cinemauq.model.enums.MetodoPago;
import co.edu.uniquindio.cinemauq.service.AuthService;
import co.edu.uniquindio.cinemauq.service.CarteleraService;
import co.edu.uniquindio.cinemauq.service.CompraService;
import co.edu.uniquindio.cinemauq.service.ConfiteriaService;
import co.edu.uniquindio.cinemauq.service.TarjetaService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Datos iniciales para demostrar la aplicación. Se cargan usando los mismos servicios,
 * así también cumplen las reglas de negocio (con fechas pasadas para tener historial).
 *
 * Usuarios de prueba:
 *   admin@cinemauq.com / admin123  (administrador)
 *   ana@mail.com / 1234            (cliente con saldo, puntos y compras)
 *   carlos@mail.com / 1234         (cliente con poco saldo y una solicitud de recarga)
 *   laura@mail.com / 1234
 *   pedro@mail.com / 1234          (tarjeta bloqueada)
 */
public class DatosIniciales {

    private final BaseDatos db;
    private final AuthService auth;
    private final CarteleraService cartelera;
    private final ConfiteriaService confiteria;
    private final CompraService compras;
    private final TarjetaService tarjetas;

    private final LocalDateTime ahora = LocalDateTime.now();
    private final LocalDateTime hoy = LocalDate.now().atStartOfDay();

    public DatosIniciales(BaseDatos db, AuthService auth, CarteleraService cartelera, ConfiteriaService confiteria,
                          CompraService compras, TarjetaService tarjetas) {
        this.db = db;
        this.auth = auth;
        this.cartelera = cartelera;
        this.confiteria = confiteria;
        this.compras = compras;
        this.tarjetas = tarjetas;
    }

    public void cargar() {
        try {
            cargarTodo();
        } catch (CinemaException e) {
            throw new IllegalStateException("Error cargando datos iniciales: " + e.getMessage(), e);
        }
    }

    private void cargarTodo() throws CinemaException {
        // ---------- Usuarios ----------
        db.getUsuarios().add(new Administrador("ADM-001", "Administrador CinemaUQ", "admin@cinemauq.com", "admin123"));
        Cliente ana = auth.registrarCliente("Ana María Gómez", "ana@mail.com", "1234", "1094000111", "3101234567");
        Cliente carlos = auth.registrarCliente("Carlos Pérez", "carlos@mail.com", "1234", "1094000222", "3157654321");
        Cliente laura = auth.registrarCliente("Laura Restrepo", "laura@mail.com", "1234", "1094000333", "3001112233");
        Cliente pedro = auth.registrarCliente("Pedro Castaño", "pedro@mail.com", "1234", "1094000444", "3204445566");
        Cliente sofia = auth.registrarCliente("Sofía Londoño", "sofia@mail.com", "1234", "1094000555", "3109998877");

        tarjetas.recargar(ana, 250_000, hoy.minusDays(30).plusHours(10));
        tarjetas.recargar(carlos, 120_000, hoy.minusDays(21).plusHours(9));
        tarjetas.recargar(laura, 200_000, hoy.minusDays(25).plusHours(16));
        tarjetas.recargar(sofia, 2_000_000, hoy.minusDays(16).plusHours(10));
        tarjetas.recargar(sofia, 1_500_000, hoy.minusDays(6).plusHours(10));
        tarjetas.recargar(pedro, 150_000, hoy.minusDays(18).plusHours(11));
        tarjetas.recargar(ana, 100_000, hoy.minusDays(8).plusHours(12));
        tarjetas.recargar(laura, 40_000, hoy.minusDays(4).plusHours(15));

        // Puntos con distintas fechas: uno vencido y uno próximo a vencer
        ana.getCuentaPuntos().acumular(500, ahora.minusMonths(13), "Bono de bienvenida");
        ana.getCuentaPuntos().acumular(800, ahora.minusMonths(11), "Bono de fidelidad");

        // ---------- Películas ----------
        Pelicula p1 = cartelera.agregarPelicula("Dune: Parte Dos", Genero.CIENCIA_FICCION, 166, "+12",
                "Paul Atreides se une a los Fremen para vengar a su familia.");
        Pelicula p2 = cartelera.agregarPelicula("Intensamente 2", Genero.ANIMACION, 96, "Todos",
                "Riley llega a la adolescencia y aparecen nuevas emociones.");
        Pelicula p3 = cartelera.agregarPelicula("El Conjuro 4", Genero.TERROR, 112, "+15",
                "Los Warren enfrentan su último caso.");
        Pelicula p4 = cartelera.agregarPelicula("Misión Café", Genero.ACCION, 118, "+12",
                "Un agente debe proteger la cosecha más valiosa del Quindío.");
        Pelicula p5 = cartelera.agregarPelicula("Amor en Salento", Genero.ROMANCE, 104, "+7",
                "Dos viajeros se conocen en el Valle de Cocora.");
        Pelicula p6 = cartelera.agregarPelicula("Moana 2", Genero.AVENTURA, 100, "Todos",
                "Moana emprende un nuevo viaje por los mares de Oceanía.");

        // ---------- Salas ----------
        Sala s1 = cartelera.agregarSala("Sala 1", 8, 10, 2);
        Sala s2 = cartelera.agregarSala("Sala 2", 6, 8, 1);
        Sala s3 = cartelera.agregarSala("Sala 3 VIP", 5, 8, 2);

        // ---------- Confitería ----------
        ProductoConfiteria crisP = confiteria.agregarProducto("Crispetas pequeñas", 9_000, CategoriaProducto.COMIDA);
        ProductoConfiteria crisG = confiteria.agregarProducto("Crispetas grandes", 14_000, CategoriaProducto.COMIDA);
        ProductoConfiteria gaseosa = confiteria.agregarProducto("Gaseosa 500 ml", 6_000, CategoriaProducto.BEBIDA);
        ProductoConfiteria agua = confiteria.agregarProducto("Agua", 4_000, CategoriaProducto.BEBIDA);
        ProductoConfiteria perro = confiteria.agregarProducto("Perro caliente", 12_000, CategoriaProducto.COMIDA);
        ProductoConfiteria nachos = confiteria.agregarProducto("Nachos con queso", 13_000, CategoriaProducto.COMIDA);
        ProductoConfiteria choco = confiteria.agregarProducto("Chocolatina", 4_500, CategoriaProducto.DULCE);
        Combo personal = confiteria.crearCombo("Combo Personal", List.of(crisP, gaseosa), 10);
        Combo pareja = confiteria.crearCombo("Combo Pareja", List.of(crisG, gaseosa, gaseosa, choco), 15);
        Combo familiar = confiteria.crearCombo("Combo Familiar", List.of(pareja, perro, perro, agua), 10); // combo dentro de combo

        Promocion martes = confiteria.agregarPromocion("Martes de cine", 20, AplicaPromocion.ENTRADAS);
        Promocion dulce = confiteria.agregarPromocion("Semana dulce", 10, AplicaPromocion.CONFITERIA);
        Promocion navidad = confiteria.agregarPromocion("Navidad UQ", 15, AplicaPromocion.TODO);
        navidad.setActiva(false);

        // ---------- Funciones pasadas (para reportes y estadísticas) ----------
        Funcion f1 = pasada(p1, s1, 14, 19, 12_000);
        Funcion f2 = pasada(p2, s2, 13, 16, 10_000);
        Funcion f3 = pasada(p3, s3, 12, 21, 15_000);
        Funcion f4 = pasada(p4, s1, 10, 18, 12_000);
        Funcion f5 = pasada(p2, s1, 9, 15, 10_000);
        Funcion f6 = pasada(p5, s2, 7, 19, 10_000);
        Funcion f7 = pasada(p1, s3, 6, 20, 15_000);
        Funcion f8 = pasada(p6, s2, 5, 16, 10_000);
        Funcion f9 = pasada(p4, s1, 3, 20, 12_000);
        Funcion f10 = pasada(p3, s1, 2, 21, 12_000);
        Funcion f11 = pasada(p6, s3, 1, 17, 15_000);

        comprar(ana, f1, new String[]{"C4", "C5"}, d(1, f1), null, MetodoPago.TARJETA_VIRTUAL, pareja, 1);
        comprar(carlos, f1, new String[]{"G3"}, d(2, f1), null, MetodoPago.TARJETA_VIRTUAL, gaseosa, 1);
        comprar(laura, f2, new String[]{"B1", "B2", "B3"}, d(1, f2), null, MetodoPago.TARJETA_VIRTUAL, personal, 3);
        comprar(ana, f3, new String[]{"D1", "D2"}, d(3, f3), martes, MetodoPago.TARJETA_VIRTUAL, nachos, 1);
        comprar(pedro, f4, new String[]{"E5", "E6"}, d(1, f4), null, MetodoPago.TARJETA_VIRTUAL, crisG, 1);
        comprar(ana, f5, new String[]{"D3", "D4", "D5", "D6"}, d(2, f5), dulce, MetodoPago.TARJETA_VIRTUAL, familiar, 1);
        comprar(laura, f6, new String[]{"F1", "F2"}, d(1, f6), null, MetodoPago.TARJETA_VIRTUAL, choco, 2);
        comprar(ana, f7, new String[]{"A1"}, d(1, f7), null, MetodoPago.PUNTOS, null, 0); // redención con puntos
        comprar(carlos, f8, new String[]{"C1", "C2"}, d(2, f8), null, MetodoPago.TARJETA_VIRTUAL, null, 0);
        comprar(laura, f9, new String[]{"D7", "D8"}, d(1, f9), martes, MetodoPago.TARJETA_VIRTUAL, perro, 2);
        comprar(ana, f10, new String[]{"H5", "H6"}, d(1, f10), null, MetodoPago.TARJETA_VIRTUAL, pareja, 1);
        comprar(carlos, f11, new String[]{"E1"}, d(1, f11), null, MetodoPago.TARJETA_VIRTUAL, agua, 1);
        comprar(pedro, f11, new String[]{"E3", "E4"}, d(1, f11), null, MetodoPago.TARJETA_VIRTUAL, personal, 2);

        // Sofía organiza salidas en grupo: más ocupación para las estadísticas
        Funcion[] grupales = {f1, f2, f4, f5, f6, f8, f9, f10, f11};
        String[][] sillas = {{"E1", "E2", "E3", "E4", "E5", "E6", "E7", "E8"}, {"D1", "D2", "D3", "D4", "D5", "D6"},
                {"B1", "B2", "B3", "B4", "B5", "B6", "B7", "B8", "B9", "B10"}, {"F1", "F2", "F3", "F4", "F5", "F6", "F7", "F8"},
                {"A1", "A2", "A3", "A4", "A5"}, {"D1", "D2", "D3", "D4", "D5", "D6", "D7"},
                {"C1", "C2", "C3", "C4", "C5", "C6", "C7", "C8", "C9", "C10"}, {"F1", "F2", "F3", "F4", "F5", "F6"},
                {"B1", "B2", "B3", "B4", "B5", "B6", "B7", "B8"}};
        ItemConfiteria[] combos = {personal, pareja, familiar, crisG, nachos, personal, pareja, perro, gaseosa};
        for (int i = 0; i < grupales.length; i++) {
            comprar(sofia, grupales[i], sillas[i], d(1, grupales[i]).plusHours(i % 4), null, MetodoPago.TARJETA_VIRTUAL,
                    combos[i], 1 + i % 3);
        }

        // Reembolsos: Carlos cancela con más de 24 h (100 %) y Laura entre 2 y 24 h (80 %)
        Compra cancelable1 = comprar(carlos, f9, new String[]{"A1", "A2"}, d(4, f9), null, MetodoPago.TARJETA_VIRTUAL, null, 0);
        compras.cancelarCompra(cancelable1, f9.getFechaHora().minusDays(3));
        Compra cancelable2 = comprar(laura, f10, new String[]{"B4"}, d(2, f10), null, MetodoPago.TARJETA_VIRTUAL, crisP, 1);
        compras.cancelarCompra(cancelable2, f10.getFechaHora().minusHours(5));

        // Función cancelada por el cine (100 % a todos)
        Funcion fc = pasada(p5, s3, 4, 15, 15_000);
        comprar(ana, fc, new String[]{"B1", "B2"}, d(2, fc), null, MetodoPago.TARJETA_VIRTUAL, null, 0);
        comprar(laura, fc, new String[]{"C3"}, d(1, fc), null, MetodoPago.TARJETA_VIRTUAL, agua, 1);
        compras.cancelarFuncion(fc, fc.getFechaHora().minusHours(10));

        // Bloqueamos la tarjeta de Pedro para demostrar la validación de tarjeta activa
        pedro.getTarjeta().setActiva(false);

        // ---------- Funciones futuras (para comprar desde la app) ----------
        Pelicula[] rotacion = {p2, p4, p5, p6, p1, p3};
        Sala[] salas = {s1, s2, s3};
        int[] minutos = {13 * 60, 16 * 60 + 30, 20 * 60}; // 3,5 h entre funciones
        int k = 0;
        Funcion primeraManana = null;
        for (int dia = 1; dia <= 6; dia++) {
            for (Sala s : salas) {
                for (int m : minutos) {
                    Pelicula p = rotacion[k % rotacion.length];
                    k++;
                    double precio = s == s3 ? 16_000 : (m == 13 * 60 ? 9_000 : 12_000);
                    Funcion f = cartelera.programarFuncion(p, s, hoy.plusDays(dia).plusMinutes(m), precio, ahora);
                    if (primeraManana == null) {
                        primeraManana = f;
                    }
                }
            }
        }
        // Funciones próximas para probar las reglas de cancelación (80 % y no permitida)
        Funcion en5Horas = cartelera.programarFuncion(p2, s3, ahora.plusHours(5).withSecond(0).withNano(0), 14_000, ahora);
        Funcion en90Min = cartelera.programarFuncion(p6, s2, ahora.plusMinutes(90).withSecond(0).withNano(0), 12_000, ahora);

        LocalDateTime haceUnRato = ahora.minusHours(1);
        comprar(ana, primeraManana, new String[]{"D5", "D6"}, haceUnRato, null, MetodoPago.TARJETA_VIRTUAL, personal, 2);
        comprar(carlos, primeraManana, new String[]{"E1", "E2", "E3"}, haceUnRato, null, MetodoPago.TARJETA_VIRTUAL, null, 0);
        comprar(ana, en5Horas, new String[]{"A3"}, haceUnRato, null, MetodoPago.TARJETA_VIRTUAL, agua, 1);
        comprar(ana, en90Min, new String[]{"C4"}, haceUnRato, null, MetodoPago.TARJETA_VIRTUAL, null, 0);
        comprar(laura, en90Min, new String[]{"C5", "C6"}, haceUnRato, null, MetodoPago.TARJETA_VIRTUAL, null, 0);

        // Solicitud de recarga pendiente
        tarjetas.solicitarRecarga(carlos, 50_000, ahora.minusHours(3));
    }

    /** Programa una función en el pasado (se simula que se programó con anticipación). */
    private Funcion pasada(Pelicula p, Sala s, int diasAtras, int hora, double precio) throws CinemaException {
        LocalDateTime fecha = hoy.minusDays(diasAtras).plusHours(hora);
        return cartelera.programarFuncion(p, s, fecha, precio, fecha.minusDays(20));
    }

    /** Fecha de compra: N días antes de la función. */
    private LocalDateTime d(int diasAntes, Funcion f) {
        return f.getFechaHora().minusDays(diasAntes).withHour(11);
    }

    private Compra comprar(Cliente c, Funcion f, String[] asientos, LocalDateTime fecha, Promocion promo,
                           MetodoPago metodo, ItemConfiteria item, int cantidad) throws CinemaException {
        CompraBuilder b = new CompraBuilder(c, f).conPromocion(promo);
        for (String a : asientos) {
            b.conAsiento(f.getSala().buscarAsiento(a));
        }
        if (item != null) {
            b.conItem(item, cantidad);
        }
        return compras.confirmarCompra(b.build(), metodo, fecha);
    }
}
