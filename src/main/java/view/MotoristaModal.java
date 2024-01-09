package view;

import Models.Motorista;
import Models.OrdenSingleton;
import controller.Conexion;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.*;

public class MotoristaModal extends JFrame {

    private JPanel modalPanel;
    OrdenSingleton orden = OrdenSingleton.getInstancia();

    public MotoristaModal() {
        super("JFrame Modal");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(400, 300);

        //JPanel transparente para simular la modalidad
        modalPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                // Pintar un fondo semitransparente
                g.setColor(new Color(0, 0, 0, 128));
                g.fillRect(0, 0, getWidth(), getHeight());
            }
        };

        modalPanel.setLayout(new BorderLayout());
        setContentPane(modalPanel);

        // Personalizar el contenido del JFrame
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BorderLayout());

        JScrollPane scrollPane;
        scrollPane = loadAllMotoristas();

        contentPanel.add(scrollPane);

        modalPanel.add(contentPanel);
        modalPanel.setVisible(true);

        setLocationRelativeTo(null); // Centrar en la pantalla

    }

    public  JScrollPane loadAllMotoristas(){
        DefaultTableModel model = new DefaultTableModel();
        JScrollPane scrollPane;
        try{
            String ventasDiarias = "select \n" +
                    "\tm.id as id,\n" +
                    "\tm.nombre as nombre,\n" +
                    "\tcase WHEN m.activo >= 1 THEN 'Si' WHEN m.activo >= 0 THEN 'No' ELSE 'Bajo' END AS activo\n" +
                    "from silverpos.motoristas m where m.activo =1";

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
        JTable table = new JTable();
        table.setModel(model);

        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {

                if (e.getClickCount() == 1) {
                    int filaSeleccionada = table.getSelectedRow();

                    if (filaSeleccionada >= 0) {
                        Motorista motorista = new Motorista();
                        DefaultTableModel modelo = (DefaultTableModel) table.getModel();
                        String id = modelo.getValueAt(filaSeleccionada, 0).toString();
                        String nombre = modelo.getValueAt(filaSeleccionada, 1).toString();

                        int opcion = JOptionPane.showConfirmDialog(null,
                                "¿Deseas asignar el motorista con " +
                                        "\nNombre: "+nombre+" \n" +
                                        "A la Orden: "+orden.getOrden()+" ?\n"+
                                        "Cliente: "+orden.getNombre()+" ?\n"+
                                        "Telefono: "+orden.getTelefono()+" ?\n"+
                                        "Total: "+orden.getValorDeclarado()+" ?\n"
                                , "Confirmación", JOptionPane.YES_NO_OPTION);

                        if (opcion == JOptionPane.YES_OPTION) {
                            Conexion con = new Conexion();
                            Connection mysql = con.conectar();
                            ResultSet rs = null;

                            String queryVentasDiarias = "select count(*) as contador from ventasdiarias.venta_encabezado ve \n" +
                                    "where ve.no_orden = ?  and \n" +
                                    "ve.cliente_domicilio = ?";

                            String historialQueryVentasDiarias = "select count(*) as contador from silverpos_hist.hist_venta_enca hve where \n" +
                                    "hve.no_orden  = ? and hve.cliente_domicilio = ?; ";

                            String actualzacionMotoristaDiario ="update ventasdiarias.venta_encabezado set idmotorista  = ? where  no_orden = ?  and \n" +
                                    "cliente_domicilio = ?";

                            String actualzacionMotoristaHistorial ="update silverpos_hist.hist_venta_enca set idmotorista  = ? where  no_orden = ?  and \n" +
                                    "cliente_domicilio = ?";

                            String actualizacionHoraCerroDiario = "update ventasdiarias.venta_encabezado set hora_cerro = sysdate()  where\n" +
                                    "no_orden = ? and cliente_domicilio = ?";

                            String actualizacionHoraCerroHistorial = "update silverpos_hist.hist_venta_enca set hora_cerro = sysdate()  where\n" +
                                    "no_orden = ? and cliente_domicilio = ?";

                            String ordenTable = orden.getOrden();
                            String[] pairs = ordenTable.split("-");
                            String ordenNeta ="";

                            for(String i: pairs){
                                System.out.println(i);
                            }

                            if(pairs.length == 2){
                                ordenNeta = pairs[1];
                            }

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

                                        PreparedStatement stMotorista = mysql.prepareStatement(actualzacionMotoristaDiario);
                                        stMotorista.setInt(1,Integer.parseInt(id));
                                        stMotorista.setString(2,ordenNeta);
                                        stMotorista.setString(3,orden.getNombre());
                                        int numero = stMotorista.executeUpdate();

                                        PreparedStatement stMotoristaHoraCerro = mysql.prepareStatement(actualizacionHoraCerroDiario);
                                        stMotoristaHoraCerro.setString(1, ordenNeta);
                                        stMotoristaHoraCerro.setString(2, orden.getNombre());
                                        int numeroHoraCerro = stMotoristaHoraCerro.executeUpdate();

                                        System.out.println("actualizacion hora "+numeroHoraCerro);
                                        System.out.println("actualizacion realizada " +numero);
                                    }else  if(resultado ==0){
                                        PreparedStatement stHistorial = mysql.prepareStatement(actualzacionMotoristaHistorial);
                                        stHistorial.setInt(1,Integer.parseInt(id));
                                        stHistorial.setString(2,ordenNeta);
                                        stHistorial.setString(3,orden.getNombre());
                                        int numero = stHistorial.executeUpdate();

                                        System.out.println("actualizacion realizada " +numero+" al historial " +ordenNeta);

                                        PreparedStatement stMotoristaHoraCerro = mysql.prepareStatement(actualizacionHoraCerroHistorial);
                                        stMotoristaHoraCerro.setString(1, ordenNeta);
                                        stMotoristaHoraCerro.setString(2, orden.getNombre());
                                        int numeroHoraCerro = stMotoristaHoraCerro.executeUpdate();

                                        System.out.println("actualizacion hora "+numeroHoraCerro);
                                        System.out.println("actualizacion realizada " +numero);
                                    }

                                } else {
                                    System.out.println("No se encontraron resultados.");
                                }
                            } catch (SQLException throwables) {
                                throwables.printStackTrace();
                            }


                            System.out.println("El usuario eligió Sí.");
                        } else {
                            System.out.println("El usuario eligió No o cerró el cuadro de diálogo.");
                        }
                    }
                }
            }
        });



       return  scrollPane = new JScrollPane(table);
    }


}
