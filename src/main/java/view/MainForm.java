package view;

import Models.OrdenSingleton;
import Models.FiltrosBusquedas;
import Models.Validaciones;
import controller.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

//HOLA MUNDO
public class MainForm extends JFrame {
    private JPanel MainPanel;
    private JPanel MainPanel2;

    private JLabel lblName;
    private JTextField txtFechaInicio;
    private JTextField txtFechafin;
    private JTable tblOrdenes;
    private JButton btnBuscar;
    private JTextField txtOrden;
    private JTextField txtMotorista;
    private JButton btnAsignarMotorista;
    private JLabel lblFechaIncio;
    private JLabel lblFechaFin;
    private JLabel lblOrden;
    private JLabel lblMotorista;
    private JButton btnActualizar;
    private JButton btnGenerarExcel;
    private JButton btnAnularOrden;
    private JComboBox cbxAnuladas;
    private JButton btnFecha1;
    private JButton btnFechaFinal;
    private JButton btnRegistrarPago;
    private JTextField txtNombe;
    private JLabel lblNombre;
    private JButton agregarOrdenesButton;


    static Connection con;
    static PreparedStatement pst;
    boolean bandera = false;

    //variable la cual resive la direccion donde sera el output de los excel,esta ruta se obtiene del .bat
    private static String excelFile;

    OrdenSingleton ordenEncontradaSingleton = OrdenSingleton.getInstancia();
    Validaciones validaciones = new Validaciones();
    FiltrosBusquedas filtrosBusquedas = new FiltrosBusquedas();
    InvocacionesFechas fechas = new InvocacionesFechas();
    GenerarExcel generarExcel = new GenerarExcel();
    CargaPorDefecto cargaPorDefecto = new CargaPorDefecto();
    FilaObtenidaController filaObtenidaController = new FilaObtenidaController();
    AsignarMotoristaController asignarMotoristaController = new AsignarMotoristaController();
    GenerarExcelController excelController = new GenerarExcelController();
    ActualizarController actualizarController = new ActualizarController();
    BuscarController buscar = new BuscarController();
    AnularOrdenController anularOrdenController = new AnularOrdenController();
    CalendarioController calendario = new CalendarioController();
    CalendarioController2 calendario2= new CalendarioController2();
    AnularOrden2Controller anularOrden2Controller =new AnularOrden2Controller();
    ControllerRegistrarPago registrarPago = new ControllerRegistrarPago();
    //Fecha2 fecha2 = new Fecha2();

    DefaultTableModel  model = new DefaultTableModel();



    public MainForm(){

        model.addColumn("ORDEN");
        model.addColumn("HORA_ASIGNACION");
        model.addColumn("NOMBRE");
        model.addColumn("TELEFONO");
        model.addColumn("CIUDAD");
        model.addColumn("DIRECCION");
        model.addColumn("DEPARTAMENTO");
        model.addColumn("MOTORISTA");
        model.addColumn("VALOR_DECLARADO");
        model.addColumn("BORRADO");
        model.addColumn("ANULADA");
        model.addColumn("PAGADA");

        setContentPane(MainPanel);

        setTitle("Soporte");
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        // Obtiene el objeto Toolkit
        Toolkit toolkit = Toolkit.getDefaultToolkit();

        // Obtiene la dimensión de la pantalla
        Dimension screenSize = toolkit.getScreenSize();

        // Obtiene el ancho y alto de la pantalla
        int anchoPantalla = screenSize.width;
        int altoPantalla = screenSize.height;

        setSize(anchoPantalla,altoPantalla-50);
        setLocationRelativeTo(null);
        setVisible(true);

        //establece fecha del sistema a los campos fecha inicial y fecha final por defecto
        cargaPorDefecto.setCamposFehas(txtFechaInicio,txtFechafin);

        //carga lista diaria por defecto
        cargaPorDefecto.loadOrdenes(tblOrdenes,model);

        String valorPorDefecto = (String) cbxAnuladas.getSelectedItem();



        //btnBuscar
        buscar.buscar( btnBuscar, txtFechaInicio, txtFechafin,  txtMotorista, txtOrden,txtNombe, tblOrdenes, model, cbxAnuladas);

        //btnAsignarMotorista
        asignarMotoristaController.asignarMotorista(btnAsignarMotorista,ordenEncontradaSingleton);

        //validaciones campos fechas
        fechas.fecha(txtFechaInicio);
        fechas.fecha(txtFechafin);

        //validacion orden
        validaciones.camposNumericos(txtOrden);

        //obtiene el valor seleccionado
        filaObtenidaController.filaObtenida(tblOrdenes,ordenEncontradaSingleton);

        //btnActualizar lista ventas diarias
        actualizarController.actualizar(btnActualizar,model,tblOrdenes,txtFechaInicio,txtFechafin);

        //btnAnularOrden
//      anularOrdenController.anularOrden(tblOrdenes,ordenEncontradaSingleton,txtComentario,btnAnularOrden);

        //UAT OFICINA
        //generarExcel.outputeExcel(model,"C:/Users/cliente/Desktop/listaGenerada/lista");

        //PRODUCCION
        //generarExcel.outputeExcel(model,"C:/Users/cliente/Desktop/REPORTES/lista");
        //estado:prod

        //DIRECCION SUSY
        
        excelController.generarReporteExcel(btnGenerarExcel,model,excelFile);

        btnFecha1.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                calendario.calendario(txtFechaInicio);

            }
        });

        btnFechaFinal.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                calendario2.calendario(txtFechafin);
            }
        });

        btnAnularOrden.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                anularOrden2Controller.anularOrdenFormulario(ordenEncontradaSingleton);
            }
        });
        btnRegistrarPago.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                registrarPago.registrarPago(ordenEncontradaSingleton);
            }
        });
        agregarOrdenesButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
               // System.out.println("Holazd");
                decoderOrdersPanel panel = new decoderOrdersPanel();
                panel.run();
                System.out.printf("cliqueado");
            }
        });
    }




    public static void main(String [] args){
       // excelFile = args[0];
        Conexion.main(args);
        new MainForm();
    }

}

