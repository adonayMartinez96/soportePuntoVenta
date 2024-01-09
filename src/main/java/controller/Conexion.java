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

    //ESTADO: PRODUCCIOn
    //UAT
    //private static  final String user = "root";
    //private static  final String pass = "alianza96";
    //private static  final String url = "jdbc:mysql://localhost/impadi";

    public static void main(String[] args) {
        user = "root";
         pass = "969696";
         url = "jdbc:mysql://localhost/impadi";
    }


    //PRODUCCION
    /*private static  final String user = "silverposfx";
    private static  final String pass = "Sistemas1504@$";
    private static  final String url = "jdbc:mysql://localhost/silverpos";*/

    //UAT CASA
   /* private static  final String user = "root";
    private static  final String pass = "969696";
    private static  final String url = "jdbc:mysql://localhost/impadi";*/


    public Connection conectar(){
        System.out.println("parametros: "+ user +" " +pass+" " +url);
        con = null;
        try{
            con = DriverManager.getConnection(url,user,pass);
            System.out.println("conectado");
            if(con!=null){
                System.out.println("conexion establecida");
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        return con;
    }

}
