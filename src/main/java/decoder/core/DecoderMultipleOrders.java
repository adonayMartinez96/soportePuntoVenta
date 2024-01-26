package decoder.core;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;

import Kernel.openia.OpenAIResponse;
import Kernel.openia.PromptProcessorGpt;

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
        if (orderBlocks.length <= 1) {
            this.errors.add("No se encontró el delimitador '" + headerStartOrder + "'");
            return;
        }
    
        for (String block : orderBlocks) {
            if (!block.trim().isEmpty()) {
                PromptProcessorGpt promptProcessor = new PromptProcessorGpt(block);
                Gson gson = new Gson();
                String jsonResponse = promptProcessor.getResponse();
                String response = "";
                System.out.println(jsonResponse);
                if (jsonResponse != null) {
                    OpenAIResponse openAIResponse = gson.fromJson(jsonResponse, OpenAIResponse.class);
                
                    if (openAIResponse != null && openAIResponse.getChoices() != null && !openAIResponse.getChoices().isEmpty()) {
                        response = openAIResponse.getChoices().get(0).getText();
                        // Resto del código
                    } else {
                        this.errors.add("La respuesta de gpt viene vacia");
                        System.out.println(promptProcessor.getResponse()); break;
                    }
                } else {
                    this.errors.add("La respuesta json de gpt viene vacia");
                    System.out.println(promptProcessor.getResponse()); break;
                }
                
                Decoder decoder = new Decoder(response);
                if(decoder.existError()){
                    this.errors.addAll(decoder.getErrors());
                    return;
                }
                this.checkRequiredKeys(decoder, requiredKeys);
                orders.add(decoder);
            }
        }
    }
    

    private void checkRequiredKeys(Decoder decoder, List<String> requiredKeys) {
        List<String> notFoundKeys = new ArrayList<>();
    
        for (String requiredKey : requiredKeys) {
            String[] keyParts = requiredKey.split("\\|");
            boolean found = false;
    
            for (String part : keyParts) {
                if (!decoder.getValue(decoder.normalizeString(part)).isEmpty()) {
                    found = true;
                    break;
                }
            }
    
            if (!found) {
                notFoundKeys.add(requiredKey);
            }
        }
    
        if (!notFoundKeys.isEmpty()) {
            String errorMessage = "No se encontraron las siguientes claves requeridas: " +
                    String.join(", ", notFoundKeys) +
                    ". Revise su ortografía y continúe.";
            this.errors.add(errorMessage);
        }
    }

    public List<Decoder> getOrders() {
        return orders;
    }
}
