package Kernel.decoder;

import java.util.List;

public class EncoderMultipleOrders {

    public static String encodeOrders(List<Decoder> orders, String headerStartOrder) {
        StringBuilder encodedOrders = new StringBuilder();

        for (Decoder order : orders) {
            encodedOrders.append(headerStartOrder).append("\n");
            encodedOrders.append(Encoder.encodeData(order)).append("\n\n");
        }

        return encodedOrders.toString();
    }
}
