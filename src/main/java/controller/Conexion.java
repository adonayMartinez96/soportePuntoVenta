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

    public static String ruteFileExcel = "";

    public static void main(String[] args) {
        host = Config.getHostDataBase();
        port = Config.getPortDataBase();
        user = Config.getUserDataBase();
        pass = Config.getPasswordDataBase();
        ruteFileExcel = Config.getExcelRute();
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
                case "-e":
                    ruteFileExcel = args[++i];
                    break;
                default:
                    break;
            }
        }
        url = "jdbc:mysql://" + host + ":" + port;
    }

    public static String getRuteFileExcel(){
        return ruteFileExcel;
    }
    

    public Connection conectar() {
        return Conexion.conectarS();
    }

    public static Connection conectarS() {
        con = null;
        try {
            con = DriverManager.getConnection(url, user, pass);
            if (con == null) {
                System.err.println("Error en la conexion de la base de datos");
                System.out.println("parametros: " + user + " " + pass + " " + url);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return con;
    }

}
