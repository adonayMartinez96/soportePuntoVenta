package decoder.core;

import java.text.Normalizer;
import java.util.*;

public class Decoder {

    private final Map<String, String> data;
    private Map<String, Integer> products;

    private double totalAmountProducts;
    private double totalToPay;
    private double shipmentCost;

    public Decoder(String input) {
        this.data = new LinkedHashMap<>();
        this.products = new LinkedHashMap<>();
        decodeData(input);
        this.totalToPay = Double.parseDouble(this.getTotal());
        this.shipmentCost = Double.parseDouble(this.getShipping());
        this.totalAmountProducts = this.totalToPay - this.shipmentCost;

    }

    private String normalizeString(String key) {
        return Normalizer.normalize(key, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "").toLowerCase();
    }

    private void decodeData(String input) {
        String[] lines = input.split("\\n");
        for (String line : lines) {
            String[] parts = line.split(":", 2);
            if (parts.length == 2) {
                String key = normalizeString(parts[0].trim());
                String value = parts[1].trim();
                if (key.startsWith("productos")) {
                    decodeProducts(value);
                } else {
                    data.put(key, value);
                }
            }
        }
    }

    private void decodeProducts(String productsString) {
        String[] productParts = productsString.split(" \\+ ");
        for (String productPart : productParts) {

            // le quito los espacios
            String productRow = productPart.replaceAll("\\s", "");
            String amount = Decoder.getInitialIntegers(productRow);
            String product = Decoder.removeInitialIntegers(productRow);
            // esto significa que no se puso la cantidad de producto asi que se supondra que
            // es solamente uno
            this.products.put(product, amount == null ? 1 : Integer.parseInt(amount));

        }
    }

    public String getValue(String key) {
        String translatedKey = translateKey(key);
        return data.getOrDefault(translatedKey.toLowerCase(),
                "No se encontró el campo " + key + ". Revisa tu ortografía y corrige.");
    }

    public static String getInitialIntegers(String texto) {
        int index = 0;
        for (int i = 0; i < texto.length(); i++) {
            if (!Character.isDigit(texto.charAt(i))) {
                index = i;
                break;
            }
        }
        return index == 0 ? null : texto.substring(0, index);
    }

    public static String removeInitialIntegers(String input) {
        int index = 0;
        while (index < input.length() && Character.isDigit(input.charAt(index))) {
            index++;
        }
        return input.substring(index);
    }

    public int getAmountProducts() {
        int amount = 0;
        for (int value : this.products.values()) {
            amount = amount + value;
        }
        return amount;
    }

    public Map<String, Integer> getProducts() {
        return this.products;
    }

    public Map<String, String> getData() {
        return this.data;
    }

    public double getUnitPrice() {
        return this.totalAmountProducts / this.getAmountProducts();
    }

    public String getName() {
        return this.getValue("name");
    }

    public String getPhone() {
        return this.getValue("phone");
    }

    public String getCity() {
        return this.getValue("city");
    }

    public String getAddress() {
        return this.getValue("address");
    }

    public String getPrice() {
        return this.getValue("price");
    }

    public String getShipping() {
        return this.getValue("shipping");
    }

    public String getTotal() {
        return this.getValue("total");
    }

    public String getDeliveryDate() {
        return this.getValue("delivery date");
    }

    public String getSalesperson() {
        return this.getValue("salesperson");
    }

    public void setData(String key, String value) {
        String translatedKey = translateKey(key);
        data.put(translatedKey.toLowerCase(), value);
    }
    
    public void setData(String key, int value) {
        String translatedKey = translateKey(key);
        products.put(translatedKey.toLowerCase(), value);
    }
    

    private String translateKey(String key) {
        switch (key.toLowerCase()) {
            case "name":
                return "nombre";
            case "phone":
                return "telefono";
            case "city":
                return "ciudad";
            case "address":
                return "direccion exacta";
            case "product":
                return "productos";
            case "price":
                return "precio";
            case "shipping":
                return "envio";
            case "total":
                return "total a pagar";
            case "delivery date":
                return "fecha de entrega";
            case "salesperson":
                return "encomendista";
            default:
                return key;
        }
    }
}
