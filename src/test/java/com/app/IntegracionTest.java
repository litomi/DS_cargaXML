package com.app;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.FileWriter;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;

class IntegracionTest {
    private Properties dbProps;
    private Properties pkProps;
    private File xmlDir;

    @BeforeEach
    void setUp(@TempDir Path tempDir) throws Exception {
        // Configurar propiedades de BD
        dbProps = new Properties();
        dbProps.setProperty("db.url", "jdbc:postgresql://localhost:5432/pruebas_db");
        dbProps.setProperty("db.usr", "sysacad_usr");
        dbProps.setProperty("db.pass", "sysacad_pass");

        // Configurar propiedades de PK
        pkProps = new Properties();
        pkProps.setProperty("localidades.pk", "codigo");

        // Crear directorio temporal para archivos XML
        xmlDir = tempDir.toFile();
        crearArchivoXMLPrueba(tempDir);
    }

    private void crearArchivoXMLPrueba(Path tempDir) throws Exception {
        File testFile = tempDir.resolve("localidades.xml").toFile();
        try (FileWriter writer = new FileWriter(testFile)) {
            writer.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
            writer.write("<root>\n");
            writer.write("  <_exportar>\n");
            writer.write("    <codigo>123</codigo>\n");
            writer.write("    <ciudad>Buenos Aires</ciudad>\n");
            writer.write("    <provincia>Buenos Aires</provincia>\n");
            writer.write("    <pais_del_c>Argentina</pais_del_c>\n");
            writer.write("  </_exportar>\n");
            writer.write("  <_exportar>\n");
            writer.write("    <codigo>456</codigo>\n");
            writer.write("    <ciudad>Córdoba</ciudad>\n");
            writer.write("    <provincia>Córdoba</provincia>\n");
            writer.write("    <pais_del_c>Argentina</pais_del_c>\n");
            writer.write("  </_exportar>\n");
            writer.write("</root>");
        }
    }

    

    @Test
    void testProcesamientoCompleto() throws Exception {
        // Ejecutar el proceso
        BDGestor gestorBD = new BDGestor(dbProps, pkProps);
        XMLGestor gestorXML = new XMLGestor();

        // Conectar a la BD\
        gestorBD.conectarBD();

        // Procesar el archivo
        File archivo = new File(xmlDir, "localidades.xml");
        assertTrue(archivo.exists(), "El archivo de prueba debe existir");

        // Obtener datos del XML
        var filasTabla = gestorXML.extraerDatosArchivo(archivo);
        var nombresCampos = gestorXML.extraerClaves(filasTabla.get(0));
        String nombreTabla = archivo.getName().replaceAll("\\.xml", "").trim();

        
        
        gestorBD.eliminarTabla(nombreTabla);
        // Crear tabla e insertar datos
        gestorBD.crearTabla(nombreTabla, nombresCampos);
        int filasInsertadas = gestorBD.insertarDatos(filasTabla, nombreTabla);

        // Verificar resultados
        assertEquals(2, filasInsertadas);

        // Verificar datos en la base de datos
        try (Connection conn = DriverManager.getConnection(
                dbProps.getProperty("db.url"),
                dbProps.getProperty("db.usr"),
                dbProps.getProperty("db.pass"));
             Statement stmt = conn.createStatement()) {
            
            ResultSet rs = stmt.executeQuery("SELECT * FROM localidades ORDER BY codigo");
            
            // Verificar primera fila
            assertTrue(rs.next());
            assertEquals("123", rs.getString("codigo"));
            assertEquals("Buenos Aires", rs.getString("ciudad"));
            assertEquals("Buenos Aires", rs.getString("provincia"));
            assertEquals("Argentina", rs.getString("pais_del_c"));

            // Verificar segunda fila
            assertTrue(rs.next());
            assertEquals("456", rs.getString("codigo"));
            assertEquals("Córdoba", rs.getString("ciudad"));
            assertEquals("Córdoba", rs.getString("provincia"));
            assertEquals("Argentina", rs.getString("pais_del_c"));

            // No debería haber más filas
            assertFalse(rs.next());
        }

        // Desconectar
        gestorBD.desconectarBD();
    }
}
