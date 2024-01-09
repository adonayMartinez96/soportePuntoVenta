package controller;

import Models.FiltrosBusquedas;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ActualizarController {

    FiltrosBusquedas filtrosBusquedas = new FiltrosBusquedas();
    CargaPorDefecto cargaPorDefecto = new CargaPorDefecto();

    public void actualizar(JButton btnActualizar, DefaultTableModel model, JTable tblOrdenes, JTextField campo1,JTextField campo2){

        btnActualizar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cargaPorDefecto.setCamposFehas(campo1,campo2);
                //filtrosBusquedas.cargarTablaPrincipal(model,tblOrdenes);
                cargaPorDefecto.loadOrdenes(tblOrdenes,model);
            }

        });
    }
}
