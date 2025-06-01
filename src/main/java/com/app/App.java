package com.app;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;


public class App {

    public static void main(String[] args) {
        Properties bdProp = cargarConfiguraciones("src/main/resources/db.properties");
        Properties pkProp = cargarConfiguraciones("src/main/resources/pk.properties");

        BDgestor gestorBD = new BDgestor(bdProp, pkProp);
        gestorBD.crearBaseDeDatos();
        // gestorBD.conectarBD();

        // ProcesadorXML procesadorXML = new ProcesadorXML();
        // List<Map<String, String>> listaMapa = new ArrayList<>();
        // String uri = "./archivosXML/.";

        // File directorio = new File(uri);

        // Map<String, String> mapa;

        // if (directorio.exists() && directorio.isDirectory()) {
        //     for (File a : directorio.listFiles()) {
        //         mapa = procesadorXML.extraerDatosArchivo(a);
        //         List<String> campos = procesadorXML.extraerClaves(mapa);
        //         listaMapa.add(mapa);
        //         gestorBD.crearTabla(a.getName().replaceAll("\\.xml", ""), campos);
        //     }

            
        // }
        // gestorBD.desconectarBD();

    }

    private static Properties cargarConfiguraciones(String nombreArchivo) {
        Properties props = new Properties();
        try (FileInputStream fis = new FileInputStream(nombreArchivo)) {
            props.load(fis);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return props;
    }
}
