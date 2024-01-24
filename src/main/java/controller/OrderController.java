package controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.swing.JOptionPane;

import Models.ClientesDireccionesDomicilios;
import Repositories.ClientesDireccionesDomiciliosRepository;
import Repositories.OrdersRepository;
import Repositories.ProductsRepository;
import Repositories.VentaDetallePlusRepository;
import decoder.core.Decoder;
import org.apache.commons.text.similarity.LevenshteinDistance;

public class OrderController {

    public int idClient;
    public Decoder data;
    public boolean isNew;
    public int addressSaveId;
    public int orderIdInsert;
    public String orderDateInsert;
    public List<Integer> insertsIdsSalesProductsInsert;
    public List<Map<String, Object>> dataInsertSaleProduct;
    public List<Map<String, Object>> dataInsertSaleComment;

    public OrderController(int idClient, Decoder data, boolean isNew) {
        this.idClient = idClient;
        this.data = data;
        this.isNew = isNew;
        if (this.isNew) {
            this.addressSaveId = this.getIdAddressProcessed();
        }
        // esto por que la orden puede devolver un string o un int
        Map<String, Object> order = this.insertOrder();
        this.orderIdInsert = (int) order.get("id");
        this.orderDateInsert = (String) order.get("date");
        this.dataInsertSaleProduct = this.insertSaleProduct();
        this.insertsIdsSalesProductsInsert = VentaDetallePlusRepository.extractInsertIds(this.dataInsertSaleProduct);
        this.dataInsertSaleComment = this.insertSaleComment();

        System.out.println("Id de orden insertado: " +  this.orderIdInsert);
        System.out.println("Fecha: " + this.orderDateInsert);

    }

    private int getIdAddressProcessed() {
        ClientesDireccionesDomiciliosRepository conn = new ClientesDireccionesDomiciliosRepository();
        List<ClientesDireccionesDomicilios> addressList = conn.getDireccionesByCliente(this.idClient);

        boolean emptyReference = this.data.getAddress().get("reference") == null
                || this.data.getAddress().get("reference").isEmpty();

        for (ClientesDireccionesDomicilios address : addressList) {
            int id = address.getId();
            int clientId = address.getIdCliente();
            String addressText = address.getDireccion();
            String reference = address.getReferencia();

            String inputAddress = this.data.getAddress().get("address");
            String inputReference = this.data.getAddress().get("reference");

            int addressDistance = LevenshteinDistance.getDefaultInstance().apply(addressText, inputAddress);
            int addressSimilarityThreshold = 5;
            int referenceSimilarityThreshold = 5;

            if (emptyReference && addressDistance <= addressSimilarityThreshold) {
                System.out.println("The address is similar, it won't be saved.");
                return id;
            } else if (!emptyReference) {
                int referenceDistance = LevenshteinDistance.getDefaultInstance().apply(reference, inputReference);

                if (addressDistance <= addressSimilarityThreshold
                        && referenceDistance <= referenceSimilarityThreshold) {
                    System.out.println("Both the address and the reference are similar, it won't be saved.");
                    return id;
                } else if (addressDistance <= addressSimilarityThreshold) {
                    System.out.println(
                            "The address is similar, but the reference is different. A new row will be created.");
                    return this.insertNewAddress(clientId, inputAddress, inputReference);
                } else if (referenceDistance <= referenceSimilarityThreshold) {
                    System.out.println(
                            "The reference is similar, but the address is different. A new row will be created.");
                    return this.insertNewAddress(clientId, inputAddress, inputReference);
                }
            }
        }

        System.out.println(
                "The client " + this.data.getName() + " didn't have any set address, but a new one was assigned: "
                        + this.data.getAddress().get("address"));
        // This will only be executed if the client's addresses list is empty, so it
        // needs to be inserted
        return this.insertNewAddress(idClient, this.data.getAddress().get("address"),
                this.data.getAddress().get("reference"));

    }

    private int insertNewAddress(int idClient, String addressComplete, String reference) {
        InsertAddress insertAddress = new InsertAddress();
        return insertAddress.insertAddressClient(idClient, addressComplete, reference);
    }

    public void setAddressSaveId(int address) {
        this.addressSaveId = address;
    }

    private Map<String, Object> insertOrder() {
        return OrdersRepository.insertOrder(
                this.data.getAddress().get("address"),
                this.data.getAddress().get("reference"),
                this.data.getName(),
                this.data.getPhone());
    }

    private List<Map<String, Object>> insertSaleProduct() {
        List<Map<String, Object>> data = new ArrayList<>(); // Declarar aquí

        Map<String, Integer> products = this.data.getProducts();

        Integer count = 0;
        for (Map.Entry<String, Integer> entry : products.entrySet()) {
            count = count + 1;
            String productNameInput = entry.getKey();
            Integer productQuantityInput = entry.getValue();

            int idProduct = ProductsRepository.getIdProductByName(productNameInput);

            if (idProduct != -1) {
                String productName = ProductsRepository.getProductNameById(idProduct);

               data = VentaDetallePlusRepository.insertMultiplesSales(
                idProduct,
                this.data.getUnitPrice(),
                this.orderDateInsert,
                this.orderIdInsert,
                productName,
                productQuantityInput,
                count);

                /* aca se cambio por data.addAll por que ps eso creo que no tiene sentido xd */
            } else {
                JOptionPane.showMessageDialog(null,
                        "El producto: '" + productNameInput + "' no fue encontrado en la base de datos.",
                        "Producto no encontrado", JOptionPane.WARNING_MESSAGE);

                System.out.println("Producto: " + productNameInput + ", Cantidad: " + productQuantityInput);
            }
        }

        return data;
    }

    private List<Map<String, Object>> insertSaleComment() {
        if (!this.data.existsKey(this.data.getComment())) {
            return Collections.emptyList(); // Retorna una lista vacía si la clave no existe
        }

        return VentaDetallePlusRepository.insertRowForCommet(
                this.data.getComment(),
                this.orderDateInsert,
                this.orderIdInsert,
                VentaDetallePlusRepository.extractInsertLastSequence(this.dataInsertSaleProduct),
                VentaDetallePlusRepository.extractInsertLastSequence(this.dataInsertSaleProduct) + 1);
    }

    @Override
    public String toString() {
        return "AddressUpdateInfo{" +
                "idClient=" + idClient +
                ", data=" + data +
                ", isNew=" + isNew +
                ", addressSaveId=" + addressSaveId +
                '}';
    }

}
