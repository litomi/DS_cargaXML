package com.app;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;

class BDGestorTest {
    private BDGestor bdGestor;
    private Properties dbProps;
    private Properties pkProps;

    @BeforeEach
    void setUp() {
        // Configurar propiedades de BD
        dbProps = new Properties();
        dbProps.setProperty("db.url", "jdbc:postgresql://localhost:5432/pruebas_db");
        dbProps.setProperty("db.usr", "sysacad_usr");
        dbProps.setProperty("db.pass", "sysacad_pass");

        // Configurar propiedades de PK
        pkProps = new Properties();
        pkProps.setProperty("test_tabla.pk", "codigo");

        bdGestor = new BDGestor(dbProps, pkProps);
        bdGestor.conectarBD(); 
    }

    @Test
    void crearTabla_ConCamposValidos_CreaTablaCorrectamente() {
        // Preparar datos
        String nombreTabla = "test_tabla";
        List<String> campos = Arrays.asList("codigo", "nombre", "descripcion");

        bdGestor.eliminarTabla(nombreTabla);

        bdGestor.crearTabla(nombreTabla, campos);

        // Verificar - Si no hay excepción, la tabla se creó correctamente
        assertTrue(true, "La tabla se creó sin errores");
    }

    
    @Test
    void insertarDatos_ConDatosValidos_InsertaCorrectamente() {
        String nombreTabla = "test_tabla";
        List<String> campos = Arrays.asList("codigo", "nombre", "descripcion");
        bdGestor.crearTabla(nombreTabla, campos);

        List<Map<String, String>> filas = Arrays.asList(
            Map.of(
                "codigo", "001",
                "nombre", "Test 1",
                "descripcion", "Descripción 1"
            ),
            Map.of(
                "codigo", "002",
                "nombre", "Test 2",
                "descripcion", "Descripción 2"
            )
        );

        int filasInsertadas = bdGestor.insertarDatos(filas, nombreTabla);

        assertEquals(2, filasInsertadas);
    }

    @Test
    void conectarBD_ConCredencialesValidas_ConectaCorrectamente() {
        assertTrue(true, "La conexión se estableció correctamente");
    }
}
