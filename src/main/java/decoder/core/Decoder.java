package decoder.core;

import java.text.Normalizer;
import java.util.*;

public class Decoder {

    private final Map<String, String> data;
    private Map<String, Integer> products;

    private double totalAmountProducts;
    private double totalToPay;
    private double shipmentCost;

    private String inputString;


    private Map<Integer, String> delivery;


    private List<String> errors;

    public Decoder(String input) {
        this.errors = new ArrayList<>();
        this.delivery = new LinkedHashMap<>();
        this.inputString = input;
        this.data = new LinkedHashMap<>();
        this.products = new LinkedHashMap<>();
        this.decodeData(input);
        this.totalToPay = Double.parseDouble(this.getTotal());
        this.shipmentCost = Double.parseDouble(this.getShipping());
        this.totalAmountProducts = this.totalToPay - this.shipmentCost;

    }

    public String getStringData(){
        return this.inputString;
    }

    public String normalizeString(String key) {
        return Normalizer.normalize(key, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "").toLowerCase();
    }

    private void decodeData(String input) {
        String[] lines = input.split("\\n");
        boolean productosKeyFound = false;
    
        for (int count = 0; count < lines.length; count++) {
            String line = lines[count];
    
            // Se obvia la primer alinea por que es un encabezado nada mas
            if (count == 0) {
                continue;
            }
    
            String[] parts = line.split(":", 2);
    
            if (parts.length != 2) {
                this.errors.add("Error en línea " + count + ": '" + line + "'. No se encontró ':' o el formato es incorrecto.");
                continue;
            }
    
            String key = this.normalizeString(parts[0].trim());
            String value = parts[1].trim();
    
            if (key.isEmpty() || value.isEmpty()) {
                this.errors.add("Error en línea " + count + ": '" + line + "'. La clave o el valor están vacíos.");
                continue;
            }
    
            if (key.startsWith("productos")) {
                if (productosKeyFound) {
                    this.errors.add("Error en línea " + count + ": '" + line + "'. Ya se encontró la clave 'productos' previamente.");
                    continue;
                }
                productosKeyFound = true;
                this.decodeProducts(value);
            } else {
                data.put(key, value);
            }
        }
    
        if (!productosKeyFound) {
            this.errors.add("No se encontró la clave 'productos'. Corrija y vuelva a intentarlo.");
        }
    }
    
    
    
    

    private void decodeProducts(String productsString) {
        String[] productParts = productsString.split("\\s*\\+\\s*");
    
        for (String productPart : productParts) {
            // Le quito los espacios al inicio y al final
            String productRow = productPart.trim();
            String amount = Decoder.getInitialIntegers(productRow);
            String product = Decoder.removeInitialIntegers(productRow);
    
            try {
                // Si la cantidad no está presente, se supone que es 1
                int quantity = (amount == null || amount.isEmpty()) ? 1 : Integer.parseInt(amount);
                this.products.put(this.translateKey(product).toLowerCase(), quantity);
            } catch (NumberFormatException e) {
                this.errors.add("Error al convertir la cantidad del producto: " + productRow);
            }
        }
    }
    

    public String getValue(String key) {
        String translatedKey = translateKey(key);
        return data.getOrDefault(translatedKey.toLowerCase(),
                "");

        /*
         * quite esto return data.getOrDefault(translatedKey.toLowerCase(),
         * "No se encontró el campo " + key + ". Revisa tu ortografía y corrige.");
         * 
         * por que ese mensaje solamente estara en el sistema y jamas saldra al usuario
         */
    }

    public boolean existsKey(String key) {
        return data.containsKey(this.translateKey(key).toLowerCase()) || this.getValue(key).equals("");
    }

    public boolean existError(){
        return this.errors.size() != 0;
    }

    public List<String> getErrors(){
        return this.errors;
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

    public String getComment() {
        String comment = this.getValue("comment");
        String deliveryDate = this.existsKey(this.getDateOfDelivery()) ? " - " + this.getDateOfDelivery() : "";
        return comment + deliveryDate;
    }
    

    public String getName() {
        return this.getValue("name");
    }

    public String getDateOfDelivery(){
        return this.getValue("Date of delivery");
    }

    public String getPhone() {
        return this.getValue("phone");
    }

    public String getCity() {
        return this.getValue("city");
    }

    public Map<String, String> getAddress() {
        Map<String, String> addressMap = new HashMap<>();
        addressMap.put("address", this.getCity() + " " + this.getValue("departamento"));
        addressMap.put("reference", this.getValue("referencia"));
        return addressMap;
    }

    public String getAllAddress(){
        return this.getAddress().get("address") + " - " + this.getAddress().get("reference");
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

    public void editData(Map<String, Integer> newMap) {
        this.products = newMap;
    }


    public void setDelivery(Integer id, String name){
        this.delivery.put(id, name);
    }


    public Map<Integer, String> getDelivery(){
        return this.delivery;
    }

    public Boolean existDelivery(){
        // Retornar true si hay un ID de entrega diferente de -1
        return this.getIdDelivery() != -1;
    }    

    public int getIdDelivery() {
        if (!delivery.isEmpty()) {
            Map.Entry<Integer, String> firstEntry = delivery.entrySet().iterator().next();
            return firstEntry.getKey();
        } else {
            return -1;
        }
    }

    public String getNameDelivery() {
        if (!delivery.isEmpty()) {
            Map.Entry<Integer, String> firstEntry = delivery.entrySet().iterator().next();
            return firstEntry.getValue();
        } else {
            return "Sin entrega";
        }
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
                return "direccion exacta"; /* Esto ya no va a venir en el input xd */
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
            case "comment":
                return "comentario";
            case "Date of delivery":
                return "fecha de entrega";
            default:
                return key;
        }
    }
}
