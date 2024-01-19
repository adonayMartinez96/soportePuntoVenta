package controller;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

//HOLA MUNDO
public class Conexion {
    public static Connection con;

    public static String user = "";
    public static String pass = "";
    public static String url = "";
    public static String port = "";

    // ESTADO: PRODUCCIOn
    // UAT
    // private static final String user = "root";
    // private static final String pass = "alianza96";
    // private static final String url = "jdbc:mysql://localhost/impadi";

    public static void main(String[] args) {
        user = "root";
        pass = "969696";
        port = "3306";
        url = "jdbc:mysql://localhost:" + port + "/impadi";
    }

    public Connection conectar() {
        return Conexion.conectarS();
    }

    public static Connection conectarS() {
        System.out.println("parametros: " + user + " " + pass + " " + url);
        con = null;
        try {
            con = DriverManager.getConnection(url, user, pass);
            System.out.println("conectado");
            if (con != null) {
                System.out.println("conexion establecida");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return con;
    }

}
