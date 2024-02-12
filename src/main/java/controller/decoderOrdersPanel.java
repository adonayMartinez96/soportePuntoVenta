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
import javax.swing.SwingUtilities;

import Kernel.utils.Extractor;
import Kernel.utils.StringSimilarityFinder;
import Models.Customer;
import Repositories.CustomersRepository;
import Repositories.OrderTypeRespository;
import Repositories.ProductsRepository;
import Repositories.VentaDetallePlusRepository;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.Color;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.Component;

import  Kernel.decoder.DecoderMultipleOrders;
import  Kernel.decoder.EncoderMultipleOrders;
import  Kernel.errors.Errors;
import  Kernel.decoder.Decoder;

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

    // errors config
    Boolean error = false;
    List<String> errorsList;

    // todo el panel
    JFrame frame;

    // scaan se activo?
    private boolean scanned = false;


    boolean wasDeleteOrder = false;

    /*
     * en esta funcion se definen los key que son requeridos, si el decoder
     * no encuentra uno de estos keys dara un error, se pueden empezar a definir
     * mas keys desde aca
     */
    private void requiredKeys(DecoderMultipleOrders orders) {
        orders.addRequiredKey("Teléfono|telefono|Telefono");
        orders.addRequiredKey("Nombre|nombre");
        orders.addRequiredKey("Ciudad|ciudad");
        orders.addRequiredKey("Departamento|departamento");
        // orders.addRequiredKey("Total producto");
        /// orders.addRequiredKey("Envío");
        orders.addRequiredKey("Fecha de entrega");
        orders.addRequiredKey("tipo|Tipo");
        // orders.addRequiredKey("productos|Productos");
    }

    public decoderOrdersPanel() {
        this.orders = new ArrayList<>();
        this.errorsList = new ArrayList<>();
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        this.screenWidth = (int) screenSize.getWidth();
        this.screenHeight = (int) screenSize.getHeight();
        this.frame = new JFrame("Escaner de ordenes");
    }

    public void run() {
        try {
            this.frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            this.frame.setSize(this.screenWidth - 40, this.screenHeight - 100);
            this.frame.add(this.panel);
            this.placeComponents(""); // vacío por defecto
            this.frame.setContentPane(this.panel);
            this.frame.setVisible(true);
        } catch (Exception e) {
            if (Errors.get().size() > 0) {
                String errorMessage = String.join("\n", Errors.get());
                JOptionPane.showMessageDialog(this.frame, errorMessage, "Errores", JOptionPane.ERROR_MESSAGE);
                this.frame.dispose();
            }
        }
    }

    private void placeComponents(String textArea) {
        this.panel.setLayout(null);
        this.textArea.setLineWrap(true);
        this.textArea.setWrapStyleWord(true);
        this.textArea.setText(textArea);

        JScrollPane scrollPane = new JScrollPane(this.textArea);
        int textAreaWidth = this.screenWidth - 80;
        int textAreaHeight = this.screenHeight - 200;
        scrollPane.setBounds(10, 10, textAreaWidth, textAreaHeight);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        this.panel.add(scrollPane);

        this.buttonScann(textAreaHeight, !this.error);
        this.buttonSave(textAreaHeight, !this.error); // Llamamos directamente al método
    }

    private void buttonScann(int textAreaHeight, boolean enable) {
        JButton scanButton = new JButton("Scanear");
        scanButton.setBounds(20, textAreaHeight + 20, 80, 25);
        scanButton.setEnabled(enable);
        scanButton.addActionListener(e -> {
            String text = this.textArea.getText();
            this.scanned = true;
            this.decodeText(text);
            if (!this.error) {
                this.runPanelEdit();
            }
            this.updateSaveButtonState();
        });
        this.panel.add(scanButton);
    }

    private void buttonSave(int textAreaHeight, boolean enable) {
        JButton save = new JButton("Guardar");
        save.setBounds(120, textAreaHeight + 20, 80, 25);
        save.setEnabled(enable && this.scanned);
        save.addActionListener(e -> {
            this.decodeText(this.textArea.getText());
            //printDebug();

            if (!this.error) {
                this.save();
                JOptionPane.showMessageDialog(null, "¡Guardado exitoso!", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                this.frame.dispose();
            }

            this.updateSaveButtonState();
        });
        this.panel.add(save);
    }

    private void updateSaveButtonState() {
        SwingUtilities.invokeLater(() -> {
            boolean saveButtonEnableState = !this.error && this.scanned;
            Component[] components = this.panel.getComponents();
            for (Component component : components) {
                if (component instanceof JButton) {
                    JButton button = (JButton) component;
                    if (button.getText().equals("Guardar")) {
                        button.setEnabled(saveButtonEnableState);
                    }
                }
            }
        });
    }

    private void updateTextArea() {
        this.textArea.setText(EncoderMultipleOrders.encodeOrders(this.orders, this.headerStartOrder));
    }

    public void decodeText(String text) {
        if (this.orders.size() == 0 && !this.wasDeleteOrder) {
            DecoderMultipleOrders orders = new DecoderMultipleOrders(text);
            this.requiredKeys(orders);
            orders.decode();
            List<Decoder> order = orders.getOrders();
            this.orders = order;
            this.headerStartOrder = orders.getHeaderStartOrder();

            // Este código solamente se ejecutará en el caso que haya errores
            this.error = orders.existError();
            this.printErrors(orders.getErrors());
            this.placeComponents(this.textArea.getText());
        }
    }

    public void printDebug() {
        for (Decoder order : this.orders) {
            System.out.println("Datos del pedido:");
            for (Map.Entry<String, String> entry : order.getData().entrySet()) {
                System.out.println("  " + entry.getKey() + " => " + entry.getValue());
            }

            Map<String, Integer> products = order.getProducts();
            if (products.isEmpty()) {
                System.out.println("No hay productos en este pedido.");
            } else {
                System.out.println("Productos en el pedido:");
                for (Map.Entry<String, Integer> productEntry : products.entrySet()) {
                    String productName = productEntry.getKey();
                    int quantity = productEntry.getValue();
                    System.out.println("  - Producto: " + productName + ", Cantidad: " + quantity);
                }
            }

            System.out.println("Detalles de entrega:");
            System.out.println(String.format("%-5s %-20s", "ID", "Tipo/nombre"));
            for (Map.Entry<Integer, String> entry : order.getDelivery().entrySet()) {
                System.out.println(String.format("%-5s %-20s", entry.getKey(), entry.getValue()));
            }
            System.out.println("");
            System.out.println("Tipos de pedido y nombres:");
            System.out.println(String.format("%-5s %-15s", "ID", "Tipo"));
            for (Map.Entry<Integer, String> entry : order.getTypeOrderIdAndName().entrySet()) {
                System.out.println(String.format("%-5s %-15s", entry.getKey(), entry.getValue()));
            }

            System.out.println("=====================================");
        }
        System.exit(0);
    }

    public void printErrors(List<String> errors) {
        if (!errors.isEmpty()) {
            String errorMessage = "Errores:\n" + String.join("\n", errors);
            JOptionPane.showMessageDialog(null, errorMessage, "Error", JOptionPane.ERROR_MESSAGE);
            this.frame.dispose(); // cerrar la ventana
        }
    }

    public void save() {
        List<String> info = new ArrayList<>();
        for (Decoder singleOrder : this.orders) {
            System.out.println("Nombre: " + singleOrder.getName());

            int idCliente = 0; // Inicializamos el idCliente fuera del bloque if
            boolean isNew;
            int idInsertIfClientNotExists = -1;

            if (insert.findCustomer(singleOrder.getPhone())) {
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
            } else {
                info.add("Se registrará al cliente " + singleOrder.getName()
                + " como un nuevo cliente. Número de teléfono: " + singleOrder.getPhone());
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

        this.showMessageDialog(info);
    }

    public void showMessageDialog(List<String> info) {
        if(info.size() == 0){
            return;
        }
        StringBuilder messageBuilder = new StringBuilder();
        messageBuilder.append("Información:\n");
        for (String item : info) {
            messageBuilder.append("• ").append(item).append("\n");
        }
        String message = messageBuilder.toString().trim();
        JOptionPane.showMessageDialog(null, message, "Información", JOptionPane.INFORMATION_MESSAGE);
    }

    public void runPanelEdit() {
        runPanelEditRecursive(0);
    }

    private void runPanelEditRecursive(int currentIndex) {
        int totalOrders = this.orders.size();
        if (currentIndex >= totalOrders) {
            return; // Kill al proceso si ya termino de leer todos las orders
        }

        Decoder singleOrder = this.orders.get(currentIndex);
        JFrame editFrame = new JFrame();
        editFrame.setTitle("Escaneando Pedido " + (currentIndex + 1) + " de " + totalOrders);
        editFrame.setTitle(editFrame.getTitle() + " (" + singleOrder.getName() + ") ");
        editFrame.setTitle(editFrame.getTitle() + this.getTypeOrderByInputTypeOrder(singleOrder.getTypeOrder()));
        editFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        editFrame.setSize(this.screenWidth - 200, this.screenHeight - 300);
        editFrame.setLocationRelativeTo(null);

        JPanel editPanel = new JPanel(new GridLayout(2, 1));
       
       /*  editPanel.setLayout(new GridLayout(0, 2)); */


        JPanel infoPanel = new JPanel(new GridLayout(0, 2));
        JPanel buttonPanel = new JPanel(new GridLayout(0, 3));
        buttonPanel.setPreferredSize(new Dimension(this.screenWidth, this.calculatePixels(20, this.screenHeight)));
        



        JTextField nameField = new JTextField(singleOrder.getName());
        infoPanel.add(new JLabel("Nombre:"));
        infoPanel.add(nameField);

        JTextField phoneField = new JTextField(singleOrder.getPhone());
        infoPanel.add(new JLabel("Teléfono:"));
        infoPanel.add(phoneField);

        JTextField cityField = new JTextField(singleOrder.getCity());
        infoPanel.add(new JLabel("Ciudad:"));
        infoPanel.add(cityField);

        String deparmentValue = singleOrder.getValue("Departamento");
        JTextField departmentField = new JTextField(deparmentValue);
        infoPanel.add(new JLabel("Departamento:"));
        infoPanel.add(departmentField);

        JTextField exactAddressField = new JTextField(singleOrder.getAllAddress());
        infoPanel.add(new JLabel("Direccion exacta:"));
        infoPanel.add(exactAddressField);

        JTextField totalAmountField = new JTextField(singleOrder.getTotal());
        infoPanel.add(new JLabel("Total producto:"));
        infoPanel.add(totalAmountField);

        JTextField deliveryDateField = new JTextField(singleOrder.getDeliveryDate());
        infoPanel.add(new JLabel("Fecha entrega:"));
        infoPanel.add(deliveryDateField);

        Map<JComboBox<String>, JTextField> productFields = new LinkedHashMap<>();

        for (Map.Entry<String, Integer> entry : singleOrder.getProducts().entrySet()) {
            String productNameInput = entry.getKey();
            int productQuantityInput = entry.getValue();

            JComboBox<String> productComboBox = this.createProductComboBox(productNameInput);
            JTextField productFieldCantidad = new JTextField(Integer.toString(productQuantityInput));
            infoPanel.add(new JLabel("Producto:"));
            infoPanel.add(productComboBox);
            infoPanel.add(new JLabel("Cantidad:"));
            infoPanel.add(productFieldCantidad);
            productFields.put(productComboBox, productFieldCantidad);
        }





        Map<Integer, String> deliveryMap = ProductsRepository.getDeliveryMap();
        List<Integer> idList = new ArrayList<>(deliveryMap.keySet());
        List<String> nameList = new ArrayList<>(deliveryMap.values());
        infoPanel.add(new JLabel("Envios:"));
        JComboBox<String> deliveryComboBox = new JComboBox<>(nameList.toArray(new String[0]));
        String selectedShipping = this.findClosestShipping(Extractor.getValues(deliveryMap), singleOrder.getShipping());
        int selectedIndex = nameList.indexOf(selectedShipping);
        if (selectedIndex != -1) {
            deliveryComboBox.setSelectedIndex(selectedIndex);
        }
        infoPanel.add(deliveryComboBox);






        Map<Integer, String> typesOrder = OrderTypeRespository.getTypeOrderMap();
        List<Integer> idListTypeOrders = new ArrayList<>(typesOrder.keySet());
        List<String> nameListTypeOrders = new ArrayList<>(typesOrder.values());
        infoPanel.add(new JLabel("Tipo de orden:"));
        JComboBox<String> typeOrdersComboBox = new JComboBox<>(nameListTypeOrders.toArray(new String[0]));
        String selectTypeOrder = StringSimilarityFinder.findMostSimilarString(singleOrder.getTypeOrder(),
                nameListTypeOrders);
        int selectedTypeOrderIndex = nameListTypeOrders.indexOf(selectTypeOrder);
        if (selectedTypeOrderIndex != -1) {
            typeOrdersComboBox.setSelectedIndex(selectedTypeOrderIndex);
        }
        infoPanel.add(typeOrdersComboBox);





        final String[] selectedDeliveryNameChange = { (String) deliveryComboBox.getSelectedItem() };
        final Integer[] selectedDeliveryIdChange = { idList.get(nameList.indexOf(selectedDeliveryNameChange[0])) };

        double price = 0.0;
        if (VentaDetallePlusRepository.getPriceDeliveryById(selectedDeliveryIdChange[0]) != null) {
            price = Double.parseDouble(VentaDetallePlusRepository.getPriceDeliveryById(selectedDeliveryIdChange[0]));
        }
        final double[] deliveryPrice = { price };

        // Inicializar el JTextField con el total inicial
        double initialTotal = Double.parseDouble(singleOrder.getTotal());
        singleOrder.setTotalToPay(roundToTwoDecimals(initialTotal + deliveryPrice[0]));
        JTextField total = new JTextField(String.valueOf(roundToTwoDecimals(initialTotal + deliveryPrice[0])));
        infoPanel.add(new JLabel("Total a pagar:"));
        infoPanel.add(total);

        deliveryComboBox.addActionListener(e -> {
            // Utilizar la variable final
            selectedDeliveryNameChange[0] = (String) deliveryComboBox.getSelectedItem();
            selectedDeliveryIdChange[0] = idList.get(nameList.indexOf(selectedDeliveryNameChange[0]));
            deliveryPrice[0] = Double
                    .parseDouble(VentaDetallePlusRepository.getPriceDeliveryById(selectedDeliveryIdChange[0]));

            double currentTotal = Double.parseDouble(singleOrder.getTotal());
            double newTotal = roundToTwoDecimals(currentTotal + deliveryPrice[0]);
            singleOrder.setTotalToPay(newTotal);
            // Actualizar el JTextField con el nuevo total
            total.setText(String.valueOf(newTotal));
        });

        
        JButton nextButton = new JButton("Siguiente");
        nextButton.addActionListener(e -> {
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
            singleOrder.setData("total", totalAmountField.getText());
            singleOrder.setData("delivery date", deliveryDateField.getText());

            /*
             * Este codigo que esta aca sirve para detectar mutabilidad en los datos del
             * formulario
             * en la parte de productos, el map esta de esta manera:
             * nameProduct -> quantity
             */
            Map<String, Integer> updatedProducts = new LinkedHashMap<>();
            for (Map.Entry<JComboBox<String>, JTextField> entry : productFields.entrySet()) {
                String productName = (String) entry.getKey().getSelectedItem();
                int productQuantity = Integer.parseInt(entry.getValue().getText());
                updatedProducts.put(productName, productQuantity);
            }

            Map<String, Integer> mainMapProducts = singleOrder.getProducts();
            Map<String, Integer> finalProducts = new LinkedHashMap<>(mainMapProducts);
            finalProducts.keySet().retainAll(updatedProducts.keySet());
            for (Map.Entry<String, Integer> entry : updatedProducts.entrySet()) {
                String productName = entry.getKey();
                Integer updatedQuantity = entry.getValue();
                finalProducts.put(productName, updatedQuantity);
            }

            singleOrder.editData(finalProducts);

            String selectedDeliveryName = (String) deliveryComboBox.getSelectedItem();
            Integer selectedDeliveryId = idList.get(nameList.indexOf(selectedDeliveryName));
            singleOrder.setDelivery(selectedDeliveryId, selectedDeliveryName);

            String selectedTypeOrderName = (String) typeOrdersComboBox.getSelectedItem();
            Integer selectedTypeOrderId = idListTypeOrders.get(nameListTypeOrders.indexOf(selectedTypeOrderName));
            singleOrder.setTypeOrder(selectedTypeOrderId, selectedTypeOrderName);


          /*   this.updateTextArea(); */
            runPanelEditRecursive(currentIndex + 1);
            editFrame.dispose();
           

        });

        buttonPanel.add(nextButton);


        JButton prevButton  = new JButton("Atras");
        prevButton.addActionListener(e -> {
            singleOrder.setData("name", nameField.getText());
            singleOrder.setData("phone", phoneField.getText());
            singleOrder.setData("city", departmentField.getText() + " - " + cityField.getText());
            singleOrder.setData("address", exactAddressField.getText());
            singleOrder.setData("price", totalAmountField.getText());
            /* singleOrder.setData("shipping", shippingField.getText()); */
            singleOrder.setData("total", totalAmountField.getText());
            singleOrder.setData("delivery date", deliveryDateField.getText());

            /*
             * Este codigo que esta aca sirve para detectar mutabilidad en los datos del
             * formulario
             * en la parte de productos, el map esta de esta manera:
             * nameProduct -> quantity
             */
            Map<String, Integer> updatedProducts = new LinkedHashMap<>();
            for (Map.Entry<JComboBox<String>, JTextField> entry : productFields.entrySet()) {
                String productName = (String) entry.getKey().getSelectedItem();
                int productQuantity = Integer.parseInt(entry.getValue().getText());
                updatedProducts.put(productName, productQuantity);
            }

            Map<String, Integer> mainMapProducts = singleOrder.getProducts();
            Map<String, Integer> finalProducts = new LinkedHashMap<>(mainMapProducts);
            finalProducts.keySet().retainAll(updatedProducts.keySet());
            for (Map.Entry<String, Integer> entry : updatedProducts.entrySet()) {
                String productName = entry.getKey();
                Integer updatedQuantity = entry.getValue();
                finalProducts.put(productName, updatedQuantity);
            }

            singleOrder.editData(finalProducts);

            String selectedDeliveryName = (String) deliveryComboBox.getSelectedItem();
            Integer selectedDeliveryId = idList.get(nameList.indexOf(selectedDeliveryName));
            singleOrder.setDelivery(selectedDeliveryId, selectedDeliveryName);

            String selectedTypeOrderName = (String) typeOrdersComboBox.getSelectedItem();
            Integer selectedTypeOrderId = idListTypeOrders.get(nameListTypeOrders.indexOf(selectedTypeOrderName));
            singleOrder.setTypeOrder(selectedTypeOrderId, selectedTypeOrderName);


          /*   this.updateTextArea(); */
            runPanelEditRecursive(currentIndex - 1);
            editFrame.dispose();
           

        });

        buttonPanel.add(prevButton);


        JButton cancelButton = new JButton("Cancelar orden");
        cancelButton.setBackground(new Color(255, 200, 200));
        cancelButton.addActionListener(e -> {
            String orderName = singleOrder.getName();
            int opcion = JOptionPane.showConfirmDialog(
                null,
                "¿Está seguro de eliminar la orden de " + orderName + "?\nEsta acción no se puede deshacer.",
                "Confirmar eliminación",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
            );
        
            if (opcion == JOptionPane.YES_OPTION) {
                this.wasDeleteOrder = true;
                this.orders.remove(currentIndex);
                JOptionPane.showMessageDialog(
                    null,
                    "La orden de " + orderName + " ha sido eliminada con éxito.",
                    "Orden eliminada",
                    JOptionPane.INFORMATION_MESSAGE
                );
            }
            runPanelEditRecursive(currentIndex);
            editFrame.dispose();
        });

        buttonPanel.add(cancelButton);


        Dimension infoPanelSize = new Dimension(screenWidth, calculatePixels(50, screenHeight));
        infoPanel.setPreferredSize(infoPanelSize);
        infoPanel.setMinimumSize(infoPanelSize);
        infoPanel.setMaximumSize(infoPanelSize);
        
        Dimension buttonPanelSize = new Dimension(screenWidth, calculatePixels(30, screenHeight));
        buttonPanel.setPreferredSize(buttonPanelSize);
        buttonPanel.setMinimumSize(buttonPanelSize);
        buttonPanel.setMaximumSize(buttonPanelSize);
        

        
        editPanel.add(infoPanel);
        editPanel.add(buttonPanel);
        editFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        editFrame.add(editPanel);


        
        editFrame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                editFrame.dispose();
                runPanelEditRecursive(currentIndex + 1);
                // handleWindowClosing(singleOrder, currentIndex, totalOrders, editFrame);
            }
        });

        editFrame.setVisible(true);

    }

    private JComboBox<String> createProductComboBox(String initialProduct) {
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

    private double roundToTwoDecimals(double value) {
        return Math.round(value * 100.0) / 100.0;
    }

    public String findClosestShipping(List<String> nameList, String desiredValue) {
        if (desiredValue == null || desiredValue.isEmpty() || desiredValue.equals("0") || desiredValue.equals("0.0")) {
            return nameList.get(0); // Default to "No shipping" if desiredValue is empty
        }

        double desiredAmount = Double.parseDouble(desiredValue);

        double minDifference = Double.MAX_VALUE;
        String closestShipping = null;

        for (String shippingName : nameList) {
            String shippingValue = shippingName.replaceAll("[^\\d.]+", "");
            if (!shippingValue.isEmpty()) {
                double shippingAmount = Double.parseDouble(shippingValue);
                double difference = Math.abs(desiredAmount - shippingAmount);

                if (difference < minDifference) {
                    minDifference = difference;
                    closestShipping = shippingName;
                }
            }
        }

        return closestShipping;
    }

    public String getTypeOrderByInputTypeOrder(String inputTypeOrder) {
        Map<Integer, String> typesOrder = OrderTypeRespository.getTypeOrderMap();
        List<String> nameListTypeOrders = new ArrayList<>(typesOrder.values());
        return StringSimilarityFinder.findMostSimilarString(inputTypeOrder,
                nameListTypeOrders);
    }

    public  int calculatePixels(double percentage, int totalHeight) {
        if (percentage < 0 || percentage > 100) {
            throw new IllegalArgumentException("El porcentaje debe estar entre 0 y 100.");
        }

        return (int) Math.round((percentage / 100) * totalHeight);
    }
}
