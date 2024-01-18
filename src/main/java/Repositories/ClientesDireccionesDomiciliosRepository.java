package Repositories;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import Models.ClientesDireccionesDomicilios;
import controller.Conexion;

public class ClientesDireccionesDomiciliosRepository {
    private String findDireccionesByClienteQuery = "SELECT * FROM silverpos.clientes_direcciones_domicilios WHERE id_cliente = ?";

    public List<ClientesDireccionesDomicilios> getDireccionesByCliente(int idCliente) {
        List<ClientesDireccionesDomicilios> direcciones = new ArrayList<>();

        try (Connection connection = Conexion.conectarS();
             PreparedStatement preparedStatement = connection.prepareStatement(findDireccionesByClienteQuery)) {

            preparedStatement.setInt(1, idCliente);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    int id = resultSet.getInt("id");
                    String direccion = resultSet.getString("direccion");
                    String referencia = resultSet.getString("referencia");
                    String identificador = resultSet.getString("identificador");


                    ClientesDireccionesDomicilios direccionCliente = new ClientesDireccionesDomicilios(idCliente, direccion, referencia, identificador);
                    direccionCliente.setId(id);

                    direcciones.add(direccionCliente);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace(); 
        }

        return direcciones;
    }
}
