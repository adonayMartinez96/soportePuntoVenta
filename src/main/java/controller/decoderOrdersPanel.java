package controller;

import java.util.*;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JLabel;
import javax.swing.JTextField;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Toolkit;

import decoder.core.DecoderMultipleOrders;
import decoder.core.Decoder;

public class decoderOrdersPanel {

    List<Decoder> orders;
    int screenWidth;
    int screenHeight;

    InsertCustomer insert = new InsertCustomer();

    InsertAddress insertAddress = new InsertAddress();

    GetTextFieldValue getTextFieldValue = new GetTextFieldValue();

    List<String> productos = new ArrayList<>();

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

        JPanel panel = new JPanel();

        frame.add(panel);
        this.placeComponents(panel);
        frame.setContentPane(panel);

        frame.setVisible(true);
        System.out.println("Cliqueado");
    }

    private void placeComponents(JPanel panel) {
        panel.setLayout(null);

        JTextArea textArea = new JTextArea();
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);

        JScrollPane scrollPane = new JScrollPane(textArea);
        /* scrollPane.setBounds(10, 10, 260, 100); */
        int textAreaWidth = this.screenWidth - 80;
        int textAreaHeight = this.screenHeight - 200;
        scrollPane.setBounds(10, 10, textAreaWidth, textAreaHeight);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        panel.add(scrollPane);

        JButton scanButton = new JButton("Scanear");
        scanButton.setBounds(20, textAreaHeight + 20, 80, 25);
        scanButton.addActionListener(e -> {
            System.out.println("se preciono el scaneo");
            // Aquí colocas la lógica que deseas ejecutar al presionar el botón "Scanear"
            String text = textArea.getText();
            // Realiza lo que necesites con el texto del JTextArea
            /* System.out.println("Texto ingresado: " + text); */
            this.decodeText(text);

            this.runPanelEdit(text);
        });
        panel.add(scanButton);

        JButton save = new JButton("Guardar");
        save.setBounds(120, textAreaHeight + 20, 80, 25);
        save.addActionListener(e -> {
            this.decodeText(textArea.getText());
            System.out.println("se preciono el guardado");
           // this.debug();
            /* aca para guardar la data */
            for(Decoder singleOrder: this.orders) {
                for (Map.Entry<String, Integer> entry : singleOrder.getProducts().entrySet()) {
                    System.out.println(entry.getKey()+" - "+entry.getValue());
                }
            }

        });
        panel.add(save);
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

            int idFound = 0;
            String deparmentValue = singleOrder.getValue("Departamento");
            String addressComplete = deparmentValue+" - "+singleOrder.getCity();


            int  encontrado = insert.findCustomer(singleOrder.getPhone());
            if(encontrado ==1 ){
                System.out.println("El cliente ya esta registrado: "+encontrado);
            }else{
                System.out.println("\n listo para insertar\n");
                idFound = insert.insertCustomer(singleOrder.getName(),singleOrder.getPhone());
            }

            insertAddress.insertAddressClient(idFound,addressComplete,singleOrder.getAddress());


        }
    }

    public void runPanelEdit(String text) {
        JTextField productField = null;
        Map<String,Integer> productShows = new HashMap<>();
        int countProducts = 0;

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

            String deparmentValue = singleOrder.getValue("Departamento");
            JTextField departmentField = new JTextField(deparmentValue);
            editPanel.add(new JLabel("Departamento:"));
            editPanel.add(departmentField);

            JTextField exactAddressField = new JTextField(singleOrder.getAddress());
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

            Map<JTextField, JTextField> fiels =  new LinkedHashMap<>();


            for (Map.Entry<String, Integer> entry : singleOrder.getProducts().entrySet()) {


                JTextField productFieldName = new JTextField(entry.getKey());
                editPanel.add(new JLabel("Producto:"));
                editPanel.add(productFieldName);

                JTextField productFieldCantidad = new JTextField(Integer.toString(entry.getValue()));
                editPanel.add(new JLabel("Cantidad:"));
                editPanel.add(productFieldCantidad);

                fiels.put(productFieldName, productFieldCantidad);

            }

            // ... Otros campos de edición
            JButton saveButton = new JButton("Guardar");
            saveButton.addActionListener(e -> {
                singleOrder.setData("name", nameField.getText());
                singleOrder.setData("phone", phoneField.getText());
                singleOrder.setData("city",departmentField.getText()+" - " +cityField.getText());
                singleOrder.setData("address", exactAddressField.getText());
                singleOrder.setData("price", totalAmountField.getText());
                singleOrder.setData("shipping", shippingField.getText());
                singleOrder.setData("total", totalAmountField.getText());
                singleOrder.setData("delivery date", deliveryDateField.getText());

                //nameProduct -> quantity
                for (Map.Entry<JTextField, JTextField> entry : fiels.entrySet()) {
                    singleOrder.setData(entry.getKey().getText(), Integer.parseInt(entry.getValue().getText()));
                }

                editFrame.dispose();

            });

            editPanel.add(saveButton);
            editFrame.setVisible(true);
        }
    }
}
