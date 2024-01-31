package Kernel.openia;

import java.text.Normalizer;


import Kernel.config.Config;

public class PromptProcessorGpt {

    private String block;
    private String prompt;
    private String model;

    public PromptProcessorGpt(String block) {
        this.block = block;
        this.prompt = buildPrompt();
        this.model = "gpt-3.5-turbo-instruct";
    }

    public String buildPrompt() {
        return this.normalizeString(new StringBuilder("Dado el siguiente texto de un pedido:")
                .append(this.block)
                .append("Realiza las siguientes transformaciones:")
                .append("1. Elimina la linea que contiene Direccion exacta.")
                .append("2. Si la linea Dirección exacta tiene información redundante, crea una nueva línea con la etiqueta Referencia: seguida ")
                .append("de la información redundante. 3. En la línea Descripción de compra o donde estén los productos, usa la etiqueta Productos: seguida de la lista de ")
                .append("productos en el formato dado: Productos: [productos].iterable.   - Ejemplo: COMBOS SMARTWATCH D20 NEGRO + AUDIFONOS F9-5C NEGRO.   - Asegúrate ")
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

    public String getPromt() {
        return this.prompt;
    }

    public String getResponse() {
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
        // Normalizar y eliminar tildes
        String normalizedString = Normalizer.normalize(key, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "");

        // Reemplazar tildes invertidas
        normalizedString = normalizedString.replace('ñ', 'n').replace('Ñ', 'N');

        // Convertir a minúsculas
        return normalizedString.toLowerCase();
    }
}
