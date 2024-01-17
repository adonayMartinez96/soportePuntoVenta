package controller;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.*;
import java.util.List;

public class GetTextFieldValue {

    public void obtenerValoresDeJTextField(JPanel panel) {
        Component[] componentes = panel.getComponents();

        for (Component componente : componentes) {
            if (componente instanceof JTextField) {
                JTextField textField = (JTextField) componente;
                String valor = textField.getText();
                System.out.println("Valor del JTextField: " + valor);
            }
        }
    }

    public List<String> recorrerYObtenerTexto(Container container, String accessibleNamePrefix) {
        List<String>  productos  = new ArrayList<>();

        for (Component componente : container.getComponents()) {
            if (componente instanceof JTextField) {
                String nombreAccesible = ((JTextField) componente).getAccessibleContext().getAccessibleName();
                if (nombreAccesible != null && nombreAccesible.startsWith(accessibleNamePrefix)) {
                    String texto = ((JTextField) componente).getText();
                    //System.out.println("Nombre Accesible: " + nombreAccesible + ", Texto: " + texto);
                    productos.add(texto);
                }
            }
        }
        return productos;
    }



}
