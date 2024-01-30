package Repositories;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import controller.Conexion;

public class OrderTypeRespository {

    public static Map<Integer, String> getTypeOrderMap() {
        String query = "select id, nombre from silverpos.tipo_orden  where activo = 1 and length(nombre) > 1;";
        Map<Integer, String> resultMap = new HashMap<>();

        try (Connection connection = Conexion.conectarS();
                PreparedStatement preparedStatement = connection.prepareStatement(query);
                ResultSet resultSet = preparedStatement.executeQuery()) {

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String nombre = resultSet.getString("nombre");
                resultMap.put(id, nombre);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return resultMap;
    }
}
