# HelpFlow - Sistema Integral de Soporte TI

Proyecto de prueba de desempeño del Módulo 6. Implementa un sistema de gestión de tickets de soporte basado en arquitectura hexagonal, seguridad JWT, JPA, pruebas, observabilidad y dos microservicios.

## Servicios

- `support-ticket-service`: API principal para usuarios, técnicos, tickets, asignaciones e historial.
- `technician-dispatch-service`: servicio liviano que recomienda un técnico de forma determinista.

## Estado del desarrollo

La estructura multi-módulo y el servicio de despacho se implementan primero. El servicio central incorporará dominio hexagonal, persistencia PostgreSQL/Flyway, JWT, reglas de asignación, pruebas y observabilidad.

## Ejecución prevista

```bash
docker compose up --build
```

Una vez iniciados los servicios:

- API de tickets: `http://localhost:8080`
- API de despacho: `http://localhost:8081`
- Actuator: `http://localhost:8080/actuator/health`

