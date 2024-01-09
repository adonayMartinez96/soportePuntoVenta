package controller;
import Models.OrdenSingleton;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


public class AnularOrdenController {

    public void anularOrden(JTable table, OrdenSingleton orden,JTextField txtComentario,JButton btnAnularOrden){

        String queryAnularHistorial ="update silverpos_hist.hist_venta_enca set \n" +
                "                 anulado=1,\n" +
                "                 hora_asignacion_motorista='0001-01-01 00:00:00',\n" +
                "                 idmotorista = 0,\n" +
                "                 razon_anulado = ?\n" +
                "                 where no_orden = ? and cliente_domicilio = ?";

        String queryAnularVentaDiaria = "update ventasdiarias.venta_encabezado  set \n" +
                "                 anulado=1,\n" +
                "                 hora_asignacion_motorista='0001-01-01 00:00:00',\n" +
                "                 idmotorista = 0,\n" +
                "                 razon_anulado = ?\n" +
                "                 where no_orden = ? and cliente_domicilio = ?";

        String queryVentasDiarias = "select count(*) as contador from ventasdiarias.venta_encabezado ve \n" +
                "where ve.no_orden = ?  and \n" +
                "ve.cliente_domicilio = ?";

        String historialQueryVentasDiarias = "select count(*) as contador from silverpos_hist.hist_venta_enca hve where \n" +
                "hve.no_orden  = ? and hve.cliente_domicilio = ?; ";

        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {

                if (e.getClickCount() == 1) {
                    int filaSeleccionada = table.getSelectedRow();

                    String ordenTable = orden.getOrden();
                    String[] pairs = ordenTable.split("-");
                    String ordenNeta ="";

                    if(pairs.length == 2){
                        ordenNeta = pairs[1];
                    }

                    System.out.println(ordenNeta);



                }else {
                    System.out.println("El usuario eligió No o cerró el cuadro de diálogo.");
                }
            }


        });

        btnAnularOrden.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                int opcion = JOptionPane.showConfirmDialog(null,
                        "¿Deseas anular la siguiente orden: " +
                                "Numero de Orden: "+orden.getOrden()+" ?\n"+
                                "Cliente: "+orden.getNombre()+" ?\n"+
                                "Telefono: "+orden.getTelefono()+" ?\n"+
                                "Total: "+orden.getValorDeclarado()+" ?\n"
                        , "Confirmación", JOptionPane.YES_NO_OPTION);

                if (opcion == JOptionPane.YES_OPTION) {
                    String ordenTable = orden.getOrden();
                    String[] pairs = ordenTable.split("-");
                    String ordenNeta ="";

                    if(pairs.length == 2){
                        ordenNeta = pairs[1];
                    }

                    System.out.println("El usuario eligió Sí.");
                    Conexion con = new Conexion();
                    Connection mysql = con.conectar();
                    ResultSet rs = null;

                    try {
                        PreparedStatement st = mysql.prepareStatement(queryVentasDiarias);
                        st.setString(1,ordenNeta);
                        st.setString(2,orden.getNombre());
                        rs = st.executeQuery();

                        if (rs.next()) {
                            int contador = rs.getInt("contador");
                            int resultado = (contador > 0) ? 1 : 0;
                            System.out.println("Resultado: " + resultado);

                            if(resultado==1){
                                String comentario= txtComentario.getText(); //!= null? txtComentario.getText().toString(): "-";

                                PreparedStatement anulacionDiaria = mysql.prepareStatement(queryAnularVentaDiaria);

                                anulacionDiaria.setString(1,comentario);
                                anulacionDiaria.setString(2,ordenNeta);
                                anulacionDiaria.setString(3,orden.getNombre());
                                int numero = anulacionDiaria.executeUpdate();


                                System.out.println("actualizacion realizada " +numero);
                            }else  if(resultado ==0){
                                String comentario= txtComentario.getText() ;// != null? txtComentario.getText().toString(): "-";

                                PreparedStatement anulacionHistorico = mysql.prepareStatement(queryAnularHistorial);

                                anulacionHistorico.setString(1,comentario);
                                anulacionHistorico.setString(2,ordenNeta);
                                anulacionHistorico.setString(3,orden.getNombre());
                                int numero = anulacionHistorico.executeUpdate();


                                System.out.println("actualizacion realizada " +numero);
                            }

                        } else {
                            System.out.println("No se encontraron resultados.");
                        }
                    } catch (SQLException throwables) {
                        throwables.printStackTrace();
                    }
                }else {
                    System.out.println("El usuario eligió No o cerró el cuadro de diálogo.");
                }

            }
        });



    }


}
