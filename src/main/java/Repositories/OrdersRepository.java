package Repositories;

import controller.Conexion;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class OrdersRepository {

    private static final String INSERT_ORDER_QUERY = "-- informacion de encabezado de orden insert,\n" +
    "INSERT INTO ventasdiarias.venta_encabezado\n" +
    "(idh, terminal, fechanegocio, fechatransaccion,\n" +
    "no_orden, mesa, invitados, idcajero, tipo_propina, \n" +
    "valor_propina, id_Ordertype, precuentas_prints, Cliente_temporal, idcliente,\n" +
    "observacion, tipodoc, num_doc, id_z, id_cortecajero, \n" +
    "no_habitacion, idmesero, cerrada, borrada, id_user_borro, \n" +
    "id_z2, id_z3, id_resolution, birthday, sucursal_id, \n" +
    "id_reporte, num_reporte, contingencia, serie, uuid,\n" +
    "direccion, telefono, num_fac_electronica, firma64, idmotorista,\n" +
    "direccion_domicilio, referencia_domicilio, cliente_domicilio, liquidada_motorista, anulado,\n" +
    "razon_anulado, erp, web, serie_interna, hora_cocinado, \n" +
    "hora_cerro, hora_asignacion_motorista, covid, cc, tipo, \n" +
    "modelo, ano, comisionpagada, id_mecanico, mecanico)\n" +
    "VALUES(0, 0, DATE_FORMAT(NOW(), '%Y-%m-%d'), now(),\n" +
    "?, '0', '1', '3', '1', -- numero de orden\n" +
    "'0,00', '1', '0', '', 1,\n" +
    "?, 1, ?, 0, 0, -- numero telefono asocioado cliente, num_doc arriba de 50000\n" +
    "'0', '3', 0, 0, 0,\n" +
    "0, 0, 0, '', 1,\n" +
    "0, '', '', '', '', \n" +
    "'', '', '', '', 0, \n" +
    "?, ?, ?, 0, 0, -- dirrecion_domicilio, referencia, nombre cliente\n" +
    "'', 0, 0, '', '0001-01-01 00:00:00',\n" +
    "'0001-01-01 00:00:00', '0001-01-01 00:00:00', 0, 0, '', \t\n" +
    "'', '', 0, 0, '');";



    public static int insertOrder(String address, String reference, String nameClient, String phone) {
        try (Connection connection = Conexion.conectarS();
                PreparedStatement preparedStatement = connection.prepareStatement(INSERT_ORDER_QUERY,
                        PreparedStatement.RETURN_GENERATED_KEYS)) {

            preparedStatement.setInt(1, getNextNumber("no_orden", 400));
            preparedStatement.setString(2, phone);
            preparedStatement.setInt(3, getNextNumber("num_doc", 50000));
            preparedStatement.setString(4, address);
            preparedStatement.setString(5, reference);
            preparedStatement.setString(6, nameClient);

            preparedStatement.executeUpdate();

            try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    int lastInsertedId = generatedKeys.getInt(1);
                    return lastInsertedId;
                } else {
                    throw new SQLException("No se pudo obtener el último ID insertado.");
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }

    public static int getNextNumber(String columnName, int startingValue) {
        try (Connection connection = Conexion.conectarS();
                PreparedStatement preparedStatement = connection.prepareStatement(
                        String.format("SELECT MAX(%s) FROM ventasdiarias.venta_encabezado", columnName))) {
    
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    int maxNumber = resultSet.getInt(1);
    
                    // Comenzar desde el valor inicial, pero solo incrementar si el máximo es mayor
                    return Math.max(startingValue, maxNumber) + 1;
                } else {
                    // Si no hay registros, comenzar desde el valor inicial
                    return startingValue;
                }
            }
    
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }
    

}
