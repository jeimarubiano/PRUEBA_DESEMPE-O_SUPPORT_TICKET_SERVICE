# Guía de verificación y evidencias

Esta guía permite reproducir la demostración sin depender de datos preexistentes. No incluye contraseñas personales ni tokens.

## 1. Levantar la solución

Desde la raíz del repositorio:

```bash
docker compose up --build
```

La ejecución correcta muestra que Flyway aplicó las migraciones y que inició `SupportTicketApplication`. Mantén esta terminal abierta mientras pruebas la aplicación.

| Evidencia | URL esperada |
| --- | --- |
| Portal de usuario | `http://localhost:8080/` |
| Swagger / OpenAPI | `http://localhost:8080/swagger-ui.html` |
| Salud del servicio | `http://localhost:8080/actuator/health` |
| Información | `http://localhost:8080/actuator/info` |
| Métricas | `http://localhost:8080/actuator/metrics` |
| Métricas Prometheus | `http://localhost:8080/actuator/prometheus` |

## 2. Demostración desde el portal

1. Abre el portal y crea un usuario de prueba con correo no usado previamente.
2. Inicia sesión con el mismo correo y contraseña.
3. Crea un ticket, por ejemplo de categoría `RED` y prioridad `ALTA`.
4. Comprueba que aparece en **Mis tickets** como `ASIGNADO`, con una fecha de SLA. La asignación se realiza automáticamente mediante el microservicio de despacho.

Esta es la evidencia funcional más fácil de mostrar: el portal consume la API protegida con JWT. Para una nueva demostración, usa otro correo o reinicia los contenedores y la base de datos según necesites.

## 3. Demostración desde Swagger

1. Ejecuta `POST /auth/register` y registra un usuario nuevo.
2. Ejecuta `POST /auth/login`; copia el valor `accessToken` de la respuesta.
3. Pulsa **Authorize** y pega `Bearer ` seguido del token.
4. Usa `POST /tickets` y después `GET /tickets`.

Ejemplo de ticket:

```json
{
  "title": "Sin conexión a la red",
  "description": "El equipo no tiene acceso a Internet desde esta mañana.",
  "category": "RED",
  "priority": "ALTA",
  "site": "Bogota",
  "affectedAsset": "Portatil-001"
}
```

## 4. Pruebas automatizadas

```bash
mvn test
```

Incluye pruebas de carga del contexto, autenticación con MockMvc, comportamiento determinista de despacho y una prueba de PostgreSQL con Testcontainers. Esta última se omite de forma segura en equipos donde Docker no esté disponible para Testcontainers.

## 5. Capturas recomendadas para la entrega

Toma estas capturas en tu equipo y añádelas a la carpeta `docs/evidence/` si el docente solicita archivos de evidencia:

1. Terminal con `docker compose up --build` y los tres servicios iniciados.
2. Portal mostrando un ticket creado.
3. Swagger con una respuesta exitosa de `POST /auth/login` o `POST /tickets`.
4. Navegador mostrando `/actuator/health` con estado `UP`.
5. Terminal con el resumen `BUILD SUCCESS` de `mvn test`.

No compartas capturas que muestren contraseñas, JWT ni información personal.
