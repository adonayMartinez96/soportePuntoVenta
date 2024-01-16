package Repositories;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import Models.Customer;
import controller.Conexion;

public class CustomersRepository {
    private String findCustomerByPhoneQuery = "SELECT * FROM silverpos.clientes c WHERE c.telefono = ?";

    public List<Customer> getCustomersByPhone(String phoneNumber) {
        List<Customer> customers = new ArrayList<>();

        try (Connection connection = Conexion.conectarS();
             PreparedStatement preparedStatement = connection.prepareStatement(findCustomerByPhoneQuery)) {

            preparedStatement.setString(1, phoneNumber);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    int id = resultSet.getInt("id");
                    String name = resultSet.getString("nombre");
                    String phone = resultSet.getString("telefono");
                    String city = resultSet.getString("ciudad");

                    Customer customer = new Customer(id, name, phone, city);
                    customers.add(customer);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Manejo de errores, puedes personalizar según tu necesidad
        }

        return customers;
    }
}
