package controller;

import Models.OrdenSingleton;
import Models.Validaciones;
import view.MotoristaModal;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class AsignarMotoristaController {
    Validaciones validaciones = new Validaciones();
    FilaObtenidaController filaObtenidaController = new FilaObtenidaController();

    public void asignarMotorista(JButton btnAsignarMotorista, OrdenSingleton ordenEncontradaSingleton){

        btnAsignarMotorista.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                    String valor = ordenEncontradaSingleton.getAnulada().toString();

                if(ordenEncontradaSingleton.getAnulada().equals("SI")){
                    JOptionPane.showMessageDialog(
                            null,        // Componente padre (null para ventana principal)
                            "Esta orden esta anulada, no es posible asignar un motorista.", // Mensaje
                            "Alerta",    // Título de la ventana
                            JOptionPane.WARNING_MESSAGE  // Tipo de icono (en este caso, advertencia)

                    );
                }else {
                    MotoristaModal modal = new MotoristaModal();
                    modal.setVisible(true);
                     validaciones.validarSeleccionOrden(ordenEncontradaSingleton);
                }
            }
        });
    }
}
