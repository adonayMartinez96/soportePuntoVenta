package Kernel.whatsapp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import Kernel.components.Components;
import Kernel.openia.PromptProcessorGpt;
import Kernel.utils.Numbers;
import Kernel.utils.Print;

public class TextWhatsAppProcessorGpt {

    private String inputText;
    private String headerOrderBlock;
    private int maxCharacter = 1500; // esto es el supuesto maximo de chat gpt en su api

    /*
     * Esta constante no me gusta mucho que digamos, pero es lo minimo de caracteres
     * que puede tener una
     * orden, esto para saber que bloque es una orden y que bloque es simplemente
     * una fecha
     */
    private int minProbabilityCharacterEachBlock = 100;

    public List<String> info;

    public List<String> allresponseChatGpt;

    public List<String> errors;

    //Esto por que o si no se tienen muchas connectiones a la base y esta falla xd
    public final int MAX_ORDERS_ORDERS = 50; 

    public TextWhatsAppProcessorGpt(String inputText, String headerOrderBlock) {
        this.errors = new ArrayList<>();
        this.allresponseChatGpt = new ArrayList<>();
        this.info = new ArrayList<>();
        this.inputText = this.sanitizeString(inputText, this.rules());
        this.headerOrderBlock = headerOrderBlock.toLowerCase();
        this.verifyMaxOrders();
        this.builderTextResponseProcessGtp();
    }


    public void verifyMaxOrders() {
        int orderCount = countOccurrences(inputText, headerOrderBlock);
        if (orderCount > MAX_ORDERS_ORDERS) {
            String message = String.format("Has intentado insertar %d órdenes. " + 
            " Por favor, intenta insertar un máximo de %d órdenes. ¡El programa se cerrará!",
                                           orderCount, MAX_ORDERS_ORDERS);
            Components.showDialog(message);
            System.exit(0);
        }
    }
    

    /*
     * Esta es una funcion de reglas, si encuentra una key, la va a reemplazar
     * por el value
     */
    public Map<String, String> rules() {
        Map<String, String> rules = new HashMap<>();
        rules.put("producto", "productos");
        /* Con este tipo de caracteres hay que tener cuidado
         * por que no basta con solamente hacer que la request
         * acepte todos los UTF_8, a veces parece ser gtp trabandose
         * o algo asi
         */
        rules.put("ç", "");
        return rules;
    }

    /*
     * Esta funcion solo retornara un true si input tiene en alguna parte el
     * formato:
     * [hours:minutes time., day/M/year]
     * 
     * si tiene este formato significa que vino desde WhatasApp en una cadena grande
     */
    public boolean isDirty() {
        return this.comprobateFormatTextTimeWhatsAppOfList(
                this.extractTextsBetweenBrackets(this.inputText));
    }

    public boolean isDirty(String text) {
        return this.comprobateFormatTextTimeWhatsAppOfList(
                this.extractTextsBetweenBrackets(text));
    }

    public int countOccurrences(String block, String substring) {
        int counter = 0;
        int index = block.indexOf(substring);

        while (index != -1) {
            counter++;
            index = block.indexOf(substring, index + 1);
        }
        return counter;
    }

    public List<String> extractTextsBetweenBrackets(String input) {
        List<String> textsBetweenBrackets = new ArrayList<>();
        boolean isInsideBrackets = false;
        String potentialText = "";
        for (int i = 0; i < input.length(); i++) {
            char character = input.charAt(i);

            if (character == '[') {
                isInsideBrackets = true;
                potentialText = "" + character; // reset the string
            }

            if (isInsideBrackets) {
                potentialText = potentialText + character;

                if (character == ']') {
                    potentialText = potentialText + character;
                    textsBetweenBrackets.add(potentialText);
                    isInsideBrackets = false;
                }
            }
        }

        return textsBetweenBrackets;
    }

    public boolean comprobateFormatTextTimeWhatsAppOfList(List<String> list) {
        for (String text : list) {
            text = text.replace("[", "").replace("]", "");
            String[] data = text.split(",");
            if (data.length == 2) {
                /* 
                 * Aca no estoy seguro si se valida el pm y el am, pero por el momento no da error jaja
                 */
                String[] hourData = data[0].split(":");
                if (hourData.length != 2 || !Numbers.isNumber(hourData[0]) || !Numbers.isNumber(hourData[1])) {
                    return true;
                }
                /* Se verifica el formato de fecha */
                String[] dateSplit = data[1].split("/");
                if (dateSplit.length != 3 || !Numbers.isNumber(dateSplit[0]) || !Numbers.isNumber(dateSplit[1])
                        || !Numbers.isNumber(dateSplit[2])) {
                    return true;
                }
            } else {
                return true;
            }
        }
        return false;
    }

    public List<String> getBuilderProcessedBlocks() {
        return this.allresponseChatGpt;
    }

    public void builderTextResponseProcessGtp() {
        if (this.isDirty()) {
            System.out.println("El input viene sucio de formato de WhatsApp");
            List<String> sets = this.blocksWithLessThanTheMaximum();
            for (int i = 0; i < sets.size(); i++) {
                System.out.println("Leyendo el conjunto " + (i + 1) + " de " + sets.size());
                String setsBlocks = sets.get(i);
                String[] blocksofSets = setsBlocks.split(this.headerOrderBlock);
                for (int j = 0; j < blocksofSets.length; j++) {
                    String block = blocksofSets[j];
                    System.out.println("Leyendo el bloque " + (j + 1) + " del conjunto " + (i + 1));
                    if (!block.trim().isEmpty()) {
                        if (this.isDirty(block)) {

                            /*
                             * System.out.println("Este es el bloque sucicio que entra: ");
                             * System.out.println(block);
                             */

                            PromptProcessorGpt promptProcessorGpt1 = new PromptProcessorGpt(block, "time");
                            this.errors.addAll(promptProcessorGpt1.getErrors());
                            String responseWithOutTime = promptProcessorGpt1.getResponseTxt();

                            PromptProcessorGpt promptProcessorGpt2 = new PromptProcessorGpt(responseWithOutTime,
                                    "block");
                            this.errors.addAll(promptProcessorGpt2.getErrors());
                            this.allresponseChatGpt.add(promptProcessorGpt2.getResponseTxt());

                        } else {
                            PromptProcessorGpt promptProcessorGpt3 = new PromptProcessorGpt(block, "block");
                            this.errors.addAll(promptProcessorGpt3.getErrors());
                            this.allresponseChatGpt.add(promptProcessorGpt3.getResponseTxt());
                        }

                    }

                }
            }

        } else {
            String[] orderBlocks = this.inputText.split(this.headerOrderBlock);
            if (orderBlocks.length <= 1) {
                this.errors.add("No se encontró el delimitador '" + this.headerOrderBlock + "'");
                return;
            }
            for (String block : orderBlocks) {
                if (!block.trim().isEmpty()) {
                    PromptProcessorGpt promptProcessorGpt4 = new PromptProcessorGpt(block, "block");
                    this.errors.addAll(promptProcessorGpt4.getErrors());
                    this.allresponseChatGpt.add(promptProcessorGpt4.getResponseTxt());
                }
            }

        }
    }

    public List<String> blocksWithLessThanTheMaximum() {
        String[] blocks = this.inputText.split(this.headerOrderBlock);
        List<String> resultBlocks = new ArrayList<>();
        String setBlocks = "";

        for (int i = 0; i < blocks.length; i++) {
            String block = blocks[i];

            if (block.length() < this.minProbabilityCharacterEachBlock) {
                this.info.add("La siguiente orden contiene únicamente " + block.length() +
                        " caracteres. El programa considera que esta cantidad es insuficiente para " +
                        "constituir una orden. ¿Es esto correcto? Orden:\n" + block);
                continue;
            }

            String blockWithHeader;

            if (i == 1) {
                blockWithHeader = this.headerOrderBlock + block;
            } else {
                blockWithHeader = "\n\n" + this.headerOrderBlock + block;
            }

            if (setBlocks.length() + blockWithHeader.length() < this.maxCharacter) {
                setBlocks += blockWithHeader;
            } else {
                resultBlocks.add(setBlocks);
                setBlocks = blockWithHeader;
            }

            if (i == blocks.length - 1) {
                resultBlocks.add(setBlocks);
            }
        }

        printInfoMethodBlocksWithLessThanTheMaximum(resultBlocks);
        return resultBlocks;
    }

    private void printInfoMethodBlocksWithLessThanTheMaximum(List<String> resultBlocks) {
        System.out.println("Se crearon " + resultBlocks.size() + " conjuntos");
        System.out.println("Hay " + this.info.size() + " info");
        for (int i = 0; i < resultBlocks.size(); i++) {
            System.out.println("El conjunto " + (i + 1) + " tiene " + resultBlocks.get(i).length() + " caracteres");
        }

    }

    public String removeEmptyLines(String text) {
        String[] lines = text.split("\\n");
        String result = String.join("\n", Arrays.stream(lines)
                .filter(line -> !line.trim().isEmpty())
                .toArray(String[]::new));
        return result;
    }

    public List<String> getErrors() {
        return this.errors;
    }

    public String sanitizeString(String inputText, Map<String, String> rules) {
        inputText = inputText.toLowerCase();
        for (Map.Entry<String, String> entry : rules.entrySet()) {
            inputText = inputText.replaceAll(entry.getKey(), entry.getValue());
        }
        return inputText;
    }

}
