package Repositories;

import org.apache.commons.text.similarity.LevenshteinDistance;

import controller.Conexion;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


public class ProductsRepository {

    private static final String GET_PRODUCTS = "SELECT nombre, nombre_corto FROM silverpos.plus " +
            "WHERE (nombre IS NOT NULL AND nombre != '') OR (nombre_corto IS NOT NULL AND nombre_corto != '');";

    public static String searchProduct(String productInput) {
        try (Connection connection = Conexion.conectarS();
                PreparedStatement preparedStatement = connection.prepareStatement(GET_PRODUCTS)) {

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                String bestMatch = "";
                int minDistance = Integer.MAX_VALUE;

                while (resultSet.next()) {
                    String nombre = resultSet.getString("nombre");
                    String nombreCorto = resultSet.getString("nombre_corto");


                    if (nombre.equalsIgnoreCase(productInput) || nombreCorto.equalsIgnoreCase(productInput)) {
                        return productInput; 
                    }


                    int distanceNombre = LevenshteinDistance.getDefaultInstance().apply(nombre, productInput);
                    int distanceNombreCorto = LevenshteinDistance.getDefaultInstance().apply(nombreCorto, productInput);


                    if (distanceNombre < minDistance) {
                        minDistance = distanceNombre;
                        bestMatch = nombre;
                    }

                    if (distanceNombreCorto < minDistance) {
                        minDistance = distanceNombreCorto;
                        bestMatch = nombreCorto;
                    }
                }

                return bestMatch;
            }
        } catch (SQLException e) {
            e.printStackTrace(); 
            return null;
        }
    }

    public static int getIdProductByName(String productName) {
        String query = "SELECT id FROM silverpos.plus WHERE " +
                       "nombre = ? OR nombre_corto = ?";
    
        try (Connection connection = Conexion.conectarS();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
    
            String searchedProduct = searchProduct(productName);
            if (searchedProduct != null) {
                preparedStatement.setString(1, searchedProduct);
                preparedStatement.setString(2, searchedProduct);
    
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        return resultSet.getInt("id");
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace(); 
        }
    
        return -1; 
    }

    public static String getProductNameById(int id) {
        String query = "SELECT nombre, nombre_corto FROM silverpos.plus " +
                       "WHERE id = ? AND (nombre IS NOT NULL AND nombre != '' OR nombre_corto IS NOT NULL AND nombre_corto != '')";
    
        try (Connection connection = Conexion.conectarS();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
    
            preparedStatement.setInt(1, id);
    
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    String nombre = resultSet.getString("nombre");
                    String nombreCorto = resultSet.getString("nombre_corto");
    
                    return (nombre != null && !nombre.isEmpty()) ? nombre : nombreCorto;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace(); 
        }
    
        return null; 
    }
    
    
    


}
