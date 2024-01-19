package controller;

import java.util.*;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

import Models.Customer;
import Repositories.CustomersRepository;
import Repositories.ProductsRepository;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.Color;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import decoder.core.DecoderMultipleOrders;
import decoder.core.EncoderMultipleOrders;
import decoder.core.Decoder;

public class decoderOrdersPanel {

    List<Decoder> orders;
    String headerStartOrder;
    int screenWidth;
    int screenHeight;
   
    JPanel panel = new JPanel();
    JTextArea textArea = new JTextArea();

    InsertCustomer insert = new InsertCustomer();

    InsertAddress insertAddress = new InsertAddress();

    GetTextFieldValue getTextFieldValue = new GetTextFieldValue();

    List<String> productos = new ArrayList<>();


    //errors config
    Boolean error = false;
    List<String> errorsList;

    public decoderOrdersPanel() {
        this.orders = new ArrayList<>();
        this.errorsList = new ArrayList<>();
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

        this.buttonScann(textAreaHeight, !this.error);
        this.buttonSave(textAreaHeight, !this.error);
        if(this.error){
            this.printErrors(this.errorsList);
        }

    }

    private void buttonScann(int textAreaHeight, boolean enable) {
        JButton scanButton = new JButton("Scanear");
        scanButton.setBounds(20, textAreaHeight + 20, 80, 25);
        scanButton.setEnabled(enable);
        scanButton.addActionListener(e -> {
            System.out.println("se preciono el scaneo");
            // Aquí colocas la lógica que deseas ejecutar al presionar el botón "Scanear"
            String text = this.textArea.getText();
            // Realiza lo que necesites con el texto del JTextArea
            /* System.out.println("Texto ingresado: " + text); */
            this.decodeText(text);
            if(!this.error){
                this.runPanelEdit();
            }
            

        });
        this.panel.add(scanButton);
    }

    private void buttonSave(int textAreaHeight, boolean enable) {
        JButton save = new JButton("Guardar");
        save.setBounds(120, textAreaHeight + 20, 80, 25);
        save.setEnabled(enable);
        save.addActionListener(e -> {
            this.decodeText(this.textArea.getText());
            System.out.println("se preciono el guardado");
            if(!this.error){
                this.save();
            }
        });
        this.panel.add(save);
    }

    private void updateTextArea() {
        this.textArea.setText(EncoderMultipleOrders.encodeOrders(this.orders, this.headerStartOrder));
    }

    public void decodeText(String text) {
        if (this.orders.size() == 0) {
            DecoderMultipleOrders orders = new DecoderMultipleOrders(text);
            orders.addRequiredKey("Teléfono");
            orders.addRequiredKey("Nombre");
            orders.addRequiredKey("Ciudad");
            orders.addRequiredKey("Departamento");
            orders.addRequiredKey("Total a pagar");
            orders.addRequiredKey("Envío");
            orders.addRequiredKey("Fecha de entrega");
            orders.decode();
            List<Decoder> order = orders.getOrders();
            this.orders = order;
            this.headerStartOrder = orders.getHeaderStartOrder();
            

            //este codigo solamente se ejecutara en el caso que haya errores
           
            this.error = orders.existError();
            this.printErrors(orders.getErrors());
        }
    }


    public void printErrors(List<String> errors){
        StringBuilder errorMessage = new StringBuilder("Errores:\n");
        for (String error : errors) {
            errorMessage.append("- ").append(error).append("\n");
        }
        if(this.error){
            JOptionPane.showMessageDialog(null, errorMessage.toString(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void save() {
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

    public void runPanelEdit() {
        int totalOrders = this.orders.size();
        runPanelEditRecursive(0, totalOrders);
    }

    private void runPanelEditRecursive(int currentIndex, int totalOrders) {
        if (currentIndex >= totalOrders) {
            return; // Kill al proceso si ya termino de leer todos las orders
        }

        Decoder singleOrder = this.orders.get(currentIndex);
        JFrame editFrame = new JFrame(
                "Scaneando pedido " + (currentIndex + 1) + " de " + totalOrders + " (" + singleOrder.getName() + ")");
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

        String deparmentValue = singleOrder.getValue("Departamento");
        JTextField departmentField = new JTextField(deparmentValue);
        editPanel.add(new JLabel("Departamento:"));
        editPanel.add(departmentField);

        JTextField exactAddressField = new JTextField(
                singleOrder.getAddress().get("address") + " - " + singleOrder.getAddress().get("reference"));
        editPanel.add(new JLabel("Direccion exacta:"));
        editPanel.add(exactAddressField);

        JTextField totalAmountField = new JTextField(singleOrder.getTotal());
        editPanel.add(new JLabel("Total a pagar:"));
        editPanel.add(totalAmountField);

        JTextField shippingField = new JTextField(singleOrder.getShipping());
        editPanel.add(new JLabel("Envio:"));
        editPanel.add(shippingField);

        JTextField deliveryDateField = new JTextField(singleOrder.getDeliveryDate());
        editPanel.add(new JLabel("Fecha entrega:"));
        editPanel.add(deliveryDateField);

        Map<JComboBox<String>, JTextField> productFields = new LinkedHashMap<>();

        for (Map.Entry<String, Integer> entry : singleOrder.getProducts().entrySet()) {
            String productNameInput = entry.getKey();
            int productQuantityInput = entry.getValue();

            JComboBox<String> productComboBox = createProductComboBox(productNameInput);
            JTextField productFieldCantidad = new JTextField(Integer.toString(productQuantityInput));
            editPanel.add(new JLabel("Producto:"));
            editPanel.add(productComboBox);
            editPanel.add(new JLabel("Cantidad:"));
            editPanel.add(productFieldCantidad);
            productFields.put(productComboBox, productFieldCantidad);
        }

        // ... Otros campos de edición
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
            singleOrder.setData("city", departmentField.getText() + " - " + cityField.getText());
            singleOrder.setData("address", exactAddressField.getText());
            singleOrder.setData("price", totalAmountField.getText());
            singleOrder.setData("shipping", shippingField.getText());
            singleOrder.setData("total", totalAmountField.getText());
            singleOrder.setData("delivery date", deliveryDateField.getText());

            // nameProduct -> quantity
            Map<String, Integer> updatedProducts = new LinkedHashMap<>();

            for (Map.Entry<JComboBox<String>, JTextField> entry : productFields.entrySet()) {
                String productNameMutable = (String) entry.getKey().getSelectedItem(); // esto para obtener el
                                                                                       // producto selecionado xd
                int productQuantityMutable = Integer.parseInt(entry.getValue().getText());

                for (Map.Entry<String, Integer> productEntry : singleOrder.getProducts().entrySet()) {
                    String productNameTempInmutable = productEntry.getKey();
                    int productQuantityTempInmutable = productEntry.getValue();

                    if (productNameMutable.equals(productNameTempInmutable)) {
                        // El nombre del producto coincide, verifica si hay cambios en la cantidad
                        if (productQuantityMutable != productQuantityTempInmutable) {
                            updatedProducts.put(productNameMutable, productQuantityMutable);
                        }
                    } else {
                        // El nombre del producto no coincide, se mantendra el nombre original
                        updatedProducts.put(productNameTempInmutable, productQuantityTempInmutable);
                    }
                }
            }

            singleOrder.editData(updatedProducts);

            this.updateTextArea(); // ver esa cosa que esta haciendo bugs :c
            editFrame.dispose();

            runPanelEditRecursive(currentIndex + 1, totalOrders);

        });

        editPanel.add(saveButton);

        editFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        editFrame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                editFrame.dispose();
                runPanelEditRecursive(currentIndex + 1, totalOrders);
                //handleWindowClosing(singleOrder, currentIndex, totalOrders, editFrame);
            }
        });

        editFrame.setVisible(true);

    }

    private static JComboBox<String> createProductComboBox(String initialProduct) {
        List<String> availableProducts = ProductsRepository.getAvailableProducts();

        String bestMatch = ProductsRepository.searchProduct(initialProduct);
        if (bestMatch.isEmpty()) {
            availableProducts.add("No se encontró el producto, elige uno");
        }

        JComboBox<String> productComboBox = new JComboBox<>(availableProducts.toArray(new String[0]));

        int selectedIndex = availableProducts.indexOf(bestMatch);
        if (selectedIndex != -1) {
            productComboBox.setSelectedIndex(selectedIndex);
        } else {
            productComboBox.setSelectedItem("No se encontró el producto, elige uno");
            productComboBox.setForeground(Color.RED);
        }

        return productComboBox;
    }

}
