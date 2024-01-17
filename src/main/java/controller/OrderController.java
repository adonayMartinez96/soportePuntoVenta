package controller;

import java.util.List;

import Models.ClientesDireccionesDomicilios;
import Repositories.ClientesDireccionesDomiciliosRepository;
import decoder.core.Decoder;
import org.apache.commons.text.similarity.LevenshteinDistance;

public class OrderController {

    public int idClient;
    public Decoder data;
    public boolean isNew;
    public int addressSaveId;

    public OrderController(int idClient, Decoder data, boolean isNew) {
        this.idClient = idClient;
        this.data = data;
        this.isNew = isNew;
        if (this.isNew) {
            this.addressSaveId = this.getIdAddressProcessed();
        }

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
