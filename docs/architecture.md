# Arquitectura de HelpFlow

## Vista de contenedores

```mermaid
flowchart LR
    U[Usuario] --> W[Portal web integrado :8080]
    U --> S[Swagger UI :8080/swagger-ui.html]
    W --> R[Adaptador REST]
    S --> R
    R --> A[Casos de uso / aplicación]
    A --> P1[Puerto de persistencia]
    A --> P2[Puerto de despacho]
    P1 --> J[Adaptador JPA]
    J --> DB[(PostgreSQL :5433)]
    P2 --> D[technician-dispatch-service :8081]
    A --> M[Micrometer / Actuator]
```

## Arquitectura hexagonal del servicio central

```mermaid
flowchart TB
    subgraph Entrada[Adaptadores de entrada]
        REST[Controladores REST]
        UI[Portal web estático]
    end
    subgraph Aplicacion[Aplicación]
        T[TicketAssignmentService]
        PORT1[TechnicianDispatchPort]
    end
    subgraph Dominio[Dominio]
        E[Ticket, Usuario, Técnico, Historial]
        R[Reglas de SLA y estados]
    end
    subgraph Salida[Adaptadores de salida]
        JPA[Repositorios JPA]
        HTTP[RestTechnicianDispatchAdapter]
    end
    REST --> T
    UI --> REST
    T --> E
    T --> PORT1
    JPA --> E
    HTTP --> PORT1
```

Las decisiones de asignación, el historial y la transacción se concentran en la capa de aplicación. Los controladores no conocen detalles de persistencia ni de la llamada HTTP al microservicio externo.

## Ciclo de vida del ticket

```mermaid
stateDiagram-v2
    [*] --> ABIERTO: usuario registra ticket
    ABIERTO --> ASIGNADO: asignación
    ASIGNADO --> EN_PROGRESO: técnico inicia atención
    EN_PROGRESO --> RESUELTO: técnico resuelve
    RESUELTO --> CERRADO: usuario/admin cierra
    ABIERTO --> CANCELADO
    ASIGNADO --> CANCELADO
```
