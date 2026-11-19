# CinemaUQ – Sistema integral de gestión de cine

Proyecto final de Programación II (Universidad del Quindío). Java 17+, JavaFX 21, Maven, arquitectura MVC.

## Cómo ejecutar
1. Abrir la carpeta en IntelliJ IDEA como proyecto Maven (esperar a que descargue JavaFX).
2. Ejecutar `co.edu.uniquindio.cinemauq.App`, o en la pestaña Maven: `Plugins > javafx > javafx:run`.

## Usuarios de prueba
| Rol | Correo | Contraseña |
|---|---|---|
| Administrador | admin@cinemauq.com | admin123 |
| Cliente (saldo, puntos y compras) | ana@mail.com | 1234 |
| Cliente (solicitud de recarga pendiente) | carlos@mail.com | 1234 |
| Clientes | laura@mail.com, sofia@mail.com | 1234 |
| Cliente con tarjeta bloqueada | pedro@mail.com | 1234 |

## Patrones implementados
- Creacionales: Singleton (`BaseDatos`), Builder (`CompraBuilder`), Factory Method (`CreadorEntrada`), Prototype (`Funcion.clonar()`).
- Estructurales: Composite (`ItemConfiteria`/`Combo`), Decorator (`PromocionDecorator`), Adapter (`TarjetaVirtualAdapter`, `PuntosAdapter`), Facade (`CineFacade`).
- Comportamiento: Strategy (`PoliticaReembolso`), State (`EstadoCompra`), Observer (`ObservadorFuncion`), Template Method (`ReporteBase`).

## Regla de puntos
1 punto por cada $100 pagados con tarjeta. 1 punto = $10 al redimir. Vigencia de 1 año; se usan primero los más próximos a vencer.

El diagrama de clases está en `docs/DiagramaClases_CinemaUQ.pdf`.
