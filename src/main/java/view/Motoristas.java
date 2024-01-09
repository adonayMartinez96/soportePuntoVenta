package view;

import controller.Conexion;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Motoristas extends JFrame {
    private  JTable tblMotoristas;
    private JLabel txtMotorista;
    private JButton btnAsignar;
    private JPanel panelMotorista;
    private JScrollPane scrMotoristas;
    JDialog dialog = new JDialog();


    public Motoristas(){
        // Crear una nueva ventana de diálogo (JDialog)
        setContentPane(panelMotorista);

        setSize(500,500);
        setLocationRelativeTo(null);
        setVisible(true);

        setTitle("MOTORISTAS");
    }
/*
    public void loadMotoristas(){
        DefaultTableModel model = new DefaultTableModel();

        try{
            String ventasDiarias = "select \n" +
                    "\tm.id as id,\n" +
                    "\tm.nombre as nombre,\n" +
                    "\tcase WHEN m.activo >= 1 THEN 'Si' WHEN m.activo >= 0 THEN 'No' ELSE 'Bajo' END AS activo\n" +
                    "from silverpos.motoristas m where LENGTH(m.nombre) > 0";

            Conexion con = new Conexion();
            Connection conexionMysql  = con.conectar();
            System.out.println(ventasDiarias);

            Statement st = conexionMysql.createStatement();
            ResultSet rs = st.executeQuery(ventasDiarias);


            model.addColumn("ID");
            model.addColumn("NOMBRE");
            model.addColumn("ACTIVO");


            while (rs.next()) {
                String id = rs.getString("id");
                String orden = rs.getString("nombre");
                String activo = rs.getString("activo");

                model.addRow(new Object[]{id,orden,activo});
            }
            rs.close();;
            st.close();
        }
        catch ( SQLException e){
            e.printStackTrace();
        }

        tblMotoristas.setModel(model);
    }*/
}
