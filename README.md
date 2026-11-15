# TP2 / AE4 - Paradigmas y Lenguajes de Programación II
Sistema de facturación en Java (proyecto Maven). Etapa 1 de la AE4: **arquitectura en capas**.
Autor: Elio Lionel Britez

## Ejecutar
- Menú: clase `com.example.presentacion.Main` (`mvn compile exec:java`, o desde el IDE).
- Recorrido automático: `com.example.presentacion.Demo`.

## Capas (paquetes bajo `src/main/java/com/example`)
| Capa | Paquetes | Responsabilidad | Puede depender de |
|---|---|---|---|
| Presentación | `presentacion` | Entrada/salida por consola (`Menu`, `Pantalla`) y arranque (`Main`, `Aplicacion`) | servicio, dominio |
| Servicio | `servicio` | Casos de uso: clientes, facturación, guardar/recuperar | dominio, interfaz `Repositorio` |
| Dominio | `actores`, `catalogo`, `pagos`, `gestion`, `interfaces`, `excepciones` | Entidades y reglas del negocio | solo el propio dominio |
| Persistencia | `persistencia` | Interfaz `Repositorio` + implementación en archivos de texto | dominio |

`Main` solo inicia el sistema. `Aplicacion` es el único punto que conoce las implementaciones concretas
de persistencia y arma las capas.
