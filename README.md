# 👻 Casas Embrujadas — Backend (API REST)

API REST de la tienda **Casas Embrujadas**, desarrollada con **Spring Boot** para la asignatura **DSY1104 · Desarrollo FullStack II** (Duoc UC), Evaluación Parcial 3.

Frontend (React): https://github.com/Dangelo-Rojas/casas-embrujadas-frontend

## 🛠️ Tecnologías

| Herramienta | Uso |
|---|---|
| Java 21+ / Spring Boot 4 | Framework del backend |
| Spring Web MVC | Controladores REST |
| Spring Data JPA + Hibernate | Entidades y repositorios (ORM) |
| MySQL 8 | Base de datos |
| Spring Security + JJWT | Autenticación con tokens JWT y roles |
| BCrypt | Hash de contraseñas |
| springdoc-openapi (Swagger) | Documentación interactiva de la API |
| Bean Validation | Validación de los datos de entrada |

## 🚀 Cómo ejecutarlo

1. Tener **MySQL** corriendo en `localhost:3306` con usuario `root` sin contraseña (por ejemplo con Laragon).
   La base `casas_embrujadas` se crea sola.
2. Ejecutar:
   ```bash
   ./mvnw spring-boot:run        # Linux / Mac
   mvnw.cmd spring-boot:run      # Windows (o doble clic en correr-backend.bat)
   ```
3. La API queda en **http://localhost:8080** y Swagger en **http://localhost:8080/swagger-ui.html**.

La primera vez se cargan automáticamente las propiedades, blogs, regiones, comunas y los usuarios de prueba.

### Usuarios de prueba

| Rol | Correo | Contraseña |
|---|---|---|
| ADMIN | `admin@duoc.cl` | `admin123` |
| CLIENTE | `cliente@gmail.com` | `cliente123` |

### Variables de entorno (opcionales)

| Variable | Para qué |
|---|---|
| `JWT_SECRET` | Clave (Base64, 256 bits o más) con que se firman los tokens. En producción **siempre** se debe definir; el valor de `application.yml` es solo de desarrollo |

## 🧱 Arquitectura en capas

```
Petición HTTP ─▶ Controller ─▶ Service ─▶ Repository ─▶ MySQL
                 (recibe)      (reglas)    (consultas)
```

```
src/main/java/cl/duoc/casasembrujadas/
├── model/        Entidades JPA (Propiedad, Usuario, Pedido, DetallePedido, Blog, MensajeContacto, Region, Comuna)
├── repository/   Interfaces JpaRepository
├── service/      Lógica de negocio
├── controller/   Endpoints REST
├── dto/          Datos de entrada/salida (login, registro, pedido)
├── security/     JWT (JwtService, JwtAuthFilter) y SecurityConfig
├── exception/    Manejo global de errores (@RestControllerAdvice)
└── config/       DataLoader (datos iniciales) y OpenApiConfig (Swagger)
```

## 🔐 Seguridad

- **Login** (`POST /api/auth/login`) devuelve un **token de acceso** (30 min) y un **refresh token** (7 días).
- Cada petición protegida envía `Authorization: Bearer <token>`. El filtro `JwtAuthFilter` verifica la **firma** y la **expiración**.
- **Renovación**: `POST /api/auth/refresh` entrega tokens nuevos sin pedir la contraseña.
- **Roles**: `CLIENTE` y `ADMIN`. Las reglas están en `SecurityConfig` y en cada endpoint con `@PreAuthorize("hasRole('ADMIN')")`.
- Respuestas: **401** sin sesión o token inválido/expirado · **403** con sesión pero sin el rol necesario.

## 📚 Endpoints

| Método | Ruta | Acceso | Descripción |
|---|---|---|---|
| POST | `/api/auth/registro` | Público | Crear cuenta (rol CLIENTE) |
| POST | `/api/auth/login` | Público | Iniciar sesión |
| POST | `/api/auth/refresh` | Público | Renovar token |
| GET | `/api/auth/me` | Sesión | Usuario actual |
| GET | `/api/propiedades` | Público | Catálogo (activas) |
| GET | `/api/propiedades/destacadas` | Público | Destacadas del Home |
| GET | `/api/propiedades/slug/{slug}` | Público | Detalle |
| GET | `/api/propiedades/todas` | ADMIN | Todas, incluso inactivas |
| GET | `/api/propiedades/{id}` | ADMIN | Buscar por id |
| POST | `/api/propiedades` | ADMIN | Crear |
| PUT | `/api/propiedades/{id}` | ADMIN | Actualizar |
| DELETE | `/api/propiedades/{id}` | ADMIN | Eliminar |
| GET | `/api/blogs` · `/api/blogs/{slug}` | Público | Casos curiosos |
| POST · PUT · DELETE | `/api/blogs` · `/api/blogs/{id}` | ADMIN | CRUD del blog |
| GET | `/api/regiones` · `/api/regiones/{codigo}/comunas` | Público | Regiones y comunas |
| POST | `/api/contacto` | Público | Enviar mensaje |
| GET · PATCH · DELETE | `/api/contacto` · `/api/contacto/{id}/leido` · `/api/contacto/{id}` | ADMIN | Gestionar mensajes |
| POST | `/api/pedidos` | Sesión | Finalizar compra (el total lo calcula el servidor) |
| GET | `/api/pedidos/mis-pedidos` | Sesión | Pedidos propios |
| GET | `/api/pedidos` | ADMIN | Todos los pedidos |
| PATCH | `/api/pedidos/{id}/estado` | ADMIN | Cambiar estado |
| GET · PATCH · DELETE | `/api/usuarios` · `/api/usuarios/{id}/rol` · `/api/usuarios/{id}` | ADMIN | Administrar usuarios |

## 👤 Autor

**Dangelo Rojas** — Duoc UC
