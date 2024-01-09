package controller;

import Models.OrdenSingleton;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class ControllerRegistrarPago {
    Conexion conexion = new Conexion();
    private String valorSeleccionado ;

    public void registrarPago(OrdenSingleton orden) {

        String queryMetodosPagos = "select nombre_metodo from silverpos_hist.metodos_pago where activo = 1";

        String insertRegistroPagos = "insert into silverpos_hist.registro_pagos(no_orden,metodo_pago,monto_pago,fecha_registro_pago) values\n" +
                "(?,?,?,DATE_FORMAT(NOW(), '%Y-%m-%d %H:%i:%s'));";

        if (orden.getPagada().equals("NO")) {
            JFrame frame = new JFrame("Anular orden");
            frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            frame.setSize(300, 150);

            // Crear un JPanel para contener los componentes
            JPanel panel = new JPanel();
            frame.add(panel);

            JComboBox<String> jComboBox = new JComboBox<>();
            // Crear un JLabel
            JLabel label = new JLabel("Monto a pagar:");
            JLabel label2 = new JLabel("Metodo de pago:");

            try {
                Conexion con = new Conexion();
                Connection conexionMysql = con.conectar();

                Statement st = conexionMysql.createStatement();
                ResultSet rs = st.executeQuery(queryMetodosPagos);

                // Limpia el JComboBox
                jComboBox.removeAllItems();

                // Llena el JComboBox con los datos obtenidos
                while (rs.next()) {
                    String nombre = rs.getString("nombre_metodo");
                    jComboBox.addItem(nombre);
                }

                jComboBox.setSelectedIndex(0);
                // Cierra la conexión
                rs.close();
                rs.close();
                rs.close();


            } catch (Exception e) {
                e.printStackTrace();
            }

            // Crear un JTextField
            JTextField textField = new JTextField(20);
            textField.addKeyListener(new NumericTextFieldKeyListener());

            // Crear un JButton
            JButton button = new JButton("Registrar pago");

            // Agregar un ActionListener al botón
            button.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    valorSeleccionado = (String) jComboBox.getSelectedItem();

                    if (textField.getText().length() > 0) {
                        int opcion = JOptionPane.showConfirmDialog(null,
                                "¿Deseas pagar la orden: " +
                                        "Numero de Orden: " + orden.getOrden() + " \n" +
                                        "Cliente: " + orden.getNombre() + " \n" +
                                        "Telefono: " + orden.getTelefono() + " \n" +
                                        "Total a pagar: " + textField.getText() + " \n" +
                                        "Metodo de pago: " + valorSeleccionado + " ?\n"
                                , "Confirmación", JOptionPane.YES_NO_OPTION);

                        if (opcion == JOptionPane.YES_OPTION) {
                            System.out.println("si");
                            try {
                                Conexion con = new Conexion();
                                Connection conexionMysql = con.conectar();
                                String textoTxt = textField.getText();

                                PreparedStatement preparedStatement = conexionMysql.prepareStatement(insertRegistroPagos);
                                preparedStatement.setString(1, orden.getOrden());
                                preparedStatement.setString(2, valorSeleccionado);
                                preparedStatement.setFloat(3, Float.parseFloat(textoTxt));


                                // Ejecutar la inserción
                                int filasAfectadas = preparedStatement.executeUpdate();

                                if (filasAfectadas > 0) {
                                    System.out.println("Inserción exitosa.");
                                } else {
                                    System.out.println("No se pudo insertar el usuario.");
                                }

                                panel.setVisible(false);
                                frame.setVisible(false); // Hace invisible el JFrame
                                frame.dispose();
                                System.out.println("cierre exitoso");

                            } catch (Exception i) {
                                i.printStackTrace();
                            }
                        } else {
                            System.out.println("El usuario eligió No o cerró el cuadro de diálogo.");
                        }

                    } else {
                        JOptionPane.showMessageDialog(frame, "Usted no ingreso un comentario");
                    }
                }


            });


            // Agregar los componentes al panel
            panel.add(label2);
            panel.add(jComboBox);
            panel.add(label);
            panel.add(textField);
            panel.add(button);

            // Configurar el diseño del panel
            panel.setLayout(new FlowLayout());

            // Mostrar la ventana

            frame.setVisible(true);

        }else {
            JOptionPane.showMessageDialog(
                    null,        // Componente padre (null para ventana principal)
                    "Esta orden esta pagada, no es posible pagar nuevamente .", // Mensaje
                    "Alerta",    // Título de la ventana
                    JOptionPane.WARNING_MESSAGE  // Tipo de icono (en este caso, advertencia)

            );
        }
    }

    class NumericTextFieldKeyListener implements KeyListener {
        @Override
        public void keyTyped(KeyEvent e) {
            char c = e.getKeyChar();

            if (!(Character.isDigit(c) || c == '.')) {
                e.consume(); // Ignora la pulsación si no es un dígito o un punto
            }
        }

        @Override
        public void keyPressed(KeyEvent e) {
            // No necesitamos implementar nada aquí
        }

        @Override
        public void keyReleased(KeyEvent e) {
            // No necesitamos implementar nada aquí
        }
    }
}
