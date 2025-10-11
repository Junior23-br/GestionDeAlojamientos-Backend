# Rincon del Viajero- Backend
> 🏨 Sistema de gestión de alojamientos y reservas desarrollado con Spring Boot


**Rincon del Viajero** es una aplicación de gestion de alojamientos y reservas, diseñado con el fin de automatizar y optimizar procesos de hospedaje, mejorando la experiencia de usuarios y anfitriones.

## 📌 Descripción
**Rincon del Viajero** -Backend es el backend encargado de la lógica de negocio de una plataforma de gestión de alojamientos. Está pensado para facilitar la administración de reservas, gestión de huéspedes, generación de documentación (PDFs de creación de cuenta, eliminación de cuenta y de reservas realizadas) y roles de usuario (huesped, anfitrion).
El proyecto sigue buenas prácticas de ingeniería: arquitectura en capas, separación clara de responsabilidades, con el fin de facilitar las pruebas unitarias. Cuenta con el uso de DTOs y mapeo con MapStruct, validación con Jakarta Validation y controles de seguridad con Spring Security + JWT.

---

## ⚙️ Características del Sistema:
- Gestión de huéspedes, reservas.
- Generación de documentos PDF para comprobantes y comunicaciones.
- Autenticación y autorización con Spring Security (JWT).
- Consultas en línea de servicios ofrecidos y movimientos por cliente autenticado

---

## 🔐 Seguridad
El sistema utiliza:

- Spring Security + JWT para autenticación.
- Filtros personalizados para validación de tokens.
- Roles: HUESPED, ANFITRION, ADMIN.

---

## 🧩 Patrones de Diseño Utilizados

### 1. 🧱 Arquitectura General: **Arquitectura en Capas** (Layered Architecture)

**Descripción:**  
Este patrón organiza el sistema en capas separadas: **Presentación**, **Lógica de Negocio**, **Acceso a Datos** y **Modelo de Dominio**.  
Cada capa tiene una responsabilidad clara y se comunica únicamente con la capa inmediata inferior o superior.

**Ventajas:**
- Separación de responsabilidades
- Mayor facilidad de prueba y mantenimiento
- Mejor escalabilidad del proyecto

---

## 🛠 Tecnologías y dependencias relevantes

- Java 21
- Spring Boot
- Spring Security + JWT
- Spring Data JPA (Hibernate)
- MapStruct (mappers)
- iText / OpenPDF / iText7 (generación PDF)
- MariaDB driver
- Lombok
- OpenAPI (Swagger)

---
## 🧩 Estructura de carpetas (resumen)

Estructura de paquetes base: `com.gestion.alojamientos`

- `controller` — Endpoints REST y controladores MVC
- `service` / `service.impl` — Lógica de negocio
- `repository` — Interfaces JPA
- `dto` — Objetos de transferencia
- `entity` / `model` — Entidades JPA
- `mapper` — MapStruct mappers
- `config` — Configuración de seguridad, JWT, web, etc.
- `commons` — Helpers

---

## 🧪 Testing 

Pruebas unitarias y de aceptación con:
-  JUnit + Mockito
-  Postman

---

### 2. 🧠 Backend Spring Boot: **Stereotype-based Component Model**

**Descripción:**  
Spring aplica internamente un patrón de organización de componentes usando anotaciones como `@Component`, `@Service`, `@Repository` y `@Controller`.  
Estas anotaciones permiten una gestión automatizada de dependencias y separación de responsabilidades.

**Ventajas:**
- Facilidad para aplicar inyección de dependencias
- Identificación clara del rol de cada clase

---

## 🙌 Agradecimientos

Queremos expresar nuestro profundo agradecimiento a los desarrolladores, testers y visionarios que contribuyeron a la creación de **Rincon del Viajero**. Este sistema representa la unión de buenas prácticas, el esfuero y sacrificio de cada uno de los integrantes fueron clave para alcanzar el éxito. Gracias y gracias a Santiago Sicarony, Antonio Betancourth y David Batero.
También agradecemos a las comunidades de **Spring**, **Oracle**, **MapStruct** y **Cloudinary**,  cuyos aportes han sido fundamentales para el desarrollo de esta plataforma moderna, robusta y funcional.


## 🪪 Licencia

Este proyecto está bajo la licencia MIT.
Consulta el archivo `LICENSE` para más información.





