package Repositories;

/* import org.apache.commons.text.similarity.LevenshteinDistance; */

import controller.Conexion;
import decoder.core.ProductMatcher;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ProductsRepository {

    private static final String GET_PRODUCTS = "select nombre, nombre_corto from silverpos.plus p  where activo  = 1 and length(nombre)>0 and id not in (170,171,173,180,100);";

    public static String searchProduct(String productInput) {
        try (Connection connection = Conexion.conectarS();
             PreparedStatement preparedStatement = connection.prepareStatement(GET_PRODUCTS)) {

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                List<String> nombreCortoList = new ArrayList<>();

                while (resultSet.next()) {
                    ///String nombre = resultSet.getString("nombre");
                    String nombreCorto = resultSet.getString("nombre_corto");
                    nombreCortoList.add(nombreCorto);
                }

                return ProductMatcher.findBestMatch(productInput, nombreCortoList);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static int getIdProductByName(String productName) {
        String query = "SELECT id, nombre, nombre_corto FROM silverpos.plus " +
                "WHERE activo = 1 AND LENGTH(nombre) > 0 AND id NOT IN (170, 171, 173, 180, 100) " +
                "AND (nombre = ? OR nombre_corto = ?)";

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

    public static List<String> getAvailableProducts() {
        List<String> availableProducts = new ArrayList<>();

        try (Connection connection = Conexion.conectarS();
                PreparedStatement preparedStatement = connection.prepareStatement(GET_PRODUCTS);
                ResultSet resultSet = preparedStatement.executeQuery()) {

            while (resultSet.next()) {
                // availableProducts.add(resultSet.getString("nombre"));
                availableProducts.add(resultSet.getString("nombre_corto"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return availableProducts;
    }

    public static String getProductNameById(int id) {
        String query = "SELECT nombre, nombre_corto FROM silverpos.plus " +
                "WHERE activo = 1 AND LENGTH(nombre) > 0 AND id NOT IN (170, 171, 173, 180, 100) " +
                "AND id = ? AND (nombre IS NOT NULL AND nombre != '' OR nombre_corto IS NOT NULL AND nombre_corto != '')";

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
