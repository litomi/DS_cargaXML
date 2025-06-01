package com.app;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

class ProcesadorXML {

    public ProcesadorXML() {}

    /**
     * Extrae los datos del archivo.
     * 
     * @param archivoXML
     * @return Map<clave, valor>
     * @throws JDOMException
     * @throws IOException
     */

    public Map<String, String> extraerDatosArchivo(File archivoXML) {
        SAXBuilder saxBuilder = new SAXBuilder();
        Map<String, String> mapa = new HashMap<>();
        try {
            Document documento = saxBuilder.build(archivoXML);
            Element elementoRaiz = documento.getRootElement();

            for (Element e : elementoRaiz.getChildren()) {
                for (Element e1 : e.getChildren()) {
                    mapa.put(
                            e1.getName().trim().toLowerCase(),
                            e1.getValue().trim().toLowerCase());
                }
            }
        } catch (JDOMException | IOException e) {
            e.printStackTrace();
            return null;
        }

        return mapa;
    }

    public List<String> extraerClaves(Map<String, String> mapa) {
        List<String> campos = new ArrayList<>();
        mapa.keySet().forEach((clave) -> {
            campos.add(clave.trim().toLowerCase());
        });
        return campos;
    }

}