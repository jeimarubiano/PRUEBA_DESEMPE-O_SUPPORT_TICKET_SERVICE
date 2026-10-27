# HelpFlow - Sistema Integral de Soporte TI

Proyecto de prueba de desempeño del Módulo 6. Implementa un sistema de gestión de tickets de soporte basado en arquitectura hexagonal, seguridad JWT, JPA, pruebas, observabilidad y dos microservicios.

## Servicios

- `support-ticket-service`: API principal para usuarios, técnicos, tickets, asignaciones e historial.
- `technician-dispatch-service`: servicio liviano que recomienda un técnico de forma determinista.

## Arquitectura

```mermaid
flowchart LR
  Client[Cliente / Postman] --> API[support-ticket-service :8080]
  API --> DB[(PostgreSQL)]
  API --> Dispatch[technician-dispatch-service :8081]
  API --> Actuator[Actuator / métricas]
```

El servicio central separa adaptadores REST y JPA de los casos de uso. Las operaciones de asignación e historial se ejecutan en una transacción.

## Seguridad y roles

`POST /auth/register` registra un usuario activo. `POST /auth/login` devuelve un JWT Bearer. Los endpoints de tickets requieren el encabezado `Authorization: Bearer <token>`.

- `ROLE_USUARIO`: crea y consulta sus propios tickets.
- `ROLE_TECNICO`: gestiona tickets asignados.
- `ROLE_ADMIN`: reasigna y configura reglas operativas.

Para pruebas locales se carga un técnico activo por categoría. Si necesitas habilitar la cuenta administradora `admin@helpflow.com`, define una contraseña propia antes de iniciar el servicio: `BOOTSTRAP_ADMIN_PASSWORD=tu_clave_segura`. Esa clave nunca se guarda en el repositorio.

## Endpoints principales

| Método | Ruta | Descripción |
| --- | --- | --- |
| POST | `/auth/register` | Registro de usuario |
| POST | `/auth/login` | Token JWT |
| POST | `/tickets` | Crear ticket y calcular SLA |
| GET | `/tickets` | Listar tickets del solicitante |
| PATCH | `/tickets/{id}/status` | Cambio de estado con observación |
| POST | `/tickets/{id}/assign` | Asignación automática (admin) |
| POST | `/technician-dispatch` | Sugerencia determinista de técnico |

## SLA

| Prioridad | Límite |
| --- | --- |
| BAJA | 72 horas |
| MEDIA | 24 horas |
| ALTA | 8 horas |
| CRITICA | 2 horas |

## Ciclo de vida

```mermaid
stateDiagram-v2
  [*] --> ABIERTO
  ABIERTO --> ASIGNADO
  ASIGNADO --> EN_PROGRESO
  EN_PROGRESO --> RESUELTO
  RESUELTO --> CERRADO
  ABIERTO --> CANCELADO
  ASIGNADO --> CANCELADO
```

## Ejecución prevista

```bash
docker compose up --build
```

Una vez iniciados los servicios:

- API de tickets: `http://localhost:8080`
- API de despacho: `http://localhost:8081`
- Actuator: `http://localhost:8080/actuator/health`
- Swagger UI: `http://localhost:8080/swagger-ui.html`

