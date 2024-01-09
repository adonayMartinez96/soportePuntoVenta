package controller;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class CargaPorDefecto {

    public void loadOrdenes(JTable tblOrdenes, DefaultTableModel model){
        try{
            String ventasDiarias = "select \n" +
                    "\t\tconcat(diaria_encabezado.num_doc,'-', diaria_encabezado.no_orden) as orden,\n" +
                    "\tdiaria_encabezado.hora_cerro AS hora_cerro,\n" +
                    "\t(diaria_encabezado.fechatransaccion) as fecha_ingreso,\n" +
                    "\tdiaria_encabezado.cliente_domicilio as NOMBRE,\n" +
                    "\tdiaria_encabezado.observacion as TELEFONO,\n" +
                    "\tSUBSTRING_INDEX(diaria_encabezado.direccion_domicilio, '-', -1) as direccion,\n" +
                    "\tSUBSTRING_INDEX(diaria_encabezado.direccion_domicilio, '-', 1) as ciudad,\n" +
                    "\tSUBSTRING_INDEX(SUBSTRING_INDEX(diaria_encabezado.direccion_domicilio, '-', 2),'-',-1) departamento,\n" +
                    "\t(select m.nombre  from silverpos.motoristas m where diaria_encabezado.idmotorista = m.id) as motorista,\n" +
                    "\tSUM(vdp.precioinicial * vdp.cantidad - vdp.descuento) AS valor_declarado,\n" +
                    "\t(case  when diaria_encabezado.borrada = 0 then 'NO' when diaria_encabezado.borrada = 1 then 'SI' end) as borrada,\n" +
                    "\t(case  when diaria_encabezado.anulado = 0 then 'NO' when diaria_encabezado.anulado = 1 then 'SI' end) as anulada,\n" +
                    "\t(case WHEN RP.fecha_registro_pago IS NOT NULL THEN 'SI' ELSE 'NO' END) AS PAGADA\n" +
                    "from ventasdiarias.venta_encabezado diaria_encabezado\n" +
                    "inner join ventasdiarias.venta_detalle_plus vdp  on\n" +
                    "diaria_encabezado.id =vdp.id_enca\n" +
                    "left join silverpos_hist.registro_pagos rp on \n" +
                    "SUBSTRING_INDEX(rp.no_orden, '-', 1) = diaria_encabezado.num_doc AND \n" +
                    "SUBSTRING_INDEX(rp.no_orden, '-', -1) = diaria_encabezado.no_orden \n" +
                    "group by diaria_encabezado.id,RP.fecha_registro_pago \n"+
                    "order by diaria_encabezado.hora_cerro desc \n";

            Conexion con = new Conexion();
            Connection conexionMysql  = con.conectar();

            Statement st = conexionMysql.createStatement();
            ResultSet rs = st.executeQuery(ventasDiarias);

          /*  model.addColumn("ORDEN");
            model.addColumn("HORA_ASIGNACION");
            model.addColumn("NOMBRE");
            model.addColumn("TELEFONO");
            model.addColumn("CIUDAD");
            model.addColumn("DIRECCION");
            model.addColumn("DEPARTAMENTO");
            model.addColumn("MOTORISTA");
            model.addColumn("VALOR_DECLARADO");
            model.addColumn("BORRADO");
            model.addColumn("ANULADA");
*/
            model.setRowCount(0);
            while (rs.next()) {
                String orden = rs.getString("orden");

                String hora_asignacion = rs.getString("hora_cerro");
                String nombre = rs.getString("nombre");
                String telefono = rs.getString("telefono");
                String direccion = rs.getString("direccion");
                String ciudad = rs.getString("ciudad");
                String departamento = rs.getString("departamento");
                String motorista = rs.getString("motorista");
                String valor_declarado = rs.getString("valor_declarado");
                String borrada = rs.getString("borrada");
                String anulada = rs.getString("anulada");
                String pagada = rs.getString("PAGADA");
                String valorFinal = "$"+valor_declarado;

                model.addRow(new Object[]{orden,hora_asignacion,nombre, telefono,direccion,ciudad,departamento,motorista,valorFinal,borrada,anulada,pagada});
            }
            rs.close();;
            st.close();

        }
        catch ( SQLException e){
            e.printStackTrace();
        }

        tblOrdenes.setModel(model);
    }

    public void setCamposFehas(JTextField txtFechaInicio,JTextField txtFechafin){
        LocalDateTime fecha1 = LocalDateTime.now();
        LocalDateTime fecha2 = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime fechaInicio1 = fecha1.withHour(03).withMinute(00).withSecond(0).withNano(0);
        LocalDateTime fechaFinal2 = fecha2.withHour(23).withMinute(59).withSecond(0).withNano(0);

        String fecha1Final = fechaInicio1.format(formatter);
        String fecha2Final = fechaFinal2.format(formatter);

        txtFechaInicio.setText(fecha1Final);
        txtFechafin.setText(fecha2Final);
    }

}
