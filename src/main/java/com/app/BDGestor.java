package com.app;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class BDGestor {
    private Connection conex;
    private Properties dbConfig;
    private Properties pkConfig;

    public BDGestor(Properties dbConfig, Properties pkConfig) {
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
     * 
     * @param nombreTabla
     * @return
     */
    private String camposPK(String nombreTabla) {
        return pkConfig.getProperty(nombreTabla + ".pk");
    }

    /**
     * Crea tabla en la base de datos.
     * 
     * @param nombreTabla
     * @param campos
     */

    public void crearTabla(String nombreTabla, List<String> campos) {

        String sql = "CREATE TABLE " + nombreTabla + "(";
        for (String c : campos) {
            sql += c + " VARCHAR(100),";
        }
        sql += " PRIMARY KEY(" + camposPK(nombreTabla) + "));";

        try (Statement stmt = conex.createStatement()) {
            conex.setAutoCommit(false);
            stmt.executeUpdate(sql);
            conex.commit();
        } catch (SQLException e) {
            try {
                conex.rollback();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
            System.err.println("Error al ejecutar CREATE TABLE para " + nombreTabla + ": " + e.getMessage());
            System.err.println("SQL intentado: " + sql);
            
            
        } finally {
            try {
                conex.setAutoCommit(true);
            } catch (SQLException e2) {
                e2.printStackTrace();
            }
        }

    }

    private String armarSqlDeInsercion(Map<String, String> mapa, String nombreTabla) {
        List<String> cols = new ArrayList<>();
        List<String> vals = new ArrayList<>();

        mapa.keySet().forEach((k) -> {
            cols.add(k);
            vals.add("?");
        });

        return String.format(
                "INSERT INTO %s(%s) VALUES(%s)",
                nombreTabla,
                String.join(", ", cols),
                String.join(", ", vals));
    }

    public int insertarDatos(List<Map<String, String>> datosTabla, String nombreTabla) {
        int filasInsertadas = 0;
        int[] resultados;

        if (datosTabla == null || datosTabla.isEmpty()) {
            System.out.println("No hay datos para insertar en '" + nombreTabla + "'.");
            return 0;
        }

        String sql = armarSqlDeInsercion(datosTabla.get(0), nombreTabla);

        try (PreparedStatement statement = conex.prepareStatement(sql)) {

            conex.setAutoCommit(false);

            for (Map<String, String> fila : datosTabla) {
                int i = 1;
                for (String valor : fila.values()) {
                    statement.setString(i++, valor);
                }
                statement.addBatch();
            }
            resultados = statement.executeBatch();
            conex.commit();

            for (int f : resultados) {
                if (f == Statement.SUCCESS_NO_INFO) {
                    filasInsertadas++;
                } else if (f >= 0) {
                    filasInsertadas += f;
                }
            }

        } catch (SQLException e) {
            System.out.println("ERROR: fallo en la inserción en lote: " + e.getMessage());
            e.printStackTrace();
            try {
                if (conex != null) {
                    conex.rollback();
                }
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
            
        } finally {
            try {
                conex.setAutoCommit(true);
            } catch (Exception e2) {
                e2.printStackTrace();
            }
            
        }

        return filasInsertadas;
    }

    void eliminarTabla(String nombreTabla){
        String sql = String.format("DROP TABLE IF EXISTS %s CASCADE", nombreTabla);
        try(Statement stmt = conex.createStatement()){
            stmt.execute(sql);
        }catch ( SQLException e){
            System.out.println(e.getMessage());
        }
    }

}
