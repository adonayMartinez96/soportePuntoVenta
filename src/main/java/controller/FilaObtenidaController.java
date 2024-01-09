package controller;

import Models.OrdenSingleton;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class FilaObtenidaController {

    public void filaObtenida(JTable tblOrdenes, OrdenSingleton ordenEncontradaSingleton){
        //obtiene el valor seleccionado
        tblOrdenes.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {

                if (e.getClickCount() == 1) {
                    int filaSeleccionada = tblOrdenes.getSelectedRow();
                    if (filaSeleccionada >= 0) {

                        //este no es para la grid, este obtiene los datos del
                        DefaultTableModel modelo = (DefaultTableModel) tblOrdenes.getModel();

                        String orden = (modelo.getValueAt(filaSeleccionada, 0) != null)? modelo.getValueAt(filaSeleccionada, 0).toString(): "-";
                      //  String fechaIngreso = (modelo.getValueAt(filaSeleccionada, 1)!= null)?(modelo.getValueAt(filaSeleccionada, 1).toString()):"-";
                        String horaCerro = (modelo.getValueAt(filaSeleccionada, 1) != null)?(modelo.getValueAt(filaSeleccionada, 1).toString()): "=";
                        String nombre = (modelo.getValueAt(filaSeleccionada, 2) != null)?(modelo.getValueAt(filaSeleccionada, 2).toString()): "=";
                        String telefono = (modelo.getValueAt(filaSeleccionada, 3) != null)?(modelo.getValueAt(filaSeleccionada, 3).toString()): "=";
                        String ciudad = (modelo.getValueAt(filaSeleccionada, 4) != null)?(modelo.getValueAt(filaSeleccionada, 4).toString()): "=";
                        String direccion =   (modelo.getValueAt(filaSeleccionada, 5) != null)?(modelo.getValueAt(filaSeleccionada, 5).toString()): "=";
                        String departamento = (modelo.getValueAt(filaSeleccionada, 6) != null)?(modelo.getValueAt(filaSeleccionada, 6).toString()): "=";
                        String valorDeclarado = (modelo.getValueAt(filaSeleccionada, 8) != null)?(modelo.getValueAt(filaSeleccionada, 8).toString()): "=";
                        String borrada = (modelo.getValueAt(filaSeleccionada, 9) != null)?(modelo.getValueAt(filaSeleccionada, 9).toString()): "=";
                        String anulada = (modelo.getValueAt(filaSeleccionada, 10) != null)?(modelo.getValueAt(filaSeleccionada, 10).toString()): "=";
                        String pagada = (modelo.getValueAt(filaSeleccionada, 11) != null)?(modelo.getValueAt(filaSeleccionada, 11).toString()): "=";

                        ordenEncontradaSingleton.setOrden(orden);
                        //ordenEncontradaSingleton.setFechaIngreso(fechaIngreso);
                        ordenEncontradaSingleton.setHoraCerro(horaCerro);
                        ordenEncontradaSingleton.setNombre(nombre);
                        ordenEncontradaSingleton.setTelefono(telefono);
                        ordenEncontradaSingleton.setCuidad(ciudad);
                        ordenEncontradaSingleton.setDireccion(direccion);
                        ordenEncontradaSingleton.setDepartamento(departamento);
                        ordenEncontradaSingleton.setValorDeclarado(valorDeclarado);
                        ordenEncontradaSingleton.setBorrado(borrada);
                        ordenEncontradaSingleton.setAnulada(anulada);
                        ordenEncontradaSingleton.setPagada(pagada);
                        System.out.println("pagada: "+pagada);
                    }
                }
            }
        });
    }
}
