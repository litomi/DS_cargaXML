package com.app;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

class XMLGestor {

    public XMLGestor() {
    }

    /**
     * Extrae los datos del archivo.
     * 
     * @param archivoXML
     * @return Map<clave, valor>
     * @throws JDOMException
     * @throws IOException
     */

    public List<Map<String, String>> extraerDatosArchivo(File archivoXML) {
        SAXBuilder saxBuilder = new SAXBuilder();
        List<Map<String, String>> filas = new ArrayList<>();
        try {
            Document documento = saxBuilder.build(archivoXML);
            Element elementoRaiz = documento.getRootElement();

            for (Element fila : elementoRaiz.getChildren()) {

                Map<String, String> datosFila = new LinkedHashMap<>();
                for (Element columna : fila.getChildren()) {
                    datosFila.put(
                            columna.getName().trim().toLowerCase(),
                            columna.getValue().trim());
                }
                if(!datosFila.isEmpty()){
                    filas.add(datosFila);
                }
            }
            

        } catch (JDOMException | IOException e) {
            // e.printStackTrace();
        }

        return filas;
    }

    public List<String> extraerClaves(Map<String, String> mapa) {
        List<String> campos = new ArrayList<>();
        mapa.keySet().forEach((clave) -> {
            campos.add(clave.trim().toLowerCase());
        });
        return campos;
    }

}