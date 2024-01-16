package controller;

import java.util.ArrayList;
import java.util.List;

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
            this.debug();
            /* aca para guardar la data */
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

            // ... Otros campos de edición

            JButton saveButton = new JButton("Guardar");
            saveButton.addActionListener(e -> {
                singleOrder.setData("name", nameField.getText());
                singleOrder.setData("phone", phoneField.getText());
                singleOrder.setData("city", cityField.getText());

                editFrame.dispose();
            });

            editPanel.add(saveButton);
            editFrame.setVisible(true);
        }
    }
}
