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

- `domain`: entidades y puertos (`Tienda`, `TiendaRepository`)
- `application`: casos de uso (`CreateTiendaUseCase`, `GetApplicablePriceUseCase`)
- `infrastructure`: adaptadores, entidades JPA, repositorios Spring Data, controladores y configuración de seguridad

### Regla de dependencias

- `domain` no depende de frameworks
- `application` depende de puertos del dominio
- `infrastructure` implementa puertos y expone API REST

## Diagrama de clases

- Archivo local: [`diagrama-clases-tienda.pdf`](./diagrama-clases-tienda.pdf)
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

Ruta base: `http://localhost:8080/tiendas`

### Crear tarifa

`POST /tiendas`

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

Devuelve la tarifa activa para esa fecha, producto y marca, aplicando prioridad descendente.

## Datos iniciales de ejemplo

`src/main/resources/data.sql` crea registros base:

- `brands`: ZARA (`id=1`)
- `products`: CAMISETA (`id=35455`)

## Calidad y pruebas

Actualmente hay pruebas de:

- Dominio (`TiendaTest`)
- Casos de uso (`CreateTiendaUseCaseTest`, `GetApplicablePriceUseCaseTest`)
- Controlador/integración (`TiendaControllerTest`, `TiendaPrecioEndpointIntegrationTest`)

## Plan de evolución (enfoque para recruiters)

### Fase 1 - Dominio retail completo

- Modelar agregados: `Pedido`, `LineaPedido`, `Devolucion`, `CambioTalla`, `Stock`
- Añadir reglas de negocio:
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

### Fase 3 - Exposición y observabilidad

- Endpoints para compra, devolución y cambio de talla
- Registros estructurados y métricas
- Trazabilidad de reglas aplicadas en cada operación

### Fase 4 - Entorno empresarial

- SQL productivo (por ejemplo MySQL/PostgreSQL/Oracle, gestionable con SQL Developer)
- Contenedorización con Docker Compose
- Canalización de integración continua (build + pruebas + calidad estática)

## Notas de seguridad

Configuración actual orientada a entorno de desarrollo:

- Acceso abierto para facilitar pruebas
- Consola H2 habilitada (`/h2-console`)

Para entorno real se recomienda endurecer autenticación/autorización y desactivar la consola H2.

## Autor

Proyecto mantenido por Manuel Santos Taboada como laboratorio de arquitectura hexagonal y portfolio técnico.
