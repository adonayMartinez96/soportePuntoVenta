package controller;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class InsertOrder {

    public int findCustomer(String phoneNumber) {
        int resultado = 0;

        //In this query, i do find a one customer for phone number
        String findCustomer = "select count(telefono) as contador from silverpos.clientes c where c.telefono = ?";
        try {
            Conexion con = new Conexion();
            Connection conexionMysql = con.conectar();

            PreparedStatement st = conexionMysql.prepareStatement(findCustomer);
            st.setString(1,phoneNumber);
            ResultSet rs = st.executeQuery();

            while (rs.next()) {
                int contador = rs.getInt("contador");
                resultado = (contador > 0) ? 1 : 0;
                System.out.println("Resultado: " + resultado);
            }
        }    catch (SQLException e){
            e.printStackTrace();
        }

        return resultado;
    }

    public void insertCustomer(String name,String numberPhone){
        //query to do insert a new customer
        String insertCus = "INSERT INTO silverpos.clientes\n" +
                "(nombre, direccion, telefono, email, estado, codigo, num_doc, id_tipo_doc, fecha_expedicion, tipo_cliente, primer_apellido, segundo_apellido,\n" +
                "nacionalidad, sexo, fecha_nacimiento, ciudad, provincia_estado, observacion, prefijo, telefono2, giro, registro, updated, mecanico,\n" +
                "pasaporte, empresa, nit, profesion, cargo, num_casa_apartamento,estado_civil, dependiente1, nodependiente1, dependiente2, nodependiente2,\n" +
                "dependiente3, nodependiente3,nombre_comercial, razon_social, direccion_juridica, tel_juridica, email_juridica, nit_juridica, registro_juridica,\n" +
                "id_tipomembresia, membresia, referencia1, referencia2, referencia3, vendedor,lugar_efectuo_venta, no_noches_gratis,\n" +
                "no_tours, no_masaje, fecha_creacion)\n" +
                "values\n" +
                "(?, '', ?, '', 0, '0', '0', 1, '2000-01-01', 1, ' ', ' ', 1, 1, NULL, ' ', ' ', ' ', '1', ' ', ' ', ' ', now(), 0, '', 0, \n" +
                "'', '', '', '', '', '', '', '', '', '', '', '', '', '', '', '', '', '', 0, '', '', '', '', '', 0, 0, 0, 0, NULL);";

        try{
            //conect with database
            Conexion con = new Conexion();
            Connection mysql  =  con.conectar();

            //in this block are passed the parameters to build the query
            PreparedStatement st = mysql.prepareStatement(insertCus);
            st.setString(1,name);
            st.setString(2,numberPhone);

            System.out.println("\n se inserto el cliente: " + numberPhone+"\n");
        }catch (SQLException e ){
            e.printStackTrace();
        }
    }
}