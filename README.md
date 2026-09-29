
![LOGO_BELLACIAO.png](docs/images/LOGO_BELLACIAO.png)


<p align="center">
  <strong>Tradición clásica.</strong><br>
  <em>Experiencia moderna.</em>
</p>

**BellaCiao** es una auténtica *trattoria italiana* que combina la tradición culinaria clásica con la eficiencia de un sistema de gestión moderno.

El restaurante se especializa en **pizzas al horno de leña** y **pastas artesanales**, preparadas con ingredientes de primera calidad. El sistema permite a los clientes **personalizar sus platos** bajo estrictos estándares gastronómicos, garantizando una experiencia consistente y de alta calidad.

Además, la plataforma permite optimizar procesos internos como:

- 📦 **Control de inventario**
- 🧾 **Facturación**
- 👨‍🍳 **Flujo de preparación en cocina**
- 🍕 **Personalización de platos**
- 📊 **Gestión de pedidos**

### 🎯 Objetivo

Desarrollar una solución de gestión para BellaCiao que permita optimizar
el manejo de pedidos, inventario, facturación y preparación en cocina,
manteniendo una experiencia de usuario sencilla y eficiente.
## Diagramas de Arquitectura

---

### 1. Diagrama de Contexto (C4 Model - Nivel 1)

> **Propósito:** Delimita el alcance del **Sistema BellaCiao**, mostrando a los usuarios/actores (roles del restaurante) que interactúan directamente con la aplicación y los sistemas externos de integración.

![Diagrama de Contexto](docs/images/diagrama_contexto.svg)

<details>
<summary><b>Ver código PlantUML (.puml)</b></summary>

```plantuml
@startuml Diagrama_Contexto_BellaCiao
!theme plain
skinparam backgroundColor #FFFFFF
skinparam defaultFontName Arial
skinparam roundCorner 8

title DIAGRAMA DE CONTEXTO (C4 NIVEL 1) - SISTEMA BELLACIAO

actor "Administrador / Gerente" as Admin
actor "Mesero" as Mesero
actor "Chef / Cocinero" as Chef
actor "Cajero" as Cajero
actor "Comensal / Cliente" as Cliente

rectangle "Sistema Central BellaCiao\n[Software System]\nGestión de menú, pedidos, mesas y facturación" as BellaCiao #1976D2;text:white

rectangle "Pasarela de Pagos (PSE / Stripe)" as PasarelaPago
rectangle "Facturación DIAN (Fiscal)" as DianService
rectangle "Servicio Notificaciones (KDS / SMS)" as NotifService

Admin --> BellaCiao : Gestiona menú, mesas y reportes
Mesero --> BellaCiao : Consulta mesas y registra pedidos
Chef --> BellaCiao : Visualiza comandas y actualiza estados
Cajero --> BellaCiao : Cobra y cierra cuentas
Cliente --> BellaCiao : Consulta carta digital interactiva

BellaCiao --> PasarelaPago : Autorización y cobro electrónico
BellaCiao --> DianService : Transmisión de factura electrónica
BellaCiao --> NotifService : Despacha alertas a cocina y comensales
@enduml
```

Archivo fuente: [diagrama_contexto.puml](docs/diagrams/diagrama_contexto.puml)
</details>

---

### 👥 Matriz de Roles y Funcionalidades (Permisos del Restaurante)

> Archivo descargable para Excel: [roles_permisos_bellaciao.csv](docs/roles_permisos_bellaciao.csv)

| Rol | ¿Qué funcionalidades PUEDE hacer? | ¿Qué funcionalidades NO PUEDE hacer? |
| :--- | :--- | :--- |
| **Administrador / Gerente** | • Crear, modificar y eliminar platos de la carta.<br>• Activar/desactivar disponibilidad de platos (`disponible: true/false`).<br>• Fijar precios, categorías y recetas.<br>• Crear y configurar mesas del salón.<br>• Consultar reportes consolidados de ventas y auditoría.<br>• Administrar usuarios y roles. | • Modificar o anular comandas ya cobradas y facturadas ante la DIAN.<br>• Eliminar pistas de auditoría fiscal. |
| **Mesero** | • Consultar carta activa y disponibilidad en tiempo real.<br>• Ver estado de ocupación de las mesas asignadas.<br>• Crear nuevos pedidos asociados a una mesa (`POST /api/v1/pedidos`).<br>• Adicionar ítems/platos a pedidos abiertos.<br>• Solicitar precuenta para el cliente. | • Crear, editar o eliminar platos de la carta.<br>• Modificar los precios de los platos.<br>• Marcar platos como listos en cocina.<br>• Cobrar o emitir facturas fiscales.<br>• Cancelar pedidos en preparación sin autorización. |
| **Chef / Cocina** | • Visualizar comandas en pantalla KDS en tiempo real.<br>• Cambiar el estado del pedido: `RECIBIDO` &rarr; `EN_PREPARACION` &rarr; `LISTO`.<br>• Notificar a sala cuando los platos están listos para entrega.<br>• Notificar agotamiento de insumos al administrador. | • Tomar pedidos o asignar mesas a clientes.<br>• Modificar precios o registrar cobros.<br>• Cancelar o eliminar pedidos de comensales.<br>• Modificar la carta gastronómica. |
| **Cajero** | • Consultar el detalle de la cuenta de cualquier mesa con pedido abierto.<br>• Registrar medios de pago (efectivo, datáfono, pasarela PSE).<br>• Aplicar descuentos comerciales o propinas autorizadas.<br>• Emitir factura de venta legal ante la DIAN.<br>• Cerrar pedido y marcar la mesa como libre. | • Modificar platos, recetas o precios de la carta.<br>• Cambiar el estado de preparación de los platos en cocina.<br>• Alterar consumos sin autorización del administrador. |
| **Comensal / Cliente** | • Escanear código QR de la mesa y abrir el menú digital.<br>• Consultar platos, fotos, descripciones, alérgenos y precios.<br>• Filtrar por platos disponibles en tiempo real.<br>• Consultar el estado de avance de su pedido. | • Crear, modificar o eliminar platos de la carta.<br>• Cambiar estados de preparación culinaria.<br>• Ver cuentas o pedidos de otras mesas.<br>• Modificar pedidos ya confirmados en cocina sin el mesero. |

---

### 💾 Elección de Persistencia: ¿Por qué Relacional (SQL)?

Para **BellaCiao**, tras evaluar los tres modelos de persistencia, se determinó que la arquitectura **Relacional (RDBMS con Spring Data JPA + H2 / PostgreSQL)** es la solución óptima por las siguientes razones de ingeniería:

1. **Garantías ACID y Consistencia Financiera:** En un restaurante, los cobros, facturación y comandas exigen transaccionalidad estricta. Una cuenta no puede cobrarse a medias, ni una mesa puede figurar libre y ocupada a la vez.
2. **Integridad Referencial Estricta:** El modelo posee relaciones directas y de cardinalidad estricta (`Mesa` 1:N `Pedido` 1:N `ItemPedido` N:1 `Plato`). Las restricciones de clave foránea (`Foreign Key`) y borrado en cascada evitan inconsistencias históricas o comandas huérfanas.
3. **Cumplimiento Tributario (DIAN):** La facturación fiscal exige estructuras normalizadas, auditoría contable y esquemas relacionales inmutables.

---

## Diagramas de Arquitectura (Componentes UML)

---

### 2. Diagrama de Componentes General (Arquitectura Global del Sistema)

> **Propósito:** Muestra la arquitectura global de **BellaCiao**, cómo se conectan los módulos centrales con los sistemas externos, las responsabilidades de cada componente y los límites de dominio.

![Diagrama de Componentes General](docs/images/diagrama_componentes_general.svg)

#### 📋 Especificación de Responsabilidades y Conexiones Externas
- **Límite de Dominio (`BELLACIAO PLATFORM / MIGRATION`):** Agrupa los componentes de negocio centrales desarrollados en Spring Boot:
  - `menu-catalog-processor` (`MenuService` / `PlatoService`): Administra la carta, platos, precios y disponibilidad en tiempo real.
  - `order-processing-service` (`PedidoService`): Gestiona la recepción de comandas, cálculo de subtotales, ítems de pedido y sincronización con mesas.
- **Dependencias y Sistemas Externos:**
  - **`Long Code File` (Insumos / Recetas):** Fuente de entrada para sincronización de insumos y carta vía canal SFTP.
  - **`Notification Service` & `Kafka`:** Cola de mensajería asíncrona para propagar eventos de preparación a la pantalla de cocina (KDS) y avisos a comensales.
  - **`DB: bellaciao_db`:** Base de datos relacional (PostgreSQL / H2 Server) accedida mediante Spring Data JPA e Hibernate con pool de conexiones HikariCP.
  - **`PPS (Pagos)` & `Payments API`:** Dominio externo para tokenización y cobro electrónico (PSE, tarjetas de crédito) y su posterior conciliación en `AccountsSettlement`.

<details>
<summary><b>Ver código PlantUML (.puml)</b></summary>

```plantuml
@startuml Diagrama_Componentes_General_BellaCiao
!theme plain
skinparam componentStyle uml2

package "Sistemas y Canales Externos" {
    [Long Code File\n(Recetas e Insumos)] as SFTPClient <<component>>
}

cloud "Broker de Eventos" {
    queue "Kafka\n(Event Stream)" as Kafka
}

package "Servicios Externos de Integración" {
    [Notification Service\n(Notificaciones Push / KDS)] as NotifService <<component>>
    package "PPS (Pagos)" {
        [Payments API\n(Pasarela PSE / Tarjetas)] as PaymentsAPI <<component>>
        [AccountsSettlement\n(Liquidación Cuentas)] as Settlement <<component>>
    }
}

database "DB: bellaciao_db\n(H2 Server / PostgreSQL)" as Database

package "BELLACIAO PLATFORM" #F8FAFD {
    [menu-catalog-processor\n(Gestión Carta, Platos y Precios)] as MenuComp <<component>> #A5D6A7
    [order-processing-service\n(Gestión Pedidos, Comandas y Mesas)] as OrderComp <<component>>
}

SFTPClient -() SFTP : Carga masiva
SFTP -( MenuComp

NotifService --> Kafka
MenuComp --> Kafka : Eventos Menú
OrderComp --> Kafka : Eventos Comanda

MenuComp --> Database : JPA / Hibernate
OrderComp --> Database : JPA / Hibernate

OrderComp --> PaymentsAPI : Cobro comanda
PaymentsAPI --> Settlement : Liquidación
OrderComp ..> MenuComp : Consulta Disponibilidad
@enduml
```

Archivo fuente: [diagrama_componentes_general.puml](docs/diagrams/diagrama_componentes_general.puml)
</details>

---

### 3. Diagrama de Componentes Específico (Arquitectura Interna del Servicio)

> **Propósito:** Muestra la arquitectura interna dentro del aplicativo (`Order / Plato MicroService`), detallando la organización de sus capas, la interfaz provista/requerida (*lollipop / socket*), componentes de soporte y dependencias de persistencia y pasarela.

![Diagrama de Componentes Específico](docs/images/diagrama_componentes_especifico.svg)

#### 🧩 Identificación de Componentes Internos
1. **Controladores Web (`Order / Plato Controller`):**
   - Paquete: `com.restaurante.controller` (`PlatoController`, `PedidoController`, `MenuController`).
   - Expone la interfaz REST (`/api/v1/platos`, `/api/v1/pedidos`) y gestiona los DTOs de entrada/salida.
2. **Servicio o Lógica de Negocio (`Order / Plato Service`):**
   - Paquete: `com.restaurante.service.impl` (`PlatoServiceImpl`, `PedidoServiceImpl`).
   - Implementa la interfaz `OrderService API` / `PlatoService`. Encapsula las reglas de negocio, transacciones (`@Transactional`) y cálculo de totales.
3. **Módulos de Validación y Utilidades (`Utils`):**
   - `PlatoValidator`: Encapsula la validación de unicidad de platos directamente en base de datos (`existsByNombreIgnoreCase`).
   - `EntityMapper` & `DTOMapper`: Desacoplan el modelo de dominio puro (`Plato`, `Pedido`) de las entidades de persistencia JPA (`PlatoEntity`, `PedidoEntity`).
4. **Repositorio de Acceso a Datos (`Order / Plato Repository`):**
   - Paquete: `com.restaurante.persistence.repository` (`PlatoRepository`, `PedidoRepository`).
   - Spring Data JPA, consultas JPQL (`@Query`) y `@EntityGraph` para resolver eficientemente el problema de carga 1+N.
5. **Adaptador de Integración Externa (`Payment Client`):**
   - Conector interno que traduce y desacopla las peticiones del servicio de negocio hacia la API externa `PSE Client`.
6. **Almacenamiento Persistente (`DB: bellaciao_db`):**
   - Motor relacional H2 Server (puerto `9092`) / PostgreSQL con las tablas `plato`, `pedido`, `item_pedido`, `mesa`.

<details>
<summary><b>Ver código PlantUML (.puml)</b></summary>

```plantuml
@startuml Diagrama_Componentes_Especifico_BellaCiao
!theme plain
skinparam componentStyle uml2

node "Order / Plato MicroService (com.restaurante)" as AppService #FDFDFD {

    [OrderController / PlatoController\n(Capa Web / REST Endpoints)] as Controller <<component>>
    
    interface "OrderService API" as IService
    interface "OrderRepository API" as IRepo
    interface "PaymentClient API" as IPaymentClient

    [OrderServiceImpl / PlatoServiceImpl\n(Lógica de Negocio)] as ServiceImpl <<component>>
    [Utils & Helpers\n(PlatoValidator / EntityMappers)] as Utils <<component>>
    [OrderRepository / PlatoRepository\n(Spring Data JPA & JPQL)] as Repo <<component>>
    [Payment Client\n(Adaptador de Pasarela)] as PaymentClient <<component>>

    Controller -( IService : invoca lógica
    IService - ServiceImpl : implementa
    
    ServiceImpl ..> Utils : validaciones y mappers
    
    ServiceImpl -( IRepo : operaciones CRUD
    IRepo - Repo : implementa
    
    ServiceImpl -( IPaymentClient : cobro de comanda
    IPaymentClient - PaymentClient : implementa
}

interface "PSE Gateway Interface" as IPSE
[PSE Client / Pasarela Externa] as PSEClient <<component>>
IPSE - PSEClient

PaymentClient -( IPSE : petición HTTPS

database "DB: bellaciao_db\n(H2 Server / PostgreSQL)" as DB {
    [Tablas Relacionales:\nplatos, mesas, pedidos, items_pedido]
}

Repo --> DB : Spring Data JPA / HikariCP
@enduml
```

Archivo fuente: [diagrama_componentes_especifico.puml](docs/diagrams/diagrama_componentes_especifico.puml)
</details>

---

### Autor

**Oscar Lasso**  
*Desarrollo y Operación de Software — DOSW 01*