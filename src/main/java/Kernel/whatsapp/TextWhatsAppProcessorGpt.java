package Kernel.whatsapp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    public TextWhatsAppProcessorGpt(String inputText, String headerOrderBlock) {
        this.errors = new ArrayList<>();
        this.allresponseChatGpt = new ArrayList<>();
        this.info = new ArrayList<>();
        this.inputText = (inputText.toLowerCase());
        this.headerOrderBlock = headerOrderBlock.toLowerCase();
        this.builderTextResponseProcessGtp();
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
            // Eliminar corchetes
            text = text.replace("[", "").replace("]", "");

            String[] data = text.split(",");
            if (data.length == 2) {
                // Verificar formato de hora
                String[] hourData = data[0].split(":");
                if (hourData.length != 2 || !Numbers.isNumber(hourData[0]) || !Numbers.isNumber(hourData[1])) {
                    return true;
                }
                // Verificar formato de fecha
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

                      /*       System.out.println("Este es el bloque sucicio que entra: ");
                            System.out.println(block); */

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

}
