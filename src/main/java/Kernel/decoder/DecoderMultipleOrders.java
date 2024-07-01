package Kernel.decoder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

import com.google.gson.Gson;

import Kernel.openia.OpenAIResponse;
import Kernel.openia.PromptProcessorGpt;
import Kernel.whatsapp.TextWhatsAppProcessorGpt;

public class DecoderMultipleOrders {

    private List<Decoder> orders;
    private String headerStartOrder = "Confirmando los datos de su pedido";
    private List<String> requiredKeys;

    private List<String> errors;


    private List<String> processedBlocks;
    public DecoderMultipleOrders(String orders) {
        TextWhatsAppProcessorGpt textWhatsAppProcessorGpt = new TextWhatsAppProcessorGpt(orders, this.headerStartOrder);
        this.processedBlocks = textWhatsAppProcessorGpt.getBuilderProcessedBlocks();
        this.orders = new ArrayList<>();
        this.errors = new ArrayList<>();
        this.requiredKeys = new ArrayList<>();
        this.errors.addAll(textWhatsAppProcessorGpt.getErrors());
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

    public boolean existError() {
        return this.errors.size() != 0;
    }

    public void decode() {
        for(String block : this.processedBlocks){
            Decoder decoder = new Decoder(block, this.headerStartOrder);
            if (decoder.existError()) {
                this.errors.addAll(decoder.getErrors());
                return;
            }
            this.checkRequiredKeys(decoder, requiredKeys);
            orders.add(decoder);
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
