package controller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class InsertAddress {

    public void insertAddressClient(int idCLient,String addressComplete,String exactAddress){
        String insertAddressExact = "INSERT INTO silverpos.clientes_direcciones_domicilios\n" +
                "( id_cliente, direccion, referencia, identificador)\n" +
                "VALUES( ?, ?, ?, '1')";

        //conect with database
        Conexion con = new Conexion();
        Connection mysql  =  con.conectar();

        try {
            PreparedStatement st = mysql.prepareStatement(insertAddressExact);
            st.setInt(1,idCLient);
            st.setString(2,addressComplete);
            st.setString(3,exactAddress);

            st.executeUpdate();

        }catch (SQLException e){
            e.printStackTrace();
        }
    }
}
