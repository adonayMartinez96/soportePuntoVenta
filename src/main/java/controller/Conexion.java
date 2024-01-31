package controller;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import Kernel.config.Config;

//HOLA MUNDO
public class Conexion {
    public static Connection con;

    public static String user = "";
    public static String pass = "";
    public static String url = "";
    public static String port = "";
    public static String host = "";

    // ESTADO: PRODUCCIOn
    // UAT
    // private static final String user = "root";
    // private static final String pass = "alianza96";
    // private static final String url = "jdbc:mysql://localhost/impadi";

    public static void main(String[] args) {
        host = Config.getHostDataBase();
        port = Config.getPortDataBase();
        user = Config.getUserDataBase();
        pass = Config.getPasswordDataBase();
        for (int i = 0; i < args.length; i++) {
            switch (args[i]) {
                case "-h":
                    host = args[++i];
                    break;
                case "-P":
                    port = args[++i];
                    break;
                case "-u":
                    user = args[++i];
                    break;
                case "-p":
                    pass = args[++i];
                    break;
                default:
                    break;
            }
        }
        url = "jdbc:mysql://" + host + ":" + port + "/impadi";
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
