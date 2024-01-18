package Repositories;

import controller.Conexion;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
/* 
 * Esta es la tabla donde se insertan las ventas de cada producto
 * luego que se cree la orden de compra, en esta tabla se guarda
 * los productos asociados a esa compra
 */
public class VentaDetallePlusRepository {

    private static final String INSERT_SALE_QUERY = "-- LUEGO DEL INSERT AL DETALLE ENCABEZADO RECUPERMOS, EL ID DEL ENCA Y ESE ASOCIAMOS A cada linea de detalle plus\n" +
            "INSERT INTO ventasdiarias.venta_detalle_plus\n" +
            "( idh, id_plu, cantidad, precio, \n" +
            "descuento, id_umedida, horatransaccion, propina, minpreparacion,\n" +
            "minentrega, descripcion, tax1, tax2, tax3,\n" +
            "tax4, tax5, tax6, tax7, tax8, \n" +
            "tax9, tax10, hold, id_enca, idmarcado,\n" +
            "terminal, modificador, idmodificador, idtypemodificador, iduser,\n" +
            "secuencia, borrado, id_user_borro, id_user_autorizo, id_centro_costo, \n" +
            "peso, untaxable, descripcion2, identificador, precioinicial, \n" +
            "erp, monitor, comision, idhoteldetaservicios, web)\n" +
            "VALUES( 0, ?, 1.0, ?, -- id_plus(id producto) , -- division de los productos y de eso obtenemos el precio\n" +
            "0.0000, '1', ?, 0.000000, '0',-- recuperar fecha de la orden insertada y ponerle esta\n" +
            "'0', ?, 1.955752, 0.000000, 0.000000,-- traer la descripcion de id_plus y poner esa\n" +
            "0.000000, 0.000000, 0.000000, 0.000000, 0.000000,\n" +
            "0.000000, 0.000000, 0, ?, '2', '1', -- este es el id recuperado de venta encabezado insertado\n" +
            "0, 0, 0, '3', 2, \n" +
            "0, 0, 0, 0, 0.0,\n" +
            "0, '', 'G', ?, 0, -- division de los productos y de eso obtenemos el precio\n" +
            "0, 0.0, 0, 0);";

    public static Integer insertVenta(int idProduct, double unitPrice, String dateOrderInsert, int idOrderInsert, String nameProduct) {
        try (Connection connection = Conexion.conectarS();
             PreparedStatement preparedStatement = connection.prepareStatement(INSERT_SALE_QUERY, PreparedStatement.RETURN_GENERATED_KEYS)) {

            preparedStatement.setInt(1, idProduct);
            preparedStatement.setDouble(2, unitPrice);
            preparedStatement.setString(3, dateOrderInsert);
            preparedStatement.setString(4, nameProduct);
            preparedStatement.setInt(5, idOrderInsert);
            preparedStatement.setDouble(6, unitPrice);

            preparedStatement.executeUpdate();

            try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                } else {
                    throw new SQLException("No se pudo obtener el último ID insertado.");
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }
}
