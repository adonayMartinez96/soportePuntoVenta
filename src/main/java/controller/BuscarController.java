package controller;

import Models.FiltrosBusquedas;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class BuscarController {
    FiltrosBusquedas filtrosBusquedas = new FiltrosBusquedas();

    public void buscar(JButton btnBuscar, JTextField txtFechaInicio, JTextField txtFechafin, JTextField txtMotorista, JTextField txtOrden, JTextField txtNombre, JTable tblOrdenes, DefaultTableModel model,JComboBox cbxAnuladas){

        btnBuscar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Object cbxValorPorDefecto =  cbxAnuladas.getSelectedItem();

                Integer anuladas = null;
                if (cbxValorPorDefecto != null) {
                    String valorSeleccionado = cbxValorPorDefecto.toString();
                    if(cbxValorPorDefecto.equals("SI") ){
                        anuladas = 1;
                    }else{
                        anuladas = 0;
                    }

                } else {

                    // No se ha seleccionado nada
                    System.out.println("Nada seleccionado.");
                }



                if(txtFechaInicio.getText().length()>0  && txtFechafin.getText().length()>0  && txtMotorista.getText().length()==0 && txtOrden.getText().length()==0 && txtNombre.getText().length()==0 ) {
                    filtrosBusquedas.ordenesPorFechas(txtFechaInicio, txtFechafin, tblOrdenes,model, anuladas);
                    System.out.println("1 exec");
                }else if(txtFechaInicio.getText().length()>0  && txtFechafin.getText().length()>0  && txtMotorista.getText().length()>0 && txtNombre.getText().length()==0 ){
                    filtrosBusquedas.ordenesPorMotorista(txtFechaInicio, txtFechafin, txtMotorista,tblOrdenes,model);
                    System.out.println("2 exec");
                }else if(txtFechaInicio.getText().length()>0  && txtFechafin.getText().length()>0  && txtMotorista.getText().length()==0 && txtOrden.getText().length()>0 && txtNombre.getText().length()==0 ){

                    filtrosBusquedas.ordenPorNoOrden(txtFechaInicio,txtFechafin,txtOrden,tblOrdenes,model);
                    System.out.println("3 exec");
                }
                else if(txtFechaInicio.getText().length()>0  && txtFechafin.getText().length()>0  && txtMotorista.getText().length()==0 && txtOrden.getText().length()==0 && txtNombre.getText().length()>0 ){
                    filtrosBusquedas.ordenesPorNombre(txtFechaInicio,txtFechafin,tblOrdenes,model,txtNombre);
                    System.out.println("4 exec");
                }



            }
        });
    }

}
