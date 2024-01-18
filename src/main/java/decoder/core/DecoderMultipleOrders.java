package decoder.core;

import java.util.ArrayList;
import java.util.List;

public class DecoderMultipleOrders {


    List<Decoder> orders;
    String ordersString;
    String headerStartOrder;


    public DecoderMultipleOrders(String orders) {
        this.headerStartOrder = "Confirmando los datos de su pedido";
        this.ordersString = orders;
        this.orders = new ArrayList<>();
        this.decodeOrders();
    }

    public String getHeaderStartOrder(){
        return this.headerStartOrder;
    }

    private void decodeOrders() {
        String[] orderBlocks = ordersString.split(headerStartOrder);

        for (String block : orderBlocks) {
            if (!block.trim().isEmpty()) {
                Decoder decoder = new Decoder(block);
                orders.add(decoder);
            }
        }
    }

    public List<Decoder> getOrders() {
        return orders;
    }
}
