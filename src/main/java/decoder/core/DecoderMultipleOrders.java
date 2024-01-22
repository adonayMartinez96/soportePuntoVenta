package decoder.core;

import java.util.ArrayList;
import java.util.List;

public class DecoderMultipleOrders {

    private List<Decoder> orders;
    private String ordersString;
    private String headerStartOrder;
    private List<String> requiredKeys;

    private List<String> errors;

    public DecoderMultipleOrders(String orders) {
        this.headerStartOrder = "Confirmando los datos de su pedido";
        this.ordersString = orders;
        this.orders = new ArrayList<>();
        this.errors = new ArrayList<>();
        this.requiredKeys = new ArrayList<>();
       // this.decodeOrders();
    }

    public String getHeaderStartOrder() {
        return this.headerStartOrder;
    }

    public void setHeaderStarOrder(String headerStartOrder) {
        this.headerStartOrder = headerStartOrder;
    }

    public List<String> getRequiredKeys() {
        return requiredKeys;
    }

    public void setRequiredKeys(List<String> newRequiredKeys) {
        requiredKeys = newRequiredKeys;
    }

    public void addRequiredKey(String key) {
        requiredKeys.add(key);
    }

    public List<String> getErrors() {
        return this.errors;
    }

    public boolean existError(){
        return this.errors.size() != 0;
    }

    public void decode() {
        String[] orderBlocks = ordersString.split(headerStartOrder);
        for (String block : orderBlocks) {
            if (!block.trim().isEmpty()) {
                Decoder decoder = new Decoder(block);
                this.checkRequiredKeys(decoder, requiredKeys);
                orders.add(decoder);
            }
        }
    }

    private void checkRequiredKeys(Decoder decoder, List<String> requiredKeys) {
        for (String key : requiredKeys) {
            if (decoder.getValue(decoder.normalizeString(key)).isEmpty()) {
                this.errors.add("Falta el campo " + key + ", revise su ortografía y continúe");
            }

        }
    }

    public List<Decoder> getOrders() {
        return orders;
    }
}
