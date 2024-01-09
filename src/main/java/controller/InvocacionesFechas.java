package controller;

import Models.Validaciones;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class InvocacionesFechas {

    Validaciones validaciones = new Validaciones();

    public void fecha(JTextField txtFechaInicio){
        System.out.println("sou");
        System.out.println();
        txtFechaInicio.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                validaciones.validateInput(txtFechaInicio);
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                validaciones.validateInput(txtFechaInicio);
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                validaciones.validateInput(txtFechaInicio);
            }
        });
    }
}
