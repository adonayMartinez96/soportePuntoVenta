package Kernel.openia;

import com.google.gson.Gson;

import Kernel.config.Config;
import Kernel.http.HttpClientManager;

import java.io.IOException;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;

public class OpenAIClient {

    private final String apiKey;
    private final HttpClientManager httpClientManager;

    public OpenAIClient(String apiKey) {
        this.apiKey = apiKey;
        this.httpClientManager = new HttpClientManager();
    }

    public String createCompletion(String model, String prompt) throws IOException {
        String apiUrl = "https://api.openai.com/v1/completions";

        Map<String, Object> requestData = new HashMap<>();
        requestData.put("prompt", prompt);
        requestData.put("model", model);
        requestData.put("max_tokens", 2000);
        requestData.put("temperature", 0);
        Gson gson = new Gson();
        String requestBody = gson.toJson(requestData);
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        headers.put("Authorization", "Bearer " + apiKey);
        return httpClientManager.sendPostRequest(apiUrl, headers, requestBody);
    }


    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    public static String generateRandomString(int length) {
        SecureRandom random = new SecureRandom();
        StringBuilder stringBuilder = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int randomIndex = random.nextInt(CHARACTERS.length());
            char randomChar = CHARACTERS.charAt(randomIndex);
            stringBuilder.append(randomChar);
        }

        return stringBuilder.toString();
    }


    
}
