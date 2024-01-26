package Kernel.config;

import io.github.cdimascio.dotenv.Dotenv;

public class Config {
    private static final Dotenv dotenv = Dotenv.configure().load();

    public static String getApiKeyOpenIA() {
        return dotenv.get("API_KEY_OPENIA");
    }

    
}
