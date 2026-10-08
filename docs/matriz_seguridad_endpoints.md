# Matriz de Seguridad y Autorización - BellaCiao

De acuerdo al "Paso 1 - Punto de Partida" de la guía y basándonos en la matriz de roles y permisos inicial (`roles_permisos_bellaciao.csv`), esta es la tabla oficial de endpoints reales del proyecto para la configuración de `SecurityConfig`:

| ENDPOINT | MÉTODO HTTP | ¿QUIÉN PUEDE? | ROL EN SPRING |
| :--- | :--- | :--- | :--- |
| **MENÚ Y PLATOS (CARTA)** | | | |
| `/api/v1/menu/**` | `GET` | Todos (Público / Comensal) | `permitAll()` |
| `/api/v1/platos`, `/api/v1/platos/**` | `GET` | Todos (Público / Comensal) | `permitAll()` |
| `/api/v1/platos` | `POST` | Solo Administrador | `hasRole('ADMIN')` |
| `/api/v1/platos/{id}` | `PUT`, `DELETE` | Solo Administrador | `hasRole('ADMIN')` |
| `/api/v1/platos/{id}/disponibilidad` | `PATCH` | Solo Administrador | `hasRole('ADMIN')` |
| **MESAS** | | | |
| `/api/v1/mesas` | `POST` | Solo Administrador | `hasRole('ADMIN')` |
| `/api/v1/mesas`, `/api/v1/mesas/disponibles`| `GET` | Mesero, Cajero, Admin | `hasAnyRole('MESERO', 'CAJERO', 'ADMIN')` |
| `/api/v1/mesas/{id}/abrir-cuenta` | `POST` | Mesero y Admin | `hasAnyRole('MESERO', 'ADMIN')` |
| `/api/v1/mesas/{id}/cerrar-cuenta` | `POST` | Cajero y Admin | `hasAnyRole('CAJERO', 'ADMIN')` |
| **PEDIDOS (COMANDAS)** | | | |
| `/api/v1/pedidos` | `POST` | Solo Mesero | `hasRole('MESERO')` |
| `/api/v1/pedidos/mesa/{id}` | `GET` | Mesero, Cajero, Chef, Admin| `hasAnyRole('MESERO', 'CAJERO', 'CHEF', 'ADMIN')`|
| `/api/v1/pedidos/{id}/estado` | `POST` | Solo Chef (y Admin opcional) | `hasAnyRole('CHEF', 'ADMIN')` |
| **AUTENTICACIÓN** | | | |
| `/api/v1/auth/login` | `POST` | Todos (Público) | `permitAll()` |
| `/swagger-ui/**`, `/v3/api-docs/**` | `GET` | Todos (Desarrollo) | `permitAll()` |

---
*Nota: El endpoint de autenticación (`/auth/login`) será el que crearemos para intercambiar credenciales por el Token JWT.*
