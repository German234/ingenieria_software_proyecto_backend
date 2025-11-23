# Círculos de Estudio Backend

## Descripción del proyecto

Círculos de Estudio es una plataforma web pensada para gestionar grupos académicos, publicaciones internas y control de asistencia de estudiantes en actividades de apoyo académico / servicio social. Su objetivo principal es centralizar la relación con el Tutor, Alumno y Administración, y dejar trazabilidad de asistencia, comunicación y material de apoyo.

El sistema actual tiene diferentes funcionalidades, tanto funcionales como no funcionales:

### Autenticación y seguridad
Implementa inicio de sesión seguro mediante tokens JWT, manejo de sesiones y protección de endpoints, garantizando acceso seguro y controlado a los diferentes recursos del sistema según los roles de usuario.

### Gestión de alumnos, tutores y cursos
Permite administrar la información de los usuarios y sus relaciones dentro de los grupos de estudio, facilitando la organización y asignación de tutores a cursos. El sistema maneja una jerarquía clara de roles (administradores, tutores, alumnos) con diferentes niveles de permisos.

### Sistema de publicaciones
Los tutores y administradores pueden crear publicaciones con contenido académico visible para los alumnos, fomentando la comunicación y el intercambio de material. Estas publicaciones pueden incluir texto, imágenes y documentos adjuntos.

### Historial de asistencia
Ofrece una vista mensual con filtros por mes y año, permitiendo consultar y controlar la asistencia de los estudiantes a las tutorías. El sistema mantiene un registro detallado que facilita el seguimiento del progreso de cada alumno.

### Subida de imágenes y archivos
Permite adjuntar recursos multimedia y documentos a las publicaciones, enriqueciendo el contenido compartido dentro de la plataforma. El sistema gestiona el almacenamiento y acceso a estos archivos de manera organizada.

### Comentarios y comunicación
Facilita la interacción entre tutores y alumnos a través de un sistema de comentarios que permite resolver dudas, compartir ideas y mantener una comunicación fluida dentro de cada grupo de estudio.

### Gestión de materiales de apoyo
Centraliza el almacenamiento y organización de documentos de estudio, guías, y recursos educativos que pueden ser asignados a grupos específicos, facilitando el acceso al material relevante para cada curso.

## Requisitos previos

- **Java 17** o superior
- **PostgreSQL 12** o superior
- **Gradle 7.0** o superior (incluido en el proyecto)
- **Git** para clonar el repositorio

## Instalación paso a paso

1. **Clonar el repositorio**
   ```bash
   git clone https://github.com/tu-usuario/ingenieria_software_proyecto_backend.git
   cd ingenieria_software_proyecto_backend
   ```

2. **Configurar la base de datos PostgreSQL**
   - Crear una base de datos en PostgreSQL:
     ```sql
     CREATE DATABASE circulos_estudio;
     ```
   - Crear un usuario con permisos sobre la base de datos (o usar uno existente)

3. **Configurar variables de entorno**
   - Copiar el archivo de variables de entorno:
     ```bash
     cp .env.example .env
      ```
   - Editar el archivo `.env` con tus configuraciones locales

4. **Compilar el proyecto**
   ```bash
   ./gradlew build
   ```

5. **Ejecutar el proyecto**
   ```bash
   ./gradlew bootRun
   ```
   
   O si prefieres usar el archivo JAR generado:
   ```bash
   java -jar build/libs/circulosestudiobackend-0.0.1-SNAPSHOT.jar
   ```

## Ejecución

Una vez que el proyecto esté configurado correctamente, la API estará disponible en:

```
http://localhost:8080
```

### Endpoints principales

- **Autenticación**: `/api/auth/login`
- **Usuarios**: `/api/users`
- **Grupos de trabajo**: `/api/work-groups`
- **Materiales de apoyo**: `/api/support-materials`
- **Documentos**: `/api/documents`
- **Asistencia**: `/api/attendance`
- **Comentarios**: `/api/comments`

## Variables de entorno

Crea un archivo `.env` en la raíz del proyecto con las siguientes variables:

```env
# Configuración de la base de datos
DATABASE_URL=jdbc:postgresql://localhost:5432/circulos_estudio
DATABASE_USERNAME=tu_usuario_postgres
DATABASE_PASSWORD=tu_contraseña_postgres

# Configuración del servidor
PORT=8080

# Configuración de JWT
JWT_SECRET=tu_secreto_jwt_muy_seguro_aqui

# Configuración de uploads
UPLOADS_PATH=./uploads
UPLOADS_BASE_URL=http://localhost:8080/uploads
```

### Explicación de las variables de entorno

- **DATABASE_URL**: URL de conexión a la base de datos PostgreSQL
- **DATABASE_USERNAME**: Nombre de usuario para la base de datos
- **DATABASE_PASSWORD**: Contraseña para la base de datos
- **PORT**: Puerto en el que se ejecutará el servidor (por defecto: 8080)
- **JWT_SECRET**: Clave secreta para firmar los tokens JWT (debe ser una cadena segura y única)
- **UPLOADS_PATH**: Ruta local donde se guardarán los archivos subidos
- **UPLOADS_BASE_URL**: URL base para acceder a los archivos subidos

## Arquitectura del proyecto

El proyecto sigue una arquitectura en capas con los siguientes componentes principales:

- **Controllers**: Manejan las peticiones HTTP y definen los endpoints de la API
- **Services**: Contienen la lógica de negocio
- **Repositories**: Interactúan con la base de datos mediante JPA
- **Entities**: Definen el modelo de datos
- **DTOs**: Objetos de transferencia de datos para las peticiones y respuestas
- **Security**: Configuración de autenticación y autorización con JWT

## Tecnologías utilizadas

- **Spring Boot 3.5.0**: Framework principal
- **Java 17**: Lenguaje de programación
- **Spring Security**: Autenticación y autorización
- **JWT (JSON Web Tokens)**: Gestión de sesiones
- **Spring Data JPA**: Acceso a datos
- **PostgreSQL**: Base de datos relacional
- **Lombok**: Reducción de código repetitivo
- **Spring Dotenv**: Gestión de variables de entorno
