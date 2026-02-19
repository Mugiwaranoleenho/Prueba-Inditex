# Arquitectura Hexagonal - Prototipo de Tienda

Proyecto Spring Boot para demostrar una arquitectura hexagonal aplicada a un caso de negocio de tienda, con foco en reglas de precio por fecha/prioridad y evolución hacia un flujo completo de compra, devolución y cambio de talla.

## Objetivo

Construir una base de código limpia y escalable para un prototipo de tienda, separando claramente:

- Dominio (reglas de negocio)
- Aplicación (casos de uso)
- Infraestructura (controladores, persistencia, seguridad)

El objetivo de evolución es soportar varios contextos de negocio:

- Compras
- Devoluciones
- Cambios de talla
- Política de precios y promociones

## Tecnologías actuales

- Java 17
- Spring Boot 3.5.x
- Spring Web
- Spring Data JPA
- Spring Security
- H2 (entorno local y pruebas)
- Driver de MySQL (dependencia incluida)
- Maven Wrapper (`mvnw`/`mvnw.cmd`)

## Arquitectura

### Capas

- `domain`: modelos y puertos (`Tienda`, `Pedido`, `Devolucion`, `CambioTalla`, `Stock`)
- `application`: casos de uso (`CreateTiendaUseCase`, `GetApplicablePriceUseCase`, `CreatePedidoUseCase`, `CreateDevolucionUseCase`, `CreateCambioTallaUseCase`)
- `infrastructure`: adaptadores, entidades JPA, repositorios Spring Data, controladores y configuración de seguridad

### Regla de dependencias

- `domain` no depende de frameworks
- `application` depende de puertos del dominio
- `infrastructure` implementa puertos y expone API REST

## Diagrama de clases

- Archivo local actualizado (Mermaid): [`diagrama-clases-tienda.md`](./diagrama-clases-tienda.md)
- Archivo histórico (PDF): [`diagrama-clases-tienda.pdf`](./diagrama-clases-tienda.pdf)
- GitHub (visualización): <https://github.com/Mugiwaranoleenho/arquitectura.hexagonal/blob/main/diagrama-clases-tienda.pdf>
- GitHub raw: <https://raw.githubusercontent.com/Mugiwaranoleenho/arquitectura.hexagonal/main/diagrama-clases-tienda.pdf>

## Estructura del proyecto

```text
src/main/java/com/manolinho
├─ domain
│  ├─ model
│  └─ repository
├─ application
│  └─ usecase
└─ infrastructure
   ├─ controller
   ├─ config
   └─ persistence
      ├─ adapter
      ├─ entity
      └─ repository
```

## Cómo ejecutar

### 1) Compilar

```bash
./mvnw -q -DskipTests compile
```

En Windows (PowerShell):

```powershell
.\mvnw.cmd -q -DskipTests compile
```

### 2) Lanzar la aplicación

```bash
./mvnw spring-boot:run
```

En Windows (PowerShell):

```powershell
.\mvnw.cmd spring-boot:run
```

### 3) Ejecutar pruebas

```bash
./mvnw test
```

En Windows (PowerShell):

```powershell
.\mvnw.cmd test
```

## API actual

Ruta base: `http://localhost:8080`

Autenticación: `HTTP Basic` en todos los endpoints (excepto que no haya permisos por rol).

### Crear tarifa

`POST /tiendas`

Roles permitidos: `EMPLEADO`, `EMPLEADO_JEFE`, `ADMIN`.

Ejemplo de cuerpo:

```json
{
  "brandId": 1,
  "startDate": "2020-06-14T00:00:00",
  "endDate": "2020-12-31T23:59:59",
  "priceList": 1,
  "productId": 35455,
  "priority": 0,
  "price": 35.50,
  "curr": "EUR"
}
```

### Obtener tarifa aplicable

`GET /tiendas/precio?fechaAplicacion=2020-06-14T10:00:00&productId=35455&brandId=1`

Roles permitidos: `CLIENTE`, `EMPLEADO`, `EMPLEADO_JEFE`, `ADMIN`.

Devuelve la tarifa activa para esa fecha, producto y marca, aplicando prioridad descendente.

Respuesta:

- `originalPrice`: precio base de la tarifa.
- `discountPercent`: descuento aplicado por rol autenticado.
- `finalPrice`: precio final tras descuento.

### Crear pedido

`POST /pedidos`

Roles permitidos: `CLIENTE`, `EMPLEADO`, `EMPLEADO_JEFE`, `ADMIN`.

### Crear devolución

`POST /devoluciones`

Roles permitidos: `CLIENTE`, `EMPLEADO`, `EMPLEADO_JEFE`, `ADMIN`.

Reglas implementadas:

- pedido existente (validación de ticket)
- ventana máxima de devolución de 30 días
- cálculo de reembolso parcial/total en base a líneas devueltas

### Crear cambio de talla

`POST /cambios-talla`

Roles permitidos: `CLIENTE`, `EMPLEADO`, `EMPLEADO_JEFE`, `ADMIN`.

Reglas implementadas:

- pedido existente
- ventana máxima de cambio de talla de 30 días
- disponibilidad de stock en talla destino
- ajuste de stock (entrada talla origen / salida talla destino)

## Datos iniciales de ejemplo

`src/main/resources/data.sql` crea registros base:

- `brands`: ZARA (`id=1`)
- `products`: CAMISETA (`id=35455`)

## Calidad y pruebas

Actualmente hay pruebas de:

- Dominio (`TiendaTest`)
- Casos de uso (`CreateTiendaUseCaseTest`, `GetApplicablePriceUseCaseTest`, `CreateDevolucionUseCaseTest`, `CreateCambioTallaUseCaseTest`)
- Controlador/integración (`TiendaControllerTest`, `TiendaPrecioEndpointIntegrationTest`, `PedidoControllerTest`, `DevolucionControllerTest`, `CambioTallaControllerTest`, `SecurityPricingIntegrationTest`)

## Estado actual (hecho)

- Arquitectura hexagonal operativa con dominio, aplicación e infraestructura desacopladas.
- Modelos de dominio con Lombok.
- Flujo de precios por prioridad implementado.
- Flujo de pedido implementado.
- Flujo de devolución implementado con:
  - validación de ticket (pedido existente)
  - ventana de devolución de 30 días
  - cálculo de reembolso parcial o total
- Flujo de cambio de talla implementado con:
  - validación de ticket (pedido existente)
  - ventana de cambio de 30 días
  - validación de stock talla destino
  - ajuste de stock origen/destino
- Seguridad Spring Security implementada con:
  - autenticación HTTP Basic
  - autorización por roles
  - bloqueo efectivo de `CLIENTE_BLOQUEADO` (sin permisos en endpoints de tienda)
  - descuentos por rol en `GET /tiendas/precio` (`EMPLEADO` 5%, `EMPLEADO_JEFE` 15%)
- Logging técnico en casos de uso, controladores y manejo de errores.
- Diagrama de clases actualizado en Mermaid (`diagrama-clases-tienda.md`).

## Plan de evolución 

### Fase 1 - Dominio retail completo

- Estado: implementada con adaptadores in-memory para `Pedido`, `Devolucion`, `CambioTalla` y `Stock`.
- Reglas de negocio implementadas:
  - ventana máxima de devolución
  - validación de ticket/compra
  - disponibilidad de talla destino
  - reembolso parcial/total
  - OpenAPI/Swagger

### Fase 2 - Persistencia dual SQL + Mongo

- Mantener puertos en dominio y crear adaptadores por tecnología
- SQL (transaccional): pedidos, líneas, pagos, stock
- Mongo (lectura/eventos/auditoría): historial de cambios, trazabilidad de devoluciones
- Definir estrategia de consistencia (outbox/eventos de dominio)
- Activación por perfiles (`sql`, `mongo`, `hybrid`)

Pendiente en Fase 2:

- Sustituir adaptadores in-memory de `Pedido`, `Devolucion`, `CambioTalla` y `Stock` por adaptadores SQL reales.
- Diseñar y aplicar modelo Mongo para historial/auditoría.
- Definir estrategia de consistencia (outbox + publicación de eventos).

### Fase 3 - Exposición y observabilidad

- Endpoints para compra, devolución y cambio de talla
- Registros estructurados y métricas
- Trazabilidad de reglas aplicadas en cada operación

Pendiente en Fase 3:

- Publicar OpenAPI/Swagger.
- Añadir correlación de logs (`traceId`/`requestId`) y métricas funcionales.
- Exponer auditoría de descuentos y decisiones de reglas.

### Fase 4 - Entorno empresarial

- SQL productivo (por ejemplo MySQL/PostgreSQL/Oracle, gestionable con SQL Developer)
- Contenedorización con Docker Compose
- Canalización de integración continua (build + pruebas + calidad estática)

Pendiente en Fase 4:

- Hardening de seguridad para producción (JWT/OAuth2, rotación de secretos, políticas de contraseña).
- Seguridad de datos y operación (rate limiting, CORS estricto, cabeceras de seguridad, RBAC persistido).
- Despliegue productivo y observabilidad centralizada.

## Notas de seguridad

Configuración actual:

- Autenticación `HTTP Basic`.
- Seguridad por roles.
- Consola H2 protegida solo para `ADMIN` (`/h2-console`).
- `cliente_bloqueado` no tiene permisos sobre endpoints de tienda.
- Descuentos:
  - `EMPLEADO`: 5%
  - `EMPLEADO_JEFE`: 15%

Usuarios de desarrollo:

- `cliente` / `cliente123` (`CLIENTE`)
- `cliente_bloqueado` / `bloqueado123` (`CLIENTE_BLOQUEADO`)
- `empleado` / `empleado123` (`EMPLEADO`)
- `empleado_jefe` / `jefe123` (`EMPLEADO_JEFE`)
- `admin` / `admin123` (`ADMIN`)

Para entorno real se recomienda endurecer autenticación/autorización y desactivar la consola H2.

## Autor

Proyecto mantenido por Manuel Santos Taboada como laboratorio de arquitectura hexagonal y portfolio técnico.
