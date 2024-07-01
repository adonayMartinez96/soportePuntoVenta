package Repositories;

import controller.Conexion;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import Kernel.Respository.CustomPreparedStatement;
import Kernel.Respository.Insert;
import Kernel.Respository.Select;

public class OrdersRepository {

    public static Map<String, Object> insertOrder(String address, String reference, String nameClient, String phone, Integer idOrderType) {
        Map<String, Object> result = new HashMap<>();
        Integer lastInsertedId = 0;

        try (Connection connection = Conexion.conectarS()) {
            Insert insert = new Insert("ventasdiarias.venta_encabezado");

            insert.setColumn("idh", 0);
            insert.setColumn("terminal", 0);
            insert.setColumn("fechanegocio", "DATE_FORMAT(NOW(), '%Y-%m-%d')");
            insert.setColumn("fechatransaccion", "NOW()" );
            insert.setColumn("no_orden", getNextNumber("no_orden", 400), true);
            insert.setColumn("mesa", "0");
            insert.setColumn("invitados", "1");
            insert.setColumn("idcajero", "3");
            insert.setColumn("tipo_propina", "1");
            insert.setColumn("valor_propina", "0,00");
            insert.setColumn("id_Ordertype", idOrderType, true); // es el tipo de orden
            insert.setColumn("precuentas_prints", "0");
            insert.setColumn("Cliente_temporal", "");
            insert.setColumn("idcliente", 1);
            insert.setColumn("observacion", phone, true);
            insert.setColumn("tipodoc", 1);
            insert.setColumn("num_doc", getNextNumber("num_doc", 50000), true);
            insert.setColumn("id_z", 0);
            insert.setColumn("id_cortecajero", 0);
            insert.setColumn("no_habitacion", "0");
            insert.setColumn("idmesero", 0);
            insert.setColumn("cerrada", 0);
            insert.setColumn("borrada", 0);
            insert.setColumn("id_user_borro", 0);
            insert.setColumn("id_z2", 0);
            insert.setColumn("id_z3", 0);
            insert.setColumn("id_resolution", 0);
            insert.setColumn("birthday", "");
            insert.setColumn("sucursal_id", 1);
            insert.setColumn("id_reporte", 0);
            insert.setColumn("num_reporte", "");
            insert.setColumn("contingencia", 0);
            insert.setColumn("serie", "");
            insert.setColumn("uuid", "");
            insert.setColumn("direccion", "");
            insert.setColumn("telefono", phone, true);
            insert.setColumn("num_fac_electronica", "");
            insert.setColumn("firma64", "");
            insert.setColumn("idmotorista", 0);
            insert.setColumn("direccion_domicilio", verifysMaxLen(address, "direccion_domicilio"), true);
            insert.setColumn("referencia_domicilio", reference, true);
            insert.setColumn("cliente_domicilio", nameClient, true);
            insert.setColumn("liquidada_motorista", 0);
            insert.setColumn("anulado", 0);
            insert.setColumn("razon_anulado", "");
            insert.setColumn("erp", 0);
            insert.setColumn("web", 0);
            insert.setColumn("serie_interna", "");
            insert.setColumn("hora_cocinado", "0001-01-01 00:00:00");
            insert.setColumn("hora_cerro", "0001-01-01 00:00:00");
            insert.setColumn("hora_asignacion_motorista", "0001-01-01 00:00:00");
            insert.setColumn("covid", 0);
            insert.setColumn("cc", "");
            insert.setColumn("tipo", "");
            insert.setColumn("modelo", "");
            insert.setColumn("ano", "");
            insert.setColumn("comisionpagada", 0);
            insert.setColumn("id_mecanico", 0);
            insert.setColumn("mecanico", ""); 


        
            //insert.debug();

             CustomPreparedStatement customPreparedStatement = new CustomPreparedStatement(connection,
                    "ventasdiarias.venta_encabezado"); 

            try (PreparedStatement preparedStatement = customPreparedStatement.prepareInsertStatement(
                insert
            )) {
                int rowsAffected = preparedStatement.executeUpdate();

                if (rowsAffected > 0) {
                    try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                        if (generatedKeys.next()) {
                            lastInsertedId = generatedKeys.getInt(1);
                        }
                    }
                }

                result.put("id", lastInsertedId);
                result.put("date", OrdersRepository.getDateInsertOrder(lastInsertedId));
                result.put("rowsAffected", rowsAffected);
            } catch (SQLException e) {
                e.printStackTrace(); // Manejo de la excepción, puedes personalizarlo según tus necesidades
            }

        } catch (SQLException e) {
            e.printStackTrace();
            result.put("error", e.getMessage());
        }

        return result;
    }

    public static int getNextNumber(String columnName, int startingValue) {
        try (Connection connection = Conexion.conectarS();
                PreparedStatement preparedStatement = connection.prepareStatement(
                        String.format("SELECT MAX(%s) FROM ventasdiarias.venta_encabezado", columnName))) {

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                return resultSet.next() ? Math.max(startingValue, resultSet.getInt(1)) + 1 : startingValue;
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }


    public static String getDateInsertOrder(int idOrder) {
        try (Connection connection = Conexion.conectarS();
            PreparedStatement preparedStatement = connection.prepareStatement(
                    "SELECT fechatransaccion FROM ventasdiarias.venta_encabezado WHERE id = ?")) {
    
            preparedStatement.setInt(1, idOrder);
    
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                return resultSet.next() ? resultSet.getTimestamp(1).toString() : null;
            }
    
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }


    public static String verifysMaxLen(String input, String column) {
        int maxColumnLength = Select.selectMaxColumnLength("ventasdiarias", "venta_encabezado", column);
        return (maxColumnLength > 0 && input.length() > maxColumnLength) 
            ? input.substring(0, maxColumnLength) 
            : input;
    }
    

}
