package Repositories;

import controller.Conexion;
import Kernel.decoder.Decoder;
import Kernel.Respository.CustomPreparedStatement;
import Kernel.Respository.Insert;
import Kernel.utils.Extractor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
/* 
 * Esta es la tabla donde se insertan las ventas de cada producto
 * luego que se cree la orden de compra, en esta tabla se guarda
 * los productos asociados a esa compra
 */
import java.util.Map;

/**
 * Descripción de la tabla:
 * 
 * - Esta tabla es bien jodida rara xD
 * porque cada producto que se ordena se guarda en una row diferente
 * y si hay un comentario, también lo guarda en la row diferente
 * -- 170 hasta 177 los envíos, luego en 180 hay algo llamado recargo 5% no sé
 * qué significa,
 * luego en la tabla 310 hay algo llamado descuento, que no sé qué es tampoco.
 * En el id 390 hay algo llamado Varios, que no sé qué es.
 * 
 * - El id 100 y 173 de la tabla silverpos.plus están reservados para mensajes y
 * guardar el envío.
 * Igualmente, el campo solo dice "MENSAJE", así que hay que volver a setearlo
 * en la columna
 * ventadoarias.venta_detalle_plus.descripcion.
 * 
 * - La columna ventasdiarias.venta_detalle_plus.modificador siempre irá en uno
 * al en el caso que id_plu sea 100.
 * El 100 significa que es un comentario.
 * 
 * - La columna ventasdiarias.venta_detalle_plus.secuencia es solamente una
 * secuencia para esa orden, contará todos
 * los productos insertados y también contará todos los comentarios insertados.
 * 
 * - En idUser, sugiero poner un id único para identificar que el programa
 * aparte hizo la orden y así poder depurar
 * cuales son las órdenes insertadas por el programa y cuales no.
 */

public class VentaDetallePlusRepository {
    public static Decoder decoder;
    // En tu clase principal o donde estés realizando la inserción de ventas:

    public static Map<String, Object> insertOneSale(int idProduct, int sequence, double unitPrice,
            String dateOrderInsert, int idOrderInsert,
            String nameProduct) {
        Map<String, Object> result = new HashMap<>();
        Integer insertId = 0;
        try (Connection connection = Conexion.conectarS()) {

            Insert insert = new Insert("ventasdiarias.venta_detalle_plus");

            insert.setColumn("idh", 0);
            insert.setColumn("id_plu", idProduct, true);
            insert.setColumn("cantidad", 1.0); // esto creo que tambien hay que modificarlo x
            insert.setColumn("precio", calcUnitPrice(unitPrice) , true);
            insert.setColumn("descuento", 0.0000);
            insert.setColumn("id_umedida", "1");
            insert.setColumn("horatransaccion", dateOrderInsert, true);
            insert.setColumn("propina", 0.000000);
            insert.setColumn("minpreparacion", "0");
            insert.setColumn("minentrega", "0");
            insert.setColumn("descripcion", nameProduct, true);
            insert.setColumn("tax1", unitPrice * 0.13, true); // la multiplicacion de 0.13 por el precio unitario
            insert.setColumn("tax2", 0.000000);
            insert.setColumn("tax3", 0.000000);
            insert.setColumn("tax4", 0.000000);
            insert.setColumn("tax5", 0.000000);
            insert.setColumn("tax6", 0.000000);
            insert.setColumn("tax7", 0.000000);
            insert.setColumn("tax8", 0.000000);
            insert.setColumn("tax9", 0.000000);
            insert.setColumn("tax10", 0.000000);
            insert.setColumn("hold", 0);
            insert.setColumn("id_enca", idOrderInsert, true);
            insert.setColumn("idmarcado", sequence, true); // secuencia 1, esto no afectara en la secuencia por que esto es
                                                     // solamente para producntos
            insert.setColumn("terminal", "1");
            insert.setColumn("modificador", 0); // esto va cero por que esta insert es de un producto xd
            insert.setColumn("idmodificador", 0); // esto siempre va en cero xd
            insert.setColumn("idtypemodificador", 0); // siempre cero
            insert.setColumn("iduser", "3"); // ponerle un user aca para el propio sistema
            insert.setColumn("secuencia", sequence, true); // secuencia dos
            insert.setColumn("borrado", 0);
            insert.setColumn("id_user_borro", 0);
            insert.setColumn("id_user_autorizo", 0);
            insert.setColumn("id_centro_costo", 0);
            insert.setColumn("peso", 0.0);
            insert.setColumn("untaxable", 0);
            insert.setColumn("descripcion2", "");
            insert.setColumn("identificador", "G");
            insert.setColumn("precioinicial",  calcUnitPrice(unitPrice), true);
            insert.setColumn("erp", 0);
            insert.setColumn("monitor", 0.0);
            insert.setColumn("comision", 0);
            insert.setColumn("idhoteldetaservicios", 0);
            insert.setColumn("web", 0);

            CustomPreparedStatement customPreparedStatement = new CustomPreparedStatement(connection,
                    "ventasdiarias.venta_detalle_plus");

            try (PreparedStatement preparedStatement = customPreparedStatement.prepareInsertStatement(
                    insert)) {
                int rowsAffected = preparedStatement.executeUpdate();

                if (rowsAffected > 0) {
                    try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                        if (generatedKeys.next()) {
                            insertId = generatedKeys.getInt(1);
                        }
                    }
                }
                result.put("rowsAffected", rowsAffected);
                result.put("insertId", insertId);
                result.put("insertedSequences", sequence);
            } catch (SQLException e) {
                e.printStackTrace(); // Manejo de la excepción, puedes personalizarlo según tus necesidades
            }

        } catch (SQLException e) {
            e.printStackTrace();
            result.put("error", e.getMessage());
        }

        System.out.println("Insercion de una venta");
        System.out.println("id: " + insertId);
        System.out.println("numero ultimo secuencia: " + sequence);
        return result;
    }

    /*
     * insertMultiplesSales lo que retornara es lo siguiente
     * 
     * [
     * {
     * insertId -> Integer
     * insertedSequences -> Integer
     * rowsAffected -> Integer
     * },
     * 
     * {
     * insertId -> Integer
     * insertedSequences -> Integer
     * rowsAffected -> Integer
     * }
     * 
     * ]
     * 
     * con la funcion Extractor.extract lo que se obtiene es una lista del mapa de
     * lo que contiene la misma
     * key, por ejempplo, si queremos hacer un extract de insertedSequences nos
     * devolvera lo siguiente:
     * 
     * 
     * [
     * Integer,
     * Integer,
     * Integer
     * ]
     */

    public static List<Map<String, Object>> insertMultiplesSales(int idProduct, double unitPrice,
            String dateOrderInsert, int idOrderInsert, String nameProduct, int productQuantityInput, int sequence) {
        List<Map<String, Object>> resultsList = new ArrayList<>();

        for (int i = 0; i < productQuantityInput; i++) {
            Map<String, Object> result = VentaDetallePlusRepository.insertOneSale(idProduct, sequence, unitPrice,
                    dateOrderInsert, idOrderInsert, nameProduct);

            resultsList.add(result);
        }

        return resultsList;
    }

    public static List<Integer> extractInsertIds(List<Map<String, Object>> resultsList) {
        return Extractor.extract(resultsList, "insertId", Integer.class);
    }

    public static List<Integer> extractInsertIdsSequence(List<Map<String, Object>> resultsList) {
        return Extractor.extract(resultsList, "insertedSequences", Integer.class);
    }

    public static Integer extractInsertLastSequence(List<Map<String, Object>> resultsList) {
        List<Integer> allSequences = VentaDetallePlusRepository.extractInsertIdsSequence(resultsList);
        return allSequences.isEmpty() ? null : allSequences.get(allSequences.size() - 1);
    }

    public static List<Map<String, Object>> insertRowForCommet(String commet, String dateOrderInsert, int idInsertOrder,
            int idmodificadorSequense, int SecuenceNormal) {
        List<Map<String, Object>> resultList = new ArrayList<>();
        Integer insertId = 0;
        try (Connection connection = Conexion.conectarS()) {
            Insert insert = new Insert("ventasdiarias.venta_detalle_plus");

            insert.setColumn("idh", 0);
            insert.setColumn("id_plu", 100); // El 100 es el id de mensaje
            insert.setColumn("cantidad", 1.0);
            insert.setColumn("precio", 0.000000);
            insert.setColumn("descuento", 0.0000);
            insert.setColumn("id_umedida", "1");
            insert.setColumn("horatransaccion", dateOrderInsert, true);
            insert.setColumn("propina", 0.000000);
            insert.setColumn("minpreparacion", "0");
            insert.setColumn("minentrega", "0");
            insert.setColumn("descripcion", commet, true);
            insert.setColumn("tax1", 0.000000);
            insert.setColumn("tax2", 0.000000);
            insert.setColumn("tax3", 0.000000);
            insert.setColumn("tax4", 0.000000);
            insert.setColumn("tax5", 0.000000);
            insert.setColumn("tax6", 0.000000);
            insert.setColumn("tax7", 0.000000);
            insert.setColumn("tax8", 0.000000);
            insert.setColumn("tax9", 0.000000);
            insert.setColumn("tax10", 0.000000);
            insert.setColumn("hold", 0);
            insert.setColumn("id_enca", idInsertOrder, true);
            insert.setColumn("idmarcado", idmodificadorSequense, true); // idmodificadorSequense
            insert.setColumn("terminal", "1");
            insert.setColumn("modificador", 1); // uno para este caso por que es comentario xd
            insert.setColumn("idmodificador", 0);
            insert.setColumn("idtypemodificador", 0);
            insert.setColumn("iduser", "3"); // Ponerle un user aquí para el propio sistema
            insert.setColumn("secuencia", SecuenceNormal, true);
            insert.setColumn("borrado", 0);
            insert.setColumn("id_user_borro", 0);
            insert.setColumn("id_user_autorizo", 0);
            insert.setColumn("id_centro_costo", 0);
            insert.setColumn("peso", 0.0);
            insert.setColumn("untaxable", 0);
            insert.setColumn("descripcion2", "");
            insert.setColumn("identificador", "G");
            insert.setColumn("precioinicial", 0.0000);
            insert.setColumn("erp", 0);
            insert.setColumn("monitor", 0.0);
            insert.setColumn("comision", 0);
            insert.setColumn("idhoteldetaservicios", 0);
            insert.setColumn("web", 0);

            CustomPreparedStatement customPreparedStatement = new CustomPreparedStatement(connection,
                    "ventasdiarias.venta_detalle_plus");

            try (PreparedStatement preparedStatement = customPreparedStatement.prepareInsertStatement(
                    insert)) {
                int rowsAffected = preparedStatement.executeUpdate();
                List<Integer> insertedSequences = new ArrayList<>();

                if (rowsAffected > 0) {
                    try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                        while (generatedKeys.next()) {
                            insertId = generatedKeys.getInt(1);
                            insertedSequences.add(SecuenceNormal);
                        }
                    }
                }

                Map<String, Object> result = new HashMap<>();
                result.put("insertId", insertId);
                result.put("insertedSequences", insertedSequences);
                result.put("rowsAffected", rowsAffected);

                resultList.add(result);
            } catch (SQLException e) {
                e.printStackTrace(); // Manejo de la excepción, puedes personalizarlo según tus necesidades
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        System.out.println("Insercion de un comentario");
        System.out.println("id: " + insertId);
        return resultList;
    }

    public static List<Map<String, Object>> insertRowDelivery(int idPlusDelivery, String namePlusDelivery,
            String dateInsert, int idInsertOrder, int idmodificadorSequense, int SecuenceNormal) {
        String price = VentaDetallePlusRepository.getPriceDeliveryById(idPlusDelivery);
        List<Map<String, Object>> resultList = new ArrayList<>();
        Integer insertId = 0;
        try (Connection connection = Conexion.conectarS()) {
            Insert insert = new Insert("ventasdiarias.venta_detalle_plus");

            insert.setColumn("idh", 0);
            insert.setColumn("id_plu", idPlusDelivery, true);
            insert.setColumn("cantidad", 1.0);
            insert.setColumn("precio", price, true);
            insert.setColumn("descuento", 0.0000);
            insert.setColumn("id_umedida", "1");
            insert.setColumn("horatransaccion", dateInsert, true);
            insert.setColumn("propina", 0.000000);
            insert.setColumn("minpreparacion", "0");
            insert.setColumn("minentrega", "0");
            insert.setColumn("descripcion", namePlusDelivery);
            insert.setColumn("tax1", Double.parseDouble(price) * 0.13, true);
            insert.setColumn("tax2", 0.000000);
            insert.setColumn("tax3", 0.000000);
            insert.setColumn("tax4", 0.000000);
            insert.setColumn("tax5", 0.000000);
            insert.setColumn("tax6", 0.000000);
            insert.setColumn("tax7", 0.000000);
            insert.setColumn("tax8", 0.000000);
            insert.setColumn("tax9", 0.000000);
            insert.setColumn("tax10", 0.000000);
            insert.setColumn("hold", 0);
            insert.setColumn("id_enca", idInsertOrder, true);
            insert.setColumn("idmarcado", idmodificadorSequense, true); // idmodificadorSequense
            insert.setColumn("terminal", "1");
            insert.setColumn("modificador", 1); // uno para este caso por que es comentario xd
            insert.setColumn("idmodificador", 0);
            insert.setColumn("idtypemodificador", 0);
            insert.setColumn("iduser", "3"); // Ponerle un user aquí para el propio sistema
            insert.setColumn("secuencia", SecuenceNormal, true);
            insert.setColumn("borrado", 0);
            insert.setColumn("id_user_borro", 0);
            insert.setColumn("id_user_autorizo", 0);
            insert.setColumn("id_centro_costo", 0);
            insert.setColumn("peso", 0.0);
            insert.setColumn("untaxable", 0);
            insert.setColumn("descripcion2", "");
            insert.setColumn("identificador", "G");
            insert.setColumn("precioinicial", price, true);
            insert.setColumn("erp", 0);
            insert.setColumn("monitor", 0.0);
            insert.setColumn("comision", 0);
            insert.setColumn("idhoteldetaservicios", 0);
            insert.setColumn("web", 0);

            CustomPreparedStatement customPreparedStatement = new CustomPreparedStatement(connection,
                    "ventasdiarias.venta_detalle_plus");

            try (PreparedStatement preparedStatement = customPreparedStatement.prepareInsertStatement(
                    insert)) {
                int rowsAffected = preparedStatement.executeUpdate();
                List<Integer> insertedSequences = new ArrayList<>();

                if (rowsAffected > 0) {
                    try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                        while (generatedKeys.next()) {
                            insertId = generatedKeys.getInt(1);
                            insertedSequences.add(SecuenceNormal);
                        }
                    }
                }

                Map<String, Object> result = new HashMap<>();
                result.put("insertId", insertId);
                result.put("insertedSequences", insertedSequences);
                result.put("rowsAffected", rowsAffected);

                resultList.add(result);
            } catch (SQLException e) {
                e.printStackTrace(); // Manejo de la excepción, puedes personalizarlo según tus necesidades
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        System.out.println("Insercion de un envio");
        System.out.println("id: " + insertId);
        return resultList;
    }

    public static String getPriceDeliveryById(int idPlus) {
        String query = "SELECT precio1 FROM silverpos.plus p WHERE P.id BETWEEN 170 AND 177 AND P.activo = 1 AND id = ?";
        String price = null;

        try (Connection connection = Conexion.conectarS();
                PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, idPlus);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    price = resultSet.getString("precio1");
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return price;
    }
    

    public static Double priceDelivery(Integer idDelivery){
        if(idDelivery == -1){
            System.out.println("En efecto, no hay envio, pong");
            return 0.0;
        }
        return Double.parseDouble(getPriceDeliveryById(idDelivery));
    }

    public static Double calcUnitPrice(Double unitPrice) {
        System.out.println("El id es: ");
        System.out.println(decoder.getDelivery());
        Double deliveryPrice = priceDelivery(decoder.getIdDelivery());
        int amountProducts = decoder.getAmountProducts();
    
        if (amountProducts <= 0) {
            return unitPrice;
        }
    
        return unitPrice - (deliveryPrice != 0.0 ? deliveryPrice : 0.0) / amountProducts;
    }
    


    public static void setDecoderData(Decoder decoder){
        VentaDetallePlusRepository.decoder = decoder;
    }
}
