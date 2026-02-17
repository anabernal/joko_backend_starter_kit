# joko-backend-starter-kit
Este es un modulo que contiene lo que normalmente utilizamos en un proyecto 
backend:
* Integracion con joko-security 1.0.0
* Integracion con swagger 2 (/swagger-ui.html)
* RestController
* Servicios basicos:
    * Users
    * Countries
* Repository + Entity (JPA)
* Integracion con liquibase

La intencion del proyecto es que sirva como un template para crear nuevos 
proyectos.

OBS. Las instrucciones estan orientadas a sistemas UNIX, no obstante, pueden ser adaptadas para otros sistemas.

# Como utilizar el proyecto
## Clonar el proyecto de
https://github.com/jokoframework/joko_backend_starter_kit

## Eliminar la dependencia al repo actual
```
rm -rf .git
git init
``` 

## Ejecutarlo
Para una guia de como correr el proyecto visite [RUN.md](RUN.md)

## Personalizarlo
* Buscar los FIXME y empezar a personalizar
* Eliminar todo lo que quiera

## PUSH al nuevo repo
```
git remote add origin <nuevoURL>
git push origin master
```
# Módulo de Auditoría

El módulo de Audit Log sobre el proyecto base [joko_backend_starter_kit](https://github.com/jokoframework/joko_backend_starter_kit).

## Cómo ejecutar el proyecto

### Requisitos
- Java 11
- Maven 3.6+

### Ejecución
```bash
git clone https://github.com/TU_USUARIO/joko_backend_starter_kit.git
cd joko_backend_starter_kit
mvn spring-boot:run
```

La app levanta en `http://localhost:8080` usando H2 como base de datos embebida. La tabla `audit.audit_event` se crea automáticamente al iniciar.

Credenciales por defecto: `admin / 123456`

Swagger: `http://localhost:8080/swagger-ui/`

Consola H2: `http://localhost:8080/h2-console` (JDBC URL: `jdbc:h2:~/.joko-DEMO-DB`, user: `sa`, pass: `123456`)

## Ejemplos de uso

### Autenticación (dos pasos)
```bash
# 1. Obtener refresh token
curl -X POST http://localhost:8080/api/login \
  -H "Content-Type: application/json" \
  -d '{"username": "admin", "password": "123456"}'

# 2. Obtener access token (usar el "secret" del paso anterior)
curl -X POST http://localhost:8080/api/token/user-access \
  -H "Content-Type: application/json" \
  -H "X-JOKO-AUTH: REFRESH_TOKEN"
```

### Crear usuario
```bash
curl -X POST http://localhost:8080/api/secure/users \
  -H "Content-Type: application/json" \
  -H "X-JOKO-AUTH: ACCESS_TOKEN" \
  -H "X-Correlation-Id: test-001" \
  -d '{"username": "nuevo_usuario", "password": "pass123", "profile": "END_USER"}'
```

### Actualizar usuario
```bash
curl -X PUT http://localhost:8080/api/secure/users/2 \
  -H "Content-Type: application/json" \
  -H "X-JOKO-AUTH: ACCESS_TOKEN" \
  -d '{"password": "nuevo_pass", "profile": "ADMIN"}'
```

### Eliminar usuario
```bash
curl -X DELETE http://localhost:8080/api/secure/users/2 \
  -H "X-JOKO-AUTH: ACCESS_TOKEN"
```

### Consultar eventos de auditoría
```bash
# Todos (paginados)
curl -X GET "http://localhost:8080/api/secure/audit-events?page=0&size=10" \
  -H "X-JOKO-AUTH: ACCESS_TOKEN"

# Por acción
curl -X GET "http://localhost:8080/api/secure/audit-events?action=USER_CREATED" \
  -H "X-JOKO-AUTH: ACCESS_TOKEN"

# Por rango de fechas
curl -X GET "http://localhost:8080/api/secure/audit-events?from=2026-01-01T00:00:00Z&to=2026-12-31T23:59:59Z" \
  -H "X-JOKO-AUTH: ACCESS_TOKEN"
```

Los tres endpoints de usuarios (POST, PUT, DELETE) generan audit events automáticamente. El cliente no envía nada relacionado a auditoría.

## Supuestos

- Se usa **hard delete** para usuarios porque el proyecto original no tiene soft delete. Los datos del usuario eliminado quedan en el metadata del audit event.
- El proyecto original solo tenía GET de usuarios. Se agregaron POST, PUT y DELETE para cumplir con los requerimientos de auditoría.
- Se usa el **username** como actor del evento (no el ID numérico) porque es más legible.
- El campo metadata es un TEXT con JSON simple, para mantener compatibilidad entre H2 y PostgreSQL.

## Decisiones técnicas

- **Auditoría en la capa Manager:** El proyecto sigue el patrón Controller → Manager → Service. El Manager orquesta la lógica de negocio, así que es donde corresponde generar los eventos de auditoría. No en el Controller (solo recibe/responde) ni en el Service (solo persiste datos).
- **Esquema `audit` separado:** Siguiendo el patrón del proyecto que ya tiene `basic` y `profile`.
- **JPA Specification para filtros:** Permite combinar filtros dinámicamente sin crear un método por cada combinación posible.
- **CorrelationIdFilter con OncePerRequestFilter:** Se ejecuta una vez por request, con prioridad máxima. Limpia el ThreadLocal en el finally para evitar memory leaks.
- **Enums en vez de strings:** Para action y resourceType. Evita errores de tipeo y da autocompletado en el IDE.

## Qué mejoraría con más tiempo

- Tests unitarios y de integración
- Auditoría asíncrona (con @Async o ApplicationEvent) para no sumar latencia al request
- Soft delete de usuarios
- Más acciones auditables (login, logout)
- Mejor manejo de errores cuando se envían valores inválidos en los filtros

