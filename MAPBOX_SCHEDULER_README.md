# Integración Mapbox y Scheduler de Notificaciones

Este documento describe la implementación de la integración con Mapbox y el sistema de notificaciones automáticas de check-in.

## 🗺️ Paso 1 - Integración de Mapbox

### Configuración

1. **Variable de entorno**: Configura la variable de entorno `MAPBOX_API_KEY` con tu token de Mapbox:
   ```bash
   export MAPBOX_API_KEY="tu_token_de_mapbox_aqui"
   ```

2. **Archivo de configuración**: El token se lee desde `application.properties`:
   ```properties
   #Config Mapbox
   mapbox.api.key=${MAPBOX_API_KEY}
   ```

### Componentes Implementados

#### 1. MapboxConfig
- **Ubicación**: `src/main/java/com/gestion/alojamientos/config/MapboxConfig.java`
- **Función**: Configuración segura del token de Mapbox
- **Características**:
  - Obtiene el token desde `application.yml` mediante `@Value("${mapbox.api.key}")`
  - Expone un `@Bean` con `RestTemplate` para llamadas HTTP
  - Oculta completamente el token sin exponerlo en código

#### 2. MapboxService
- **Ubicación**: `src/main/java/com/gestion/alojamientos/service/MapboxService.java`
- **Función**: Servicio para integración con Mapbox
- **Métodos principales**:
  - `validateCoordinates()`: Valida coordenadas geográficas
  - `calculateDistance()`: Calcula distancia entre dos puntos usando fórmula de Haversine
  - `isServiceAvailable()`: Verifica disponibilidad del servicio

#### 3. LocationService
- **Ubicación**: `src/main/java/com/gestion/alojamientos/service/LocationService.java`
- **Función**: Gestión de ubicaciones y coordenadas de alojamientos
- **Métodos principales**:
  - `updateAccommodationCoordinates()`: Actualiza coordenadas existentes
  - `registerAccommodationCoordinates()`: Registra nuevas coordenadas
  - `getAccommodationCoordinates()`: Obtiene coordenadas de un alojamiento
  - `findNearbyAccommodations()`: Busca alojamientos cercanos
  - `calculateDistanceBetweenAccommodations()`: Calcula distancia entre alojamientos

#### 4. LocationController
- **Ubicación**: `src/main/java/com/gestion/alojamientos/controller/LocationController.java`
- **Función**: API REST para gestión de ubicaciones
- **Endpoints**:
  - `PUT /api/locations/accommodation/{id}/coordinates`: Actualizar coordenadas
  - `POST /api/locations/accommodation/{id}/coordinates`: Registrar coordenadas
  - `GET /api/locations/accommodation/{id}/coordinates`: Obtener coordenadas
  - `GET /api/locations/nearby`: Buscar alojamientos cercanos
  - `GET /api/locations/accommodation/{id}/has-coordinates`: Verificar coordenadas
  - `GET /api/locations/distance`: Calcular distancia entre alojamientos

### Uso de la API

#### Registrar coordenadas de un alojamiento:
```bash
curl -X POST "http://localhost:8080/api/locations/accommodation/1/coordinates?latitude=4.6097&longitude=-74.0817"
```

#### Buscar alojamientos cercanos:
```bash
curl -X GET "http://localhost:8080/api/locations/nearby?latitude=4.6097&longitude=-74.0817&radiusKm=10"
```

## 📧 Paso 2 - Scheduler de Notificaciones de Check-in

### Configuración

El scheduler está habilitado automáticamente con la anotación `@EnableScheduling` en la clase principal.

### Componentes Implementados

#### 1. BookingNotificationScheduler
- **Ubicación**: `src/main/java/com/gestion/alojamientos/service/BookingNotificationScheduler.java`
- **Función**: Servicio programado para notificaciones automáticas
- **Características**:
  - Ejecuta cada hora (`@Scheduled(fixedRate = 3600000)`)
  - Busca reservas con check-in en las próximas 24 horas
  - Envía correos tanto al huésped como al host
  - Maneja excepciones y registra logs informativos

#### 2. SchedulerController
- **Ubicación**: `src/main/java/com/gestion/alojamientos/controller/SchedulerController.java`
- **Función**: API REST para controlar el scheduler
- **Endpoints**:
  - `GET /api/scheduler/status`: Estado del scheduler
  - `POST /api/scheduler/send-reminders`: Enviar recordatorios para fecha específica
  - `POST /api/scheduler/send-reminders/tomorrow`: Enviar recordatorios para mañana

### Funcionamiento

1. **Ejecución automática**: Cada hora, el scheduler busca reservas confirmadas cuyo check-in ocurra en las próximas 24 horas
2. **Envío de correos**: Para cada reserva encontrada, envía correos de recordatorio tanto al huésped como al host
3. **Logging**: Registra información detallada sobre el proceso y cualquier error
4. **Manejo de errores**: Continúa procesando otras reservas aunque una falle

### Uso de la API

#### Verificar estado del scheduler:
```bash
curl -X GET "http://localhost:8080/api/scheduler/status"
```

#### Enviar recordatorios para una fecha específica:
```bash
curl -X POST "http://localhost:8080/api/scheduler/send-reminders?targetDate=2024-01-15"
```

#### Enviar recordatorios para mañana:
```bash
curl -X POST "http://localhost:8080/api/scheduler/send-reminders/tomorrow"
```

## 🔧 Configuración Adicional

### Variables de Entorno Requeridas

```bash
# Token de Mapbox (obligatorio)
MAPBOX_API_KEY="pk.eyJ1IjoieW91cnVzZXJuYW1lIiwiYSI6ImNsc..."

# Configuración de base de datos (ya existente)
DB_URL="jdbc:mariadb://localhost:3300/dev"
DB_USERNAME="root"
DB_PASSWORD="admin123"

# Configuración de correo (ya existente)
MAIL_HOST="smtp.gmail.com"
MAIL_USERNAME="tu_email@gmail.com"
MAIL_PASSWORD="tu_password_de_aplicacion"
```

### Dependencias Maven

Las siguientes dependencias ya están incluidas en el proyecto:
- Spring Boot Starter Web
- Spring Boot Starter Data JPA
- Spring Boot Starter Mail
- Spring Boot Starter Validation
- Lombok
- MapStruct
- OpenAPI/Swagger

## 🚀 Despliegue

1. **Configurar variables de entorno** en tu servidor
2. **Compilar el proyecto**: `mvn clean package`
3. **Ejecutar**: `java -jar target/backend-0.0.1-SNAPSHOT.jar`

## 📝 Notas Importantes

- **Seguridad**: El token de Mapbox se mantiene completamente oculto en variables de entorno
- **Rendimiento**: El scheduler ejecuta consultas optimizadas con índices en fechas
- **Escalabilidad**: El sistema está diseñado para manejar múltiples reservas simultáneamente
- **Monitoreo**: Todos los procesos incluyen logging detallado para facilitar el debugging
- **Compatibilidad**: Compatible con Spring Boot 3+ y Java 17+

## 🔍 Testing

Para probar la funcionalidad:

1. **Mapbox**: Usa los endpoints del `LocationController` para registrar y consultar coordenadas
2. **Scheduler**: Usa los endpoints del `SchedulerController` para enviar recordatorios manuales
3. **Logs**: Revisa los logs de la aplicación para ver el funcionamiento del scheduler automático
