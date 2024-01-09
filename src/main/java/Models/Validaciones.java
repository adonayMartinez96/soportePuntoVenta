package Models;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.regex.Pattern;

public class Validaciones {

    //validacion
    public boolean validarSeleccionOrden(OrdenSingleton ordenAsignar){
        boolean bandera = false;

        if(ordenAsignar.getOrden().length() ==  0) {
            // Mostrar una alerta de advertencia

            JOptionPane.showMessageDialog(null, "Usted no a selececionado una orden, Seleccione una porfavor", "Advertencia", JOptionPane.WARNING_MESSAGE);
        }else{
            bandera = true;
        }
        return bandera;
    }

    public void detecccionDeSeleccion(JTable tabla){

        tabla.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int selectedRow = tabla.getSelectedRow();
                if (selectedRow >= 0) {
                    System.out.println("se encontro fila seleccionada");
                } else {
                    System.out.println("no se encontro fila seleccionada");
                }
            }
        });
    }

    public static void validateInput(JTextField campo) {
        String input = campo.getText();

        // Expresión regular para el formato "2002-05-05 14:20:15"
        String pattern = "^\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}$";
        if (Pattern.matches(pattern, input)) {
            // El formato es válido, permite la entrada
            campo.setBackground(Color.WHITE);
            campo.setForeground(Color.black);
            campo.setToolTipText(null);
        } else {
            // El formato no es válido, no permite la entrada
            campo.setBackground(Color.RED);
            campo.setForeground(Color.WHITE);
            campo.setToolTipText("Formato no válido. Use yyyy-MM-dd HH:mm:ss");
        }
    }

    public void caracteresAceptadosFechas(JTextField textField){
        AbstractDocument document = (AbstractDocument) textField.getDocument();

        document.setDocumentFilter(new DocumentFilter() {
            @Override
            public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
                if (esNumero(string)) {
                    super.insertString(fb, offset, string, attr);
                }
            }

            @Override
            public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
                if (esNumero(text)) {
                    super.replace(fb, offset, length, text, attrs);
                }
            }

            private boolean esNumero(String text) {
                String regex = "^[0-9:-\\s]*$";
                return text.matches("^\\\\d{4}-\\\\d{2}-\\\\d{2} \\\\d{2}:\\\\d{2}:\\\\d{2}$");
            }
        });
    }

    public void camposNumericos(JTextField textField){

        AbstractDocument document = (AbstractDocument) textField.getDocument();

        document.setDocumentFilter(new DocumentFilter() {
            @Override
            public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
                if (esNumero(string)) {
                    super.insertString(fb, offset, string, attr);
                }
            }

            @Override
            public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
                if (esNumero(text)) {
                    super.replace(fb, offset, length, text, attrs);
                }
            }

            private boolean esNumero(String text) {
                return text.matches("\\d+");
            }
        });
    }



}
