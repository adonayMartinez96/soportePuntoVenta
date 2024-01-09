package Models;

import controller.Conexion;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FiltrosBusquedas {

    public  void ordenesPorFechas(JTextField fechaInicio, JTextField fechaFinal, JTable tabla, DefaultTableModel  model, Integer anuladas){


        String valor1 = fechaInicio.getText();
        String valor2 = fechaFinal.getText();

        try{
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date fecha1 =  sdf.parse(valor1);
            Date fecha2 = sdf.parse(valor2);

            String ventasPorFechas = "select \n" +
                    "\tconcat(diaria_encabezado.num_doc,'-', diaria_encabezado.no_orden) as ORDEN,\n" +
                    "\tdiaria_encabezado.fechatransaccion as fecha_ingreso,\n" +
                    "\tdiaria_encabezado.hora_cerro as hora_cerro,\n" +
                    "\tdiaria_encabezado.cliente_domicilio as NOMBRE,\n" +
                    "\tdiaria_encabezado.observacion as TELEFONO,\n" +
                    "\tSUBSTRING_INDEX(diaria_encabezado.direccion_domicilio, '-', -1) as DIRECCION,\n" +
                    "\tSUBSTRING_INDEX(diaria_encabezado.direccion_domicilio, '-', 1) as CIUDAD,\n" +
                    "\tSUBSTRING_INDEX(SUBSTRING_INDEX(diaria_encabezado.direccion_domicilio, '-', 2),'-',-1) DEPARTAMENTO,\n" +
                    "\t(select m.nombre  from silverpos.motoristas m where diaria_encabezado.idmotorista = m.id) as motorista,\n" +
                    "\tSUM(vdp.precioinicial * vdp.cantidad - vdp.descuento) AS VALOR_DECLARADO,\n" +
                    "\t(case  when diaria_encabezado.borrada = 0 then 'NO' when diaria_encabezado.borrada = 1 then 'SI' end) as borrada,\n" +
                    "\t(case  when diaria_encabezado.anulado = 0 then 'NO' when diaria_encabezado.anulado = 1 then 'SI' end) as anulada,\n" +
                    "\t(case WHEN RP.fecha_registro_pago IS NOT NULL THEN 'SI' ELSE 'NO' END) AS PAGADA\n" +
                    "from ventasdiarias.venta_encabezado diaria_encabezado\n" +
                    "inner join ventasdiarias.venta_detalle_plus vdp  on\n" +
                    "diaria_encabezado.id =vdp.id_enca\n" +
                    "left join silverpos_hist.registro_pagos rp on \n" +
                    "SUBSTRING_INDEX(rp.no_orden, '-', 1) = diaria_encabezado.num_doc AND \n" +
                    "SUBSTRING_INDEX(rp.no_orden, '-', -1) = diaria_encabezado.no_orden \n" +
                    "where diaria_encabezado.anulado = "+anuladas+" and  \n" +
                    "DATE_FORMAT(diaria_encabezado.hora_cerro , '%Y-%m-%d %H:%i:%s')  \n" +
                    "BETWEEN '"+sdf.format(fecha1) +"'\n" +
                    "AND '"+sdf.format(fecha2) +"'\n" +
                    "\tgroup by diaria_encabezado.id,RP.fecha_registro_pago \n" +
                    "union all \n" +
                    "select \n" +
                    "\tconcat(hve.num_doc,'-', hve.no_orden) as ORDEN,\n" +
                    "\thve.fechatransaccion as fecha_ingreso,\n" +
                    "\thve.hora_cerro as hora_cerro,\n" +
                    "\thve.cliente_domicilio as NOMBRE,\n" +
                    "\thve.observacion as TELEFONO,\n" +
                    "\tSUBSTRING_INDEX(hve.direccion_domicilio, '-', -1) as DIRECCION,\n" +
                    "\tSUBSTRING_INDEX(hve.direccion_domicilio, '-', 1) as CIUDAD,\n" +
                    "\tSUBSTRING_INDEX(SUBSTRING_INDEX(hve.direccion_domicilio, '-', 2),'-',-1) DEPARTAMENTO,\n" +
                    "\t(select m.nombre  from silverpos.motoristas m where hve.idmotorista = m.id) ,\n" +
                    "\tSUM(hvdp.precioinicial * hvdp.cantidad - hvdp.descuento) AS VALOR_DECLARADO,\n" +
                    "\t(case  when hve.borrada = 0 then 'NO' when hve.borrada = 1 then 'SI' end) as borrada,\n" +
                    "\t(case  when hve.anulado = 0 then 'NO' when hve.anulado = 1 then 'SI' end) as anulada,\n" +
                    "\t(case WHEN RP.fecha_registro_pago IS NOT NULL THEN 'SI' ELSE 'NO' END) AS PAGADA\n" +
                    "from silverpos_hist.hist_venta_enca hve \n" +
                    "inner join silverpos_hist.hist_venta_deta_plus hvdp on\n" +
                    "hve.id = hvdp.id_enca\n" +
                    "left join silverpos_hist.registro_pagos rp on \n" +
                    "SUBSTRING_INDEX(rp.no_orden, '-', 1) = hve.num_doc AND \n" +
                    "SUBSTRING_INDEX(rp.no_orden, '-', -1) = hve.no_orden \n" +
                    "where hve.anulado ="+anuladas+" and  \n" +
                    "DATE_FORMAT(hve.hora_cerro , '%Y-%m-%d %H:%i:%s')  \n" +
                    "BETWEEN '"+sdf.format(fecha1) +"'\n" +
                    "AND '"+sdf.format(fecha2) +"'\n" +
                    "group by hve.id,RP.fecha_registro_pago order by fecha_ingreso desc";

            Conexion con = new Conexion();
            Connection conexionMysql  = con.conectar();

            PreparedStatement st = conexionMysql.prepareStatement(ventasPorFechas);

            ResultSet rs = st.executeQuery();

            model.setRowCount(0);

            while (rs.next()) {
                String orden = rs.getString("orden");
                String fecha = rs.getString("fecha_ingreso");
                String hora_cerro = rs.getString("hora_cerro");
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

                model.addRow(new Object[]{orden,hora_cerro,nombre, telefono,direccion,ciudad,departamento,motorista,valorFinal,borrada,anulada,pagada});
            }
            rs.close();;
            st.close();


        }
        catch ( SQLException  | ParseException e){
            e.printStackTrace();
        }

        tabla.setModel(model);
    }

    public void ordenesPorMotorista(JTextField fechaInicio, JTextField fechaFinal,JTextField motoristaBuscar, JTable tabla, DefaultTableModel model){


        String valor1 = fechaInicio.getText();
        String valor2 = fechaFinal.getText();
        String idMotorista = motoristaBuscar.getText();

        try{
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date fecha1 = sdf.parse(valor1);
            Date fecha2 = sdf.parse(valor2);

            String ventasPorFechas = "select \n" +
                    "\tconcat(diaria_encabezado.num_doc,'-', diaria_encabezado.no_orden) as ORDEN,\n" +
                    "\tdiaria_encabezado.fechatransaccion as fecha_ingreso,\n" +
                    "\tdiaria_encabezado.hora_cerro as hora_cerro,\n" +
                    "\tdiaria_encabezado.cliente_domicilio as NOMBRE,\n" +
                    "\tdiaria_encabezado.observacion as TELEFONO,\n" +
                    "\tSUBSTRING_INDEX(diaria_encabezado.direccion_domicilio, '-', -1) as DIRECCION,\n" +
                    "\tSUBSTRING_INDEX(diaria_encabezado.direccion_domicilio, '-', 1) as CIUDAD,\n" +
                    "\tSUBSTRING_INDEX(SUBSTRING_INDEX(diaria_encabezado.direccion_domicilio, '-', 2),'-',-1) DEPARTAMENTO,\n" +
                    "\t(select m.nombre  from silverpos.motoristas m where diaria_encabezado.idmotorista = m.id) as motorista,\n" +
                    "\tSUM(vdp.precioinicial * vdp.cantidad - vdp.descuento) AS VALOR_DECLARADO,\n" +
                    "\t(case  when diaria_encabezado.borrada = 0 then 'NO' when diaria_encabezado.borrada = 1 then 'SI' end) as borrada,\n" +
                    "\t(case  when diaria_encabezado.anulado = 0 then 'NO' when diaria_encabezado.anulado = 1 then 'SI' end) as anulada,\n" +
                    "\t(case WHEN RP.fecha_registro_pago IS NOT NULL THEN 'SI' ELSE 'NO' END) AS PAGADA\n" +
                    "from ventasdiarias.venta_encabezado diaria_encabezado\n" +
                    "inner join ventasdiarias.venta_detalle_plus vdp  on\n" +
                    "diaria_encabezado.id =vdp.id_enca\n" +
                    "left join silverpos_hist.registro_pagos rp on \n" +
                    "SUBSTRING_INDEX(rp.no_orden, '-', 1) = diaria_encabezado.num_doc AND \n" +
                    "SUBSTRING_INDEX(rp.no_orden, '-', -1) = diaria_encabezado.no_orden \n" +
                    "where diaria_encabezado.idmotorista ="+idMotorista+" and \n"+
                    "DATE_FORMAT(diaria_encabezado.hora_cerro , '%Y-%m-%d %H:%i:%s')  \n" +
                    "BETWEEN '"+sdf.format(fecha1) +"'\n" +
                    "AND '"+sdf.format(fecha2) +"'\n" +
                    "group by diaria_encabezado.id,RP.fecha_registro_pago\n" +
                    "union all \n" +
                    "select \n" +
                    "\tconcat(hve.num_doc,'-', hve.no_orden) as ORDEN,\n" +
                    "\thve.fechatransaccion as fecha_ingreso,\n" +
                    "\thve.hora_cerro as hora_cerro,\n" +
                    "\thve.cliente_domicilio as NOMBRE,\n" +
                    "\thve.observacion as TELEFONO,\n" +
                    "\tSUBSTRING_INDEX(hve.direccion_domicilio, '-', -1) as DIRECCION,\n" +
                    "\tSUBSTRING_INDEX(hve.direccion_domicilio, '-', 1) as CIUDAD,\n" +
                    "\tSUBSTRING_INDEX(SUBSTRING_INDEX(hve.direccion_domicilio, '-', 2),'-',-1) DEPARTAMENTO,\n" +
                    "\t(select m.nombre  from silverpos.motoristas m where hve.idmotorista = m.id),\n" +
                    "\tSUM(hvdp.precioinicial * hvdp.cantidad - hvdp.descuento) AS VALOR_DECLARADO,\n" +
                    "\t(case  when hve.borrada = 0 then 'NO' when hve.borrada = 1 then 'SI' end) as borrada,\n" +
                    "\t(case  when hve.anulado = 0 then 'NO' when hve.anulado = 1 then 'SI' end) as anulada,\n" +
                    "\t(case WHEN RP.fecha_registro_pago IS NOT NULL THEN 'SI' ELSE 'NO' END) AS PAGADA\n" +
                    "from silverpos_hist.hist_venta_enca hve \n" +
                    "inner join silverpos_hist.hist_venta_deta_plus hvdp on\n" +
                    "hve.id = hvdp.id_enca\n" +
                    "left join silverpos_hist.registro_pagos rp on \n" +
                    "SUBSTRING_INDEX(rp.no_orden, '-', 1) = hve.num_doc AND \n" +
                    "SUBSTRING_INDEX(rp.no_orden, '-', -1) = hve.no_orden \n" +
                    "where hve.idmotorista ="+idMotorista+" and \n"+
                    "DATE_FORMAT(hve.hora_cerro , '%Y-%m-%d %H:%i:%s')  \n" +
                    "BETWEEN '"+sdf.format(fecha1) +"'\n" +
                    "AND '"+sdf.format(fecha2) +"'\n" +
                    "group by hve.id,RP.fecha_registro_pago\n"+
                    "order by fecha_ingreso desc";

            Conexion con = new Conexion();
            Connection conexionMysql  = con.conectar();

            PreparedStatement st = conexionMysql.prepareStatement(ventasPorFechas);

            ResultSet rs = st.executeQuery();

            model.setRowCount(0);

            while (rs.next()) {
                String orden = rs.getString("orden");
                String fecha = rs.getString("fecha_ingreso");
                String hora_cerro = rs.getString("hora_cerro");
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

                model.addRow(new Object[]{orden, fecha,nombre, telefono,direccion,ciudad,departamento,motorista,valorFinal,borrada,anulada,pagada});
            }
            rs.close();;
            st.close();


        }
        catch ( SQLException  | ParseException e){
            e.printStackTrace();
        }

        tabla.setModel(model);
    }

    public void ordenPorNoOrden(JTextField fechaInicio, JTextField fechaFinal,JTextField noOrden, JTable tabla,DefaultTableModel model){


        String valor1 = fechaInicio.getText();
        String valor2 = fechaFinal.getText();
        int ordenEncontrada = Integer.parseInt(noOrden.getText());

        try{
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date fecha1 = sdf.parse(valor1);
            Date fecha2 = sdf.parse(valor2);

            String ventasPorFechas = "select \n" +
                    "\tconcat(diaria_encabezado.num_doc,'-', diaria_encabezado.no_orden) as ORDEN,\n" +
                    "\tdiaria_encabezado.fechatransaccion as fecha_ingreso,\n" +
                    "\tdiaria_encabezado.hora_cerro as hora_cerro,\n" +
                    "\tdiaria_encabezado.cliente_domicilio as NOMBRE,\n" +
                    "\tdiaria_encabezado.observacion as TELEFONO,\n" +
                    "\tSUBSTRING_INDEX(diaria_encabezado.direccion_domicilio, '-', -1) as DIRECCION,\n" +
                    "\tSUBSTRING_INDEX(diaria_encabezado.direccion_domicilio, '-', 1) as CIUDAD,\n" +
                    "\tSUBSTRING_INDEX(SUBSTRING_INDEX(diaria_encabezado.direccion_domicilio, '-', 2),'-',-1) DEPARTAMENTO,\n" +
                    "\t(select m.nombre  from silverpos.motoristas m where diaria_encabezado.idmotorista = m.id) as motorista,\n" +
                    "\tSUM(vdp.precioinicial * vdp.cantidad - vdp.descuento) AS VALOR_DECLARADO,\n" +
                    "\t(case  when diaria_encabezado.borrada = 0 then 'NO' when diaria_encabezado.borrada = 1 then 'SI' end) as borrada,\n" +
                    "\t(case  when diaria_encabezado.anulado = 0 then 'NO' when diaria_encabezado.anulado = 1 then 'SI' end) as anulada,\n" +
                    "\t(case WHEN RP.fecha_registro_pago IS NOT NULL THEN 'SI' ELSE 'NO' END) AS PAGADA\n" +
                    "from ventasdiarias.venta_encabezado diaria_encabezado\n" +
                    "inner join ventasdiarias.venta_detalle_plus vdp  on\n" +
                    "diaria_encabezado.id =vdp.id_enca\n" +
                    "left join silverpos_hist.registro_pagos rp on \n" +
                    "SUBSTRING_INDEX(rp.no_orden, '-', 1) = diaria_encabezado.num_doc AND \n" +
                    "SUBSTRING_INDEX(rp.no_orden, '-', -1) = diaria_encabezado.no_orden \n" +
                    "where diaria_encabezado.no_orden ="+ordenEncontrada+" and \n"+
                    "DATE_FORMAT(diaria_encabezado.hora_cerro , '%Y-%m-%d %H:%i:%s')  \n" +
                    "BETWEEN '"+sdf.format(fecha1) +"'\n" +
                    "AND '"+sdf.format(fecha2) +"'\n" +
                    "group by diaria_encabezado.id,RP.fecha_registro_pago\n" +
                    "union all \n" +
                    "select \n" +
                    "\tconcat(hve.num_doc,'-', hve.no_orden) as ORDEN,\n" +
                    "\t(hve.fechatransaccion) as fecha_ingreso,\n" +
                    "\thve.hora_cerro as hora_cerro,\n" +
                    "\thve.cliente_domicilio as NOMBRE,\n" +
                    "\thve.observacion as TELEFONO,\n" +
                    "\tSUBSTRING_INDEX(hve.direccion_domicilio, '-', -1) as DIRECCION,\n" +
                    "\tSUBSTRING_INDEX(hve.direccion_domicilio, '-', 1) as CIUDAD,\n" +
                    "\tSUBSTRING_INDEX(SUBSTRING_INDEX(hve.direccion_domicilio, '-', 2),'-',-1) DEPARTAMENTO,\n" +
                    "\t(select m.nombre  from silverpos.motoristas m where hve.idmotorista = m.id),\n" +
                    "\tSUM(hvdp.precioinicial * hvdp.cantidad - hvdp.descuento) AS VALOR_DECLARADO,\n" +
                    "\t(case  when hve.borrada = 0 then 'NO' when hve.borrada = 1 then 'SI' end) as borrada,\n" +
                    "\t(case  when hve.anulado = 0 then 'NO' when hve.anulado = 1 then 'SI' end) as anulada,\n" +
                    "\t(case WHEN RP.fecha_registro_pago IS NOT NULL THEN 'SI' ELSE 'NO' END) AS PAGADA\n" +
                    "from silverpos_hist.hist_venta_enca hve \n" +
                    "inner join silverpos_hist.hist_venta_deta_plus hvdp on\n" +
                    "hve.id = hvdp.id_enca\n" +
                    "left join silverpos_hist.registro_pagos rp on \n" +
                    "SUBSTRING_INDEX(rp.no_orden, '-', 1) = hve.num_doc AND \n" +
                    "SUBSTRING_INDEX(rp.no_orden, '-', -1) = hve.no_orden \n" +
                    "where hve.no_orden ="+ordenEncontrada+" and \n"+
                    "DATE_FORMAT(hve.hora_cerro , '%Y-%m-%d %H:%i:%s')  \n" +
                    "BETWEEN '"+sdf.format(fecha1) +"'\n" +
                    "AND '"+sdf.format(fecha2) +"'\n" +
                    "group by hve.id,RP.fecha_registro_pago\n"+
                    "order by fecha_ingreso desc";

            Conexion con = new Conexion();
            Connection conexionMysql  = con.conectar();

            PreparedStatement st = conexionMysql.prepareStatement(ventasPorFechas);

            ResultSet rs = st.executeQuery();

            model.setRowCount(0);

            while (rs.next()) {
                String orden = rs.getString("orden");
                String fecha = rs.getString("fecha_ingreso");
                String hora_cerro = rs.getString("hora_cerro");
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

                model.addRow(new Object[]{orden, fecha, nombre, telefono,direccion,ciudad,departamento,motorista,valorFinal,borrada,anulada,pagada});
            }
            rs.close();;
            st.close();


        }
        catch ( SQLException  | ParseException e){
            e.printStackTrace();
        }

        tabla.setModel(model);
    }

    public void cargarTablaPrincipal(DefaultTableModel model, JTable tblOrdenes){

            try{
                String ventasDiarias = "select \n" +
                        "\t\tconcat(diaria_encabezado.num_doc,'-', diaria_encabezado.no_orden) as orden,\n" +
                        "\t(diaria_encabezado.fechatransaccion) as fecha_ingreso,\n" +
                        "\tdiaria_encabezado.hora_cerro as hora_cerro,\n" +
                        "\tdiaria_encabezado.cliente_domicilio as NOMBRE,\n" +
                        "\tdiaria_encabezado.observacion as TELEFONO,\n" +
                        "\tSUBSTRING_INDEX(diaria_encabezado.direccion_domicilio, '-', -1) as direccion,\n" +
                        "\tSUBSTRING_INDEX(diaria_encabezado.direccion_domicilio, '-', 1) as ciudad,\n" +
                        "\tSUBSTRING_INDEX(SUBSTRING_INDEX(diaria_encabezado.direccion_domicilio, '-', 2),'-',-1) departamento,\n" +
                        "\t(select m.nombre  from silverpos.motoristas m where diaria_encabezado.idmotorista = m.id) as motorista,\n" +
                        "\tSUM(vdp.precioinicial * vdp.cantidad - vdp.descuento) AS valor_declarado,\n" +
                        "\t(case  when diaria_encabezado.borrada = 0 then 'NO' when diaria_encabezado.borrada = 1 then 'SI' end) as borrada,\n" +
                        "\t(case  when diaria_encabezado.anulado = 0 then 'NO' when diaria_encabezado.anulado = 1 then 'SI' end) as anulada\n" +
                        "from ventasdiarias.venta_encabezado diaria_encabezado\n" +
                        "inner join ventasdiarias.venta_detalle_plus vdp  on\n" +
                        "diaria_encabezado.id =vdp.id_enca\n" +
                        "group by diaria_encabezado.id \n"+
                        "order by diaria_encabezado.hora_cerro desc \n";

                Conexion con = new Conexion();
                Connection conexionMysql  = con.conectar();

                Statement st = conexionMysql.createStatement();
                ResultSet rs = st.executeQuery(ventasDiarias);

                model.setRowCount(0);
                while (rs.next()) {
                    String orden = rs.getString("orden");
                    String fecha = rs.getString("fecha_ingreso");
                    String hora_cerro = rs.getString("hora_cerro");
                    String nombre = rs.getString("nombre");
                    String telefono = rs.getString("telefono");
                    String direccion = rs.getString("direccion");
                    String ciudad = rs.getString("ciudad");
                    String departamento = rs.getString("departamento");
                    String motorista = rs.getString("motorista");
                    String valor_declarado = rs.getString("valor_declarado");
                    String borrada = rs.getString("borrada");
                    String anulada = rs.getString("anulada");
                    String valorFinal = "$"+valor_declarado;

                    System.out.println("estamos en ac "+orden + fecha);
                    model.addRow(new Object[]{orden, fecha, nombre, telefono,direccion,ciudad,departamento,motorista,valorFinal,borrada,anulada});
                }
                rs.close();;
                st.close();


            }
            catch ( SQLException i){
                i.printStackTrace();
            }

            tblOrdenes.setModel(model);

    }

    public  void ordenesPorNombre(JTextField fechaInicio, JTextField fechaFinal, JTable tabla, DefaultTableModel  model, JTextField txtNombre){


        String valor1 = fechaInicio.getText();
        String valor2 = fechaFinal.getText();
        String nombreBuscar = txtNombre.getText();

        try{
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date fecha1 =  sdf.parse(valor1);
            Date fecha2 = sdf.parse(valor2);

            String ventasPorFechas = "select \n" +
                    "\tconcat(diaria_encabezado.num_doc,'-', diaria_encabezado.no_orden) as ORDEN,\n" +
                    "\tdiaria_encabezado.fechatransaccion as fecha_ingreso,\n" +
                    "\tdiaria_encabezado.hora_cerro as hora_cerro,\n" +
                    "\tdiaria_encabezado.cliente_domicilio as NOMBRE,\n" +
                    "\tdiaria_encabezado.observacion as TELEFONO,\n" +
                    "\tSUBSTRING_INDEX(diaria_encabezado.direccion_domicilio, '-', -1) as DIRECCION,\n" +
                    "\tSUBSTRING_INDEX(diaria_encabezado.direccion_domicilio, '-', 1) as CIUDAD,\n" +
                    "\tSUBSTRING_INDEX(SUBSTRING_INDEX(diaria_encabezado.direccion_domicilio, '-', 2),'-',-1) DEPARTAMENTO,\n" +
                    "\t(select m.nombre  from silverpos.motoristas m where diaria_encabezado.idmotorista = m.id) as motorista,\n" +
                    "\tSUM(vdp.precioinicial * vdp.cantidad - vdp.descuento) AS VALOR_DECLARADO,\n" +
                    "\t(case  when diaria_encabezado.borrada = 0 then 'NO' when diaria_encabezado.borrada = 1 then 'SI' end) as borrada,\n" +
                    "\t(case  when diaria_encabezado.anulado = 0 then 'NO' when diaria_encabezado.anulado = 1 then 'SI' end) as anulada,\n" +
                    "\t(case WHEN RP.fecha_registro_pago IS NOT NULL THEN 'SI' ELSE 'NO' END) AS PAGADA\n" +
                    "from ventasdiarias.venta_encabezado diaria_encabezado\n" +
                    "inner join ventasdiarias.venta_detalle_plus vdp  on\n" +
                    "diaria_encabezado.id =vdp.id_enca\n" +
                    "left join silverpos_hist.registro_pagos rp on \n" +
                    "SUBSTRING_INDEX(rp.no_orden, '-', 1) = diaria_encabezado.num_doc AND \n" +
                    "SUBSTRING_INDEX(rp.no_orden, '-', -1) = diaria_encabezado.no_orden \n" +
                    "where diaria_encabezado.cliente_domicilio like '%"+nombreBuscar+"%' and  \n" +
                    "DATE_FORMAT(diaria_encabezado.hora_cerro , '%Y-%m-%d %H:%i:%s')  \n" +
                    "BETWEEN '"+sdf.format(fecha1) +"'\n" +
                    "AND '"+sdf.format(fecha2) +"'\n" +
                    "\tgroup by diaria_encabezado.id,RP.fecha_registro_pago \n" +
                    "union all \n" +
                    "select \n" +
                    "\tconcat(hve.num_doc,'-', hve.no_orden) as ORDEN,\n" +
                    "\thve.fechatransaccion as fecha_ingreso,\n" +
                    "\thve.hora_cerro as hora_cerro,\n" +
                    "\thve.cliente_domicilio as NOMBRE,\n" +
                    "\thve.observacion as TELEFONO,\n" +
                    "\tSUBSTRING_INDEX(hve.direccion_domicilio, '-', -1) as DIRECCION,\n" +
                    "\tSUBSTRING_INDEX(hve.direccion_domicilio, '-', 1) as CIUDAD,\n" +
                    "\tSUBSTRING_INDEX(SUBSTRING_INDEX(hve.direccion_domicilio, '-', 2),'-',-1) DEPARTAMENTO,\n" +
                    "\t(select m.nombre  from silverpos.motoristas m where hve.idmotorista = m.id) ,\n" +
                    "\tSUM(hvdp.precioinicial * hvdp.cantidad - hvdp.descuento) AS VALOR_DECLARADO,\n" +
                    "\t(case  when hve.borrada = 0 then 'NO' when hve.borrada = 1 then 'SI' end) as borrada,\n" +
                    "\t(case  when hve.anulado = 0 then 'NO' when hve.anulado = 1 then 'SI' end) as anulada,\n" +
                    "\t(case WHEN RP.fecha_registro_pago IS NOT NULL THEN 'SI' ELSE 'NO' END) AS PAGADA\n" +
                    "from silverpos_hist.hist_venta_enca hve \n" +
                    "inner join silverpos_hist.hist_venta_deta_plus hvdp on\n" +
                    "hve.id = hvdp.id_enca\n" +
                    "left join silverpos_hist.registro_pagos rp on \n" +
                    "SUBSTRING_INDEX(rp.no_orden, '-', 1) = hve.num_doc AND \n" +
                    "SUBSTRING_INDEX(rp.no_orden, '-', -1) = hve.no_orden \n" +
                    "where hve.cliente_domicilio like '%"+nombreBuscar+"%' and  \n" +
                    "DATE_FORMAT(hve.hora_cerro , '%Y-%m-%d %H:%i:%s')  \n" +
                    "BETWEEN '"+sdf.format(fecha1) +"'\n" +
                    "AND '"+sdf.format(fecha2) +"'\n" +
                    "group by hve.id,RP.fecha_registro_pago order by fecha_ingreso desc";

            Conexion con = new Conexion();
            Connection conexionMysql  = con.conectar();

            PreparedStatement st = conexionMysql.prepareStatement(ventasPorFechas);

            ResultSet rs = st.executeQuery();

            model.setRowCount(0);

            while (rs.next()) {
                String orden = rs.getString("orden");
                String fecha = rs.getString("fecha_ingreso");
                String hora_cerro = rs.getString("hora_cerro");
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

                model.addRow(new Object[]{orden,hora_cerro,nombre, telefono,direccion,ciudad,departamento,motorista,valorFinal,borrada,anulada,pagada});
            }
            rs.close();;
            st.close();


        }
        catch ( SQLException  | ParseException e){
            e.printStackTrace();
        }

        tabla.setModel(model);
    }
}
