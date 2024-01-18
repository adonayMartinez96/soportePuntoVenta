package controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

import Models.Customer;
import Repositories.CustomersRepository;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Toolkit;

import decoder.core.DecoderMultipleOrders;
import decoder.core.Decoder;

public class decoderOrdersPanel {

    List<Decoder> orders;
    int screenWidth;
    int screenHeight;
    JPanel panel = new JPanel();
    JTextArea textArea = new JTextArea();

    InsertCustomer insert = new InsertCustomer();

    InsertAddress insertAddress = new InsertAddress();

    public decoderOrdersPanel() {
        this.orders = new ArrayList<>();
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        this.screenWidth = (int) screenSize.getWidth();
        this.screenHeight = (int) screenSize.getHeight();
    }

    public void run() {

        JFrame frame = new JFrame("Escaner de ordenes");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        /* frame.setSize(300, 200); */
        frame.setSize(this.screenWidth - 40, this.screenHeight - 100);

        frame.add(this.panel);
        this.placeComponents();
        frame.setContentPane(this.panel);

        frame.setVisible(true);
        System.out.println("Cliqueado");
    }

    private void placeComponents() {
        this.panel.setLayout(null);
        this.textArea.setLineWrap(true);
        this.textArea.setWrapStyleWord(true);

        JScrollPane scrollPane = new JScrollPane(this.textArea);
        /* scrollPane.setBounds(10, 10, 260, 100); */
        int textAreaWidth = this.screenWidth - 80;
        int textAreaHeight = this.screenHeight - 200;
        scrollPane.setBounds(10, 10, textAreaWidth, textAreaHeight);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        this.panel.add(scrollPane);

        this.buttonScann(textAreaHeight);
        this.buttonSave(textAreaHeight);

    }

    private void buttonScann(int textAreaHeight) {
        JButton scanButton = new JButton("Scanear");
        scanButton.setBounds(20, textAreaHeight + 20, 80, 25);
        scanButton.addActionListener(e -> {
            System.out.println("se preciono el scaneo");
            // Aquí colocas la lógica que deseas ejecutar al presionar el botón "Scanear"
            String text = this.textArea.getText();
            // Realiza lo que necesites con el texto del JTextArea
            /* System.out.println("Texto ingresado: " + text); */
            this.decodeText(text);

            this.runPanelEdit(text);
        });
        this.panel.add(scanButton);
    }

    private void buttonSave(int textAreaHeight) {
        JButton save = new JButton("Guardar");
        save.setBounds(120, textAreaHeight + 20, 80, 25);
        save.addActionListener(e -> {
            this.decodeText(this.textArea.getText());
            System.out.println("se preciono el guardado");
            this.debug();
            /* aca para guardar la data */
        });
        this.panel.add(save);
    }

    public void decodeText(String text) {
        if (this.orders.size() == 0) {
            DecoderMultipleOrders orders = new DecoderMultipleOrders(text);
            List<Decoder> order = orders.getOrders();
            this.orders = order;
        }

    }

    public void debug() {
        for (Decoder singleOrder : this.orders) {
            System.out.println("Nombre: " + singleOrder.getName());

            int idCliente = 0; // Inicializamos el idCliente fuera del bloque if
            boolean isNew;
            int idInsertIfClientNotExists = -1;

            int encontrado = insert.findCustomer(singleOrder.getPhone());
            if (encontrado == 1) {
                /*
                 * Si el cliente ya esta registrado pues tendria que obtener el id del cliente
                 * por numero de telefono
                 */
                CustomersRepository client = new CustomersRepository();
                /*
                 * Se espera que esta lista solamente traiga a un telefono por que se espera
                 * que el telefono sea unico
                 */
                List<Customer> clients = client.getCustomersByPhone(singleOrder.getPhone());
                Customer firstClient = clients.get(0);

                if (clients.size() > 1) {
                    int choice = JOptionPane.showConfirmDialog(null,
                            "¡Atención! Se encontraron múltiples clientes con el mismo número de teléfono. ¿Desea continuar?\n"
                                    + "Solamente se obtendrá el primer cliente con este teléfono. Su nombre es: "
                                    + firstClient.getName(),
                            "Mensaje de Advertencia", JOptionPane.YES_NO_OPTION);
                    if (choice == JOptionPane.NO_OPTION) {
                        System.out.println("Proceso detenido por el usuario.");
                        return; // Puedes elegir cómo manejar la interrupción del código aquí
                    }
                }

                /*
                 * Si es un cliente existente se ira a comparar que esa direcion es nueva o no
                 * nueva
                 * para saber si se va a crear o no crear, el problema aca es que puede llegar a
                 * pasar
                 * que la direcion nueva no sea igual por una sola letra o por un espacio, asi
                 * que yo creo
                 * que hay que comparar niveles de simulitud para ver si se creara la direcion o
                 * no se creara
                 * 
                 * entonces, en este if pasaran
                 * 
                 * 1. verificar si la direcion por el decoder es "parecida" a la que esta en la
                 * tabla silverpos.clientes_direcciones_domicilio
                 * 2. si tiene alta simulitud no se guardara si se tiene poca simulitud se
                 * guardara
                 * 
                 * 
                 * esa
                 */
                idCliente = firstClient.getId();
                isNew = true;
                System.out.println("El cliente ya está registrado: " + encontrado);
            } else {
                System.out.println("\n listo para insertar\n");
                idCliente = insert.insertCustomer(singleOrder.getName(), singleOrder.getPhone());
                idInsertIfClientNotExists = insertAddress.insertAddressClient(
                        idCliente,
                        singleOrder.getAddress().get("address"),
                        singleOrder.getAddress().get("reference"));
                isNew = false;
            }

            OrderController orderController = new OrderController(idCliente, singleOrder, isNew);
            if (idInsertIfClientNotExists != -1) {
                orderController.setAddressSaveId(idInsertIfClientNotExists);
            }

        }
    }

    public void runPanelEdit(String text) {

        for (Decoder singleOrder : this.orders) {
            JFrame editFrame = new JFrame("Editar Pedido");
            editFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            editFrame.setSize(400, 300);

            JPanel editPanel = new JPanel();
            editFrame.add(editPanel);
            editPanel.setLayout(new GridLayout(0, 2));

            JTextField nameField = new JTextField(singleOrder.getName());
            editPanel.add(new JLabel("Nombre:"));
            editPanel.add(nameField);

            JTextField phoneField = new JTextField(singleOrder.getPhone());
            editPanel.add(new JLabel("Teléfono:"));
            editPanel.add(phoneField);

            JTextField cityField = new JTextField(singleOrder.getCity());
            editPanel.add(new JLabel("Ciudad:"));
            editPanel.add(cityField);

            // creamos un JtextField por cada producto dentro de la orden

            Map<JTextField, JTextField> fiels =  new LinkedHashMap<>();


            for (Map.Entry<String, Integer> entry : singleOrder.getProducts().entrySet()) {


                JTextField productFieldName = new JTextField(entry.getKey());
                editPanel.add(new JLabel("Producto:"));
                editPanel.add(productFieldName);

                JTextField productFieldCantidad = new JTextField(entry.getValue());
                editPanel.add(new JLabel("Cantidad:"));
                editPanel.add(productFieldCantidad);

                fiels.put(productFieldName, productFieldCantidad);

            }



            JButton saveButton = new JButton("Guardar");
            saveButton.addActionListener(e -> {
                /*
                 * Aca editar cada una de las cosas, por el momento solamente se pueden editar
                 * esos
                 * tres campos (son los que se van a cambiar con el la lista de decoders que
                 * estas
                 * cargada en ram en ese momento)
                 * 
                 * arriba se tendra que poner todos los fields para que se puedan editar, en
                 * este momento
                 * tambien solamente estan Nombre, Telefono, Ciudad
                 */
                singleOrder.setData("name", nameField.getText());
                singleOrder.setData("phone", phoneField.getText());
                singleOrder.setData("city", cityField.getText());

                //nameProduct -> quantity
                for (Map.Entry<JTextField, JTextField> entry : fiels.entrySet()) {
                    singleOrder.setData(entry.getKey().getText(), entry.getValue().getText());
                }

                

                editFrame.dispose();
            });

            editPanel.add(saveButton);
            editFrame.setVisible(true);
        }
    }
}
