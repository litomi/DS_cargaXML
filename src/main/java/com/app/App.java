package com.app;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class App {
    private static final Logger logger = LoggerFactory.getLogger(App.class);

    public static void main(String[] args) {
        Runtime.getRuntime().addShutdownHook(new Thread(App::alTerminar));

        // Carga de los archivos de configuraciones
        Properties bdProp = cargarConfiguraciones("src/main/resources/db.properties");
        Properties pkProp = cargarConfiguraciones("src/main/resources/pk.properties");

        // Creación de objetos gestores
        BDGestor gestorBD = new BDGestor(bdProp, pkProp);
        XMLGestor gestorXML = new XMLGestor();

        String uriCarpeta = "./archivosXML/.";
        String nombreTabla;
        List<String> nombresCampos;
        List<Map<String, String>> filasTabla;
        int filasInsertadas = 0;

        // Carpeta donde se encuentran los archivos
        File directorio = new File(uriCarpeta);

        // Conectar a la base de datos.
        gestorBD.conectarBD();
        logger.info("Conexión a la base de datos establecida");

        // Recorremos la carpeta con los archivos, si existe y es directorio
        if (directorio.exists() && directorio.isDirectory()) {
            logger.info("---------------------------------------------");

            // Procesamos cada archivo
            for (File archivo : directorio.listFiles()) {
                logger.info("Procesando archivo '{}'...", archivo.getName());

                // Tomamos los datos del archivo actual
                filasTabla = gestorXML.extraerDatosArchivo(archivo);

                // Extraemos nombres de campos (columnas) para la tabla correspondiente
                nombresCampos = gestorXML.extraerClaves(filasTabla.get(0));

                // Formamos el nombre de la tabla quitando extensión.
                nombreTabla = archivo.getName().replaceAll("\\.xml", "").trim();
                logger.info("Datos extraídos: ");
                logger.info("\t|Nombre de tabla: {}", nombreTabla);
                logger.info("\t|Campos:");
                nombresCampos.forEach(c -> logger.info("\t\t{}", c));

                // Creamos la tabla en la base de datos.
                gestorBD.crearTabla(nombreTabla, nombresCampos);
                logger.info("Tabla creada con éxito.");

                // Insertamos los datos en tabla creada
                filasInsertadas = gestorBD.insertarDatos(filasTabla, nombreTabla);
                logger.info("{} registros insertados.", filasInsertadas);

                logger.info("---------------------------------------------");
            }
        } else {
            logger.error("El directorio {} no existe o no es un directorio", uriCarpeta);
        }

        // Desconexión
        gestorBD.desconectarBD();
        logger.info("Conexión a la base de datos cerrada");
    }

    private static Properties cargarConfiguraciones(String nombreArchivo) {
        Properties props = new Properties();
        try (FileInputStream fis = new FileInputStream(nombreArchivo)) {
            props.load(fis);
            logger.info("Archivo de configuración {} cargado con éxito", nombreArchivo);
        } catch (IOException e) {
            logger.error("Error al cargar el archivo de configuración {}: {}", nombreArchivo, e.getMessage());
            e.printStackTrace();
        }
        return props;
    }

    private static void alTerminar() {
        logger.info("-----------¡FIN DEL PROGRAMA!----------");
    }
}
