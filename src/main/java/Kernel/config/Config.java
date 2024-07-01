package Kernel.config;

import io.github.cdimascio.dotenv.Dotenv;

public class Config {
    private static final Dotenv dotenv = Dotenv.configure().load();

    public static String getApiKeyOpenIA() {
        return dotenv.get("API_KEY_OPEN_IA");
    }

    public static String getHostDataBase(){
        return dotenv.get("DB_HOST");
    }

    public static String getUserDataBase(){
        return dotenv.get("DB_USERNAME");
    }

    public static String getPasswordDataBase(){
        return dotenv.get("DB_PASSWORD");
    }

    public static String getPortDataBase(){
        return dotenv.get("DB_PORT");
    }


    public static String getVersionApp(){
        return dotenv.get("APP_VERSION");
    }

    public static String getExcelRute(){
        return dotenv.get("EXCEL_RUTE");
    }

}
