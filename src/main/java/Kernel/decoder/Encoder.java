package Kernel.decoder;

import java.util.LinkedHashMap;
import java.util.Map;

public class Encoder {

    public static String encodeData(Map<String, String> data, Map<String, Integer> products) {
        StringBuilder encodedData = new StringBuilder();

        // Encode fields other than products using LinkedHashMap
        Map<String, String> linkedData = new LinkedHashMap<>(data);
        for (Map.Entry<String, String> entry : linkedData.entrySet()) {
            encodedData.append(entry.getKey()).append(": ").append(entry.getValue()).append("\n");
        }

        // Encode products
        encodedData.append("productos: ");
        for (Map.Entry<String, Integer> entry : products.entrySet()) {
            encodedData.append(entry.getValue()).append(" ").append(entry.getKey()).append(" + ");
        }
        encodedData.delete(encodedData.length() - 3, encodedData.length());  // Remove the last " + "

        return encodedData.toString();
    }

    public static String encodeData(Decoder decoder) {
        return encodeData(decoder.getData(), decoder.getProducts());
    }
}

