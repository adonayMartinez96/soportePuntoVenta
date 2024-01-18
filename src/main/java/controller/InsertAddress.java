package controller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class InsertAddress {

    public int insertAddressClient(int idClient, String addressComplete, String reference) {
        String insertAddressExact = "INSERT INTO silverpos.clientes_direcciones_domicilios\n" +
                "(id_cliente, direccion, referencia, identificador)\n" +
                "VALUES(?, ?, ?, '1')";

        // Conectar con la base de datos
        Conexion con = new Conexion();
        Connection mysql = con.conectar();

        try {
            // Especificar que se desea recuperar las claves generadas automáticamente
            PreparedStatement st = mysql.prepareStatement(insertAddressExact, PreparedStatement.RETURN_GENERATED_KEYS);
            st.setInt(1, idClient);
            st.setString(2, addressComplete);
            st.setString(3, reference);

            // Ejecutar la inserción
            st.executeUpdate();

            ResultSet generatedKeys = st.getGeneratedKeys();
            if (generatedKeys.next()) {
                int idInserted = generatedKeys.getInt(1);
                return idInserted;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        // Si hay algún problema, retornar un valor que indique que no se pudo obtener
        // el ID
        return -1;
    }

}
