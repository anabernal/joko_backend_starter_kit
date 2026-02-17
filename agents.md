# Agents.md - Documentación del uso de IA

## Asistente utilizado

- **Herramienta:** Amazon Q - Claude Sonnet 4.5
- **Modalidad:** Conversación interactiva paso a paso, con explicaciones y generación de código

## Tareas para las que se usó IA

### 1. Análisis del proyecto base
Se usó para explorar la estructura del repositorio, entender convenciones (Controller → Manager → Service → Repository), configuración de seguridad y mecanismo de migraciones Liquibase. 
### 2. Generación del schema y migración Liquibase
Creación de `db-changelog-audit.xml` con la tabla, secuencia e índices.
### 5. Diseño del endpoint de consulta
GET /audit-events con filtros dinámicos usando JPA Specification, paginación y ordenamiento.
### 6. Documentación
README con instrucciones, ejemplos y decisiones técnicas. Este archivo Agents.md.

## Prompts / instrucciones utilizadas
- Se compartió el PDF del desafío técnico completo como contexto inicial posterior a la creacion del codigo de Java (Entidad, repository, service, controller, DTOs, enums, filtro de correlación y modificaciones al flujo de usuarios).
- Cada paso se verificó manualmente antes de avanzar al siguiente.

## Partes revisadas y ajustadas manualmente
- **Generación del código Java**: Entidad, repository, service, controller, DTOs, enums, filtro de correlación y modificaciones al flujo de usuarios.
- **Diseño de la estructura del módulo de auditoría**:Planificación de los 6 requerimientos y definición de qué archivos crear/modificar.
- **Verificación de compilación:** Cada cambio se compiló y testeó localmente antes de avanzar.
- **Autenticación:** Se descubrió que el proyecto usa login en dos pasos (refresh → access token) con header `X-JOKO-AUTH`, no `X-Authorization: Bearer`. Se ajustaron los ejemplos.
- **Consola H2:** Se habilitó `spring.h2.console.enabled=true` que no estaba en la configuración original.
- **Pruebas funcionales:** Se verificó en la consola H2 que los audit events se generaban correctamente, y en Swagger que los endpoints respondían.
- **Constructor de UserException:** Se verificó que usa dos parámetros (errorCode, message).
- **UserResponseDTO:** Se usó `setUserMessage()` en lugar de `setMessage()` por la cadena de herencia del proyecto.

## Decisiones NO delegadas a la IA

| Decisión | Razón |
|----------|-------|
| Ubicar auditoría en capa Manager | Requería entender la arquitectura específica del proyecto. |
| Hard delete vs soft delete | Decisión de negocio basada en el contexto del proyecto base. |
| Esquema audit separado | Decisión arquitectónica basada en el patrón existente de schemas. |

## Partes descartadas
- Se consideró usar `@EntityListeners` de JPA pero se descartó porque no permite capturar el correlation ID limpiamente.
- Se consideró Spring AOP (`@Aspect`) pero dificulta el control granular de la metadata por acción.
