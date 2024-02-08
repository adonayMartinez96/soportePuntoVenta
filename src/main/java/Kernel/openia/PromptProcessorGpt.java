package Kernel.openia;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import Kernel.config.Config;

public class PromptProcessorGpt {

    private String input;
    private String prompt;
    private String model = "gpt-3.5-turbo-instruct";
    private String headerOrderBlock;
    private List<String> error;


    private OpenAIResponse res;

    public PromptProcessorGpt(String input, String type) {
        this.input = input;
        this.error = new ArrayList<>();
        this.prompt = buildPrompt(type);
    }

    public PromptProcessorGpt(String input, String type, String headerOrderBlock) {
        this.headerOrderBlock = headerOrderBlock;
        this.input = input;
        this.prompt = buildPrompt(type);
    }

    public String buildPrompt(String type) {
        if (type.equals("block")) {
            return this.getPromtBlock();
        } else if (type.equals("all")) {
            return this.getPromptAllBlocks();
        } else if (type.equals("time")) {
            return this.getPromptDeleteFormatTimeBlocks();
        }
        return "";
    }

    public String getPromt() {
        return this.prompt;
    }

    public String getPromtBlock() {
        return this.normalizeString(new StringBuilder("Dado el siguiente texto de un pedido:")
                .append(this.input)
                .append("Realiza las siguientes transformaciones:")
                .append("1. Elimina la linea que contiene Direccion exacta.")
                .append("2. Si la linea Dirección exacta tiene información redundante, crea una nueva línea con la etiqueta Referencia: seguida ")
                .append("de la información redundante.")
                .append("3. En la línea Descripción de compra o donde estén los productos, usa la etiqueta Productos: seguida de la lista de productos en el formato dado. La lista de productos debe incluir la cantidad y el tipo de producto. Usa el siguiente formato como guía: \"Productos: [cantidad][nombre de producto] separa cada producto con el signo mas '+', si no ves este signo, significa que es solamente un producto y ya no tienes que agregar el signo '+' - Ejemplo: COMBOS SMARTWATCH D20 NEGRO + AUDIFONOS F9-5C NEGRO. - Ejemplo: 2 Combos D20 + D20 + F9 C5 BLANCO. - Ejemplo: F9 5C NEGRO. Si hay solamente un producto, el formato puede ser \"Productos: [nombre de producto]\" sin el signo \"+\" como el ultimo ejemplo que es solamente un producto")
                .append(" - Asegúrate ")
                .append("de manejar correctamente los casos donde la cantidad es 1 o no está presente.4. Si la línea de productos contiene información sobre el envío con un precio, crea una nueva línea con la etiqueta Envío: seguida del precio del envío.5. Usa la etiqueta Comentario: seguida del comentario del cliente, si está presente.6. Ignora la línea ¿Es correcto su pedido? MANDADITOS ? ya que no es un comentario relevante. 7. Si en alguna parte del texto ves la palabra Mandaditos o 44 EXPRESS, ponlo en la linea 'Tipo' de esta manera: 'tipo: Mandaditos' si no encuentras la palabra mandaditos pon el tipo como San Salvador de esta manera: 'tipo: San Salvador'")
                .append("Genera la salida en el formato siguiente:")
                .append("Nombre: [Nombre]")
                .append("Teléfono: [Teléfono]")
                .append("Ciudad: [Ciudad]")
                .append("Departamento: [Departamento]")
                .append("Referencia: [Referencia]")
                .append("Productos: [Productos]")
                .append("Total producto: [Total producto]")
                .append("Envío: [Envío]")
                .append("Fecha de entrega: [Fecha de entrega]")
                .append("Comentario: [Comentario]")
                .append("tipo: [Tipo]")
                .toString());
    }

    public String getPromptAllBlocks() {
        return normalizeString(new StringBuilder(
                "Dado un texto copiado de WhatsApp que podría contener la hora, por favor, realiza las siguientes tareas:")
                .append("\n\n1. Remueve cualquier hora presente en el texto. Si no hay hora, déjalo sin cambios.")
                .append("\n\n2. Utiliza como cabecera del mensaje \"" + this.headerOrderBlock
                        + "\" al inicio de cada bloque.")
                .append("\n\n3. Proporciona el texto puro de cada bloque justo debajo del header.")
                .append("\n\nEjemplo de formato esperado para cada bloque:")
                .append("\n" + this.headerOrderBlock)
                .append("\nInformación del bloque aquí.")
                .append("\n\nEste es el texto copiado de WhatsApp:")
                .append("\n" + this.input).toString());
    }

    public String getPromptDeleteFormatTimeBlocks() {
        return normalizeString(new StringBuilder(
                "Dado un texto copiado de WhatsApp que podría contener la hora, por favor, realiza las siguientes tareas:")
                .append("\\n 1. Remueve cualquier hora presente en el texto. Si no hay hora, déjalo sin cambios.")
                .append("\n 2. Si la linea dice 'fecha de entrega' o aperecido, esta fecha no se la quites")
                .append("\n 3. El pedazo de texto (formato de fecha) tendra el siguiente formato: [hour:minuts time., d/m/Y] username")
                .append("donde 'username' es el usuario de WhatsApp donde se copio el texto, elimina toda esta linea")
                .append("Este es el texto: \n")
                .append(this.input).toString());
    }

    public String getResponseJson() {
        try {
            OpenAIClient openAIClient = new OpenAIClient(Config.getApiKeyOpenIA());
            String response = openAIClient.createCompletion(this.model, this.getPromt());
            return response;
        } catch (Exception e) {
            System.out.println("Error");
            return "";
        }
    }

    public String normalizeString(String key) {
        String normalizedString = Normalizer.normalize(key, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "");
        normalizedString = normalizedString.replace('ñ', 'n').replace('Ñ', 'N');
        return normalizedString.toLowerCase();
    }

    public OpenAIResponse getResponse() {
        try {
            Gson gson = new Gson();
            String json = this.getResponseJson();
            OpenAIResponse response = gson.fromJson(json, OpenAIResponse.class);
            response.setRawJson(json);  
            return response;
        } catch (Exception e) {
            System.out.println("Error");
            return null;
        }
    }
    

    public String getResponseTxt() {

        try {
            this.res = this.getResponse();
            return this.res.getChoices().get(0).getText();
        } catch (Exception e) {
            System.out.println("Respuesta nulla de gpt: ");
            System.out.println(this.res.getRawJson());
            this.error.add("La respuesta vino vacia y no se por que xd");
        }
        return "";
    }


    public List<String> getErrors(){
        return this.error;
    }
}
