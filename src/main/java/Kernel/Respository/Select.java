package Kernel.Respository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import controller.Conexion;

public class Select {

    public static int selectMaxColumnLength(String schema, String table, String column) {
        String query = "SELECT COLUMN_NAME, CHARACTER_MAXIMUM_LENGTH " +
                "FROM INFORMATION_SCHEMA.COLUMNS " +
                "WHERE TABLE_SCHEMA = ? AND TABLE_NAME = ? AND COLUMN_NAME = ?";

        try (Connection connection = Conexion.conectarS();
                PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, schema);
            preparedStatement.setString(2, table);
            preparedStatement.setString(3, column);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt("CHARACTER_MAXIMUM_LENGTH");
                } else {
                    System.out.println("No se encontró información para la columna especificada.");
                    return -1; 
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return -1; 
        }
    }
}
