# Importador XML a PostgreSQL
Proyecto para la materia Desarrollo de Software de UTN - San Rafael.
Se trata de una pequeña aplicación hecha en Java que importa datos desde archivos XML y los carga en una base de datos PostgreSQL, automáticamente, creando las tablas necesarias según la estructura de los archivos.

## Descripción

- Lee archivos XML desde la carpeta `archivosXML/`.
- Crea tablas en la base de datos según la estructura de cada XML.
- Inserta los datos extraídos en las tablas correspondientes.
- Utiliza archivos de configuración para la conexión a la base de datos y definición de claves primarias.

## Requisitos

- Java 21 o superior
- Maven 3.6+
- PostgreSQL (servidor local o remoto)
- Git (opcional, para clonar el repositorio)

## Instalación

1. **Clonar el repositorio:**
   ```bash
   git clone https://github.com/litomi/DS_cargaXML.git
   cd DS_cargaXML
   ```

2. **Configura la base de datos:**
   - Crea una base de datos en PostgreSQL (por ejemplo, `sysacad_db`).
   - Crea un usuario y otórgale permisos.
   - Modifica el archivo `src/main/resources/db.properties` con los datos de tu base de datos:
     ```properties
     db.dbname=sysacad_db
     db.url=jdbc:postgresql://localhost:5432/sysacad_db
     db.usr=TU_USUARIO
     db.pass=TU_PASSWORD
     xml.directory.path=./archivosXML/
     ```

3. **Instala las dependencias y compila el proyecto:**
   ```bash
   mvn clean package
   ```

## Ejecución

1. El servidor de PostgreSQL debe estar en ejecución.
2. Ejecuta la aplicación:
   ```bash
   mvn exec:java -Dexec.mainClass="com.app.App"
   ```
   O bien, ejecuta el JAR generado:
   ```bash
   java -cp target/my-app-1.0-SNAPSHOT.jar com.app.App
   ```

## Estructura del proyecto

```
archivosXML/         # Archivos XML de entrada
src/
  main/
    java/com/app/    # Código fuente principal
    resources/       # Archivos de configuración
  test/              # (Ignorado en git) Pruebas unitarias
logs/                # Archivos de log
```

## Notas

- Los archivos de configuración de claves primarias están en `src/main/resources/pk.properties`.
- Los logs de la aplicación se guardan en la carpeta `logs/`.

- Si quieres agregar nuevos archivos XML, colócalos en la carpeta `archivosXML/` antes de ejecutar la aplicación.

- Si quieres agregar nuevos archivos XML, colócalos en la carpeta `archivosXML/` antes de ejecutar la aplicación.
- Las tablas en la base de datos no tienen restricciones -claves foráneas-. La mala organización de los datos originales complica su implementación.
- Por el mismo motivo claves primarias debieron implementarse forzosamente en forma compuesta. 

