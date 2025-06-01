package com.app;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class BDgestor {
    private Connection conex;
    private Properties dbConfig;
    private Properties pkConfig;

    public BDgestor(Properties dbConfig, Properties pkConfig) {
        this.dbConfig = dbConfig;
        this.pkConfig = pkConfig;
    }

    public void conectarBD() {
        try {
            Class.forName("org.postgresql.Driver");
            this.conex = DriverManager.getConnection(
                    dbConfig.getProperty("db.url"),
                    dbConfig.getProperty("db.usr"),
                    dbConfig.getProperty("db.pass"));
            this.conex.setAutoCommit(false);
        } catch (SQLException e) {
            System.out.println("ERROR: " + e.getMessage());
            e.printStackTrace();

        } catch (ClassNotFoundException e) {
            System.out.println("ERROR: Driver JDBC no encontrado. " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void desconectarBD() {
        try {
            if (!conex.isClosed())
                conex.close();

        } catch (SQLException e) {
            System.out.println("ERROR: Conexión ya cerrada.");
            e.printStackTrace();
        }
    }

    /**
     * Crear una cadena con los campos para 
     * clave primaria. Ej.: campo1, campo2
     * @param nombreTabla
     * @return
     */
    private String camposPK(String nombreTabla) {
        return pkConfig.getProperty(nombreTabla + ".pk");
    }

    /**
     * Crea tabla en la base de datos.
     * @param nombreTabla
     * @param campos
     */

    public void crearTabla(String nombreTabla, List<String> campos) {

        String sql = "CREATE TABLE IF NOT EXISTS " + nombreTabla + "(";
        for (String c : campos) {
            sql += c + " VARCHAR(100),";
        }
        sql += " PRIMARY KEY(" + camposPK(nombreTabla) +"));";

        try (Statement stmt = conex.createStatement()) {
            stmt.executeUpdate(sql);
            conex.commit();
            System.out.println("CREATE TABLE para '" + nombreTabla + "' ejecutado.");
        } catch (SQLException e) {
            try {
                conex.rollback();
            } catch (SQLException e1) {
                e1.printStackTrace();
            } 
            System.err.println("Error al ejecutar CREATE TABLE para " + nombreTabla + ": " + e.getMessage());
            System.err.println("SQL intentado: " + sql);
        }

    }

    public void crearBaseDeDatos(){
        String sql = String.format(
        "CREATE DATABASE %s WITH OWNER=%s "+
        "ENCODING='UTF8' "+
        "LC_COLLATE='es_AR.UTF-8' "+
        "LC_CTYPE='es_AR.UTF-8' "+
        "TEMPLATE=template0;",
        dbConfig.getProperty("db.dbname"),
        dbConfig.getProperty("db.usr")
        );

        try(Statement stmt = conex.createStatement()) {
            stmt.executeUpdate(sql);
            conex.commit();
            System.out.println("CREATE DATABASE para '" + dbConfig.getProperty("db.dbname") + "' ejecutado");
            
        } catch (SQLException e) {
            System.out.println("ERROR: No se pudo crear la base de datos.");
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }

    public void insertarDatos(List<Map<String, String>> datosTabla, String nombreTabla){

    }

    
}
