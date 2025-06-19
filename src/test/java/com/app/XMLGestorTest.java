package com.app;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.FileWriter;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class XMLGestorTest {
    private XMLGestor xmlGestor;
    
    @BeforeEach
    void setUp() {
        xmlGestor = new XMLGestor();
    }

    @Test
    void extraerDatosArchivo_ConArchivoValido_DevuelveDatosCorrectos(@TempDir Path tempDir) throws Exception {
        // Crear un archivo XML de prueba
        File testFile = tempDir.resolve("test.xml").toFile();
        try (FileWriter writer = new FileWriter(testFile)) {
            writer.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
            writer.write("<root>\n");
            writer.write("  <_exportar>\n");
            writer.write("    <codigo>123</codigo>\n");
            writer.write("    <ciudad>Buenos Aires</ciudad>\n");
            writer.write("    <provincia>Buenos Aires</provincia>\n");
            writer.write("    <pais_del_c>Argentina</pais_del_c>\n");
            writer.write("  </_exportar>\n");
            writer.write("</root>");
        }

        // Ejecutar el método a probar
        List<Map<String, String>> resultado = xmlGestor.extraerDatosArchivo(testFile);

        // Verificaciones
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        Map<String, String> primeraFila = resultado.get(0);
        assertEquals("123", primeraFila.get("codigo"));
        assertEquals("Buenos Aires", primeraFila.get("ciudad"));
        assertEquals("Buenos Aires", primeraFila.get("provincia"));
        assertEquals("Argentina", primeraFila.get("pais_del_c"));
    }

    @Test
    void extraerClaves_ConMapaValido_DevuelveListaCorrecta() {
        // Preparar datos de prueba
        Map<String, String> mapa = Map.of(
            "CODIGO", "123",
            "CIUDAD", "Buenos Aires",
            "PROVINCIA", "Buenos Aires",
            "PAIS_DEL_C", "Argentina"
        );

        // Ejecutar el método a probar
        List<String> claves = xmlGestor.extraerClaves(mapa);

        // Verificaciones
        assertNotNull(claves);
        assertEquals(4, claves.size());
        assertTrue(claves.contains("codigo"));
        assertTrue(claves.contains("ciudad"));
        assertTrue(claves.contains("provincia"));
        assertTrue(claves.contains("pais_del_c"));
    }

    @Test
    void extraerDatosArchivo_ConArchivoInexistente_DevuelveListaVacia() {
        // Crear un archivo que no existe
        File archivoInexistente = new File("archivo_inexistente.xml");

        // Ejecutar el método a probar
        List<Map<String, String>> resultado = xmlGestor.extraerDatosArchivo(archivoInexistente);

        // Verificaciones
        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
    }
}
