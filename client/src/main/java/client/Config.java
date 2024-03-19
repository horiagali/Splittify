package client;


import java.io.File;
import java.io.IOException;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.databind.ObjectMapper;


@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY, 
                getterVisibility = JsonAutoDetect.Visibility.NONE, 
                isGetterVisibility = JsonAutoDetect.Visibility.NONE, 
                setterVisibility = JsonAutoDetect.Visibility.NONE)
public class Config {
    private String serverUrl = "http://localhost:8080/";
    private String language = "romana";

    /**
     * getter for language
     * @return language
     */
    public String getLanguage() {
        return language;
    }

    /**
     * getter for server url
     * @return url
     */
    public String getServerUrl() {
        return serverUrl;
    }

    /**
     * setter for language
     * @param language
     */
    public void setLanguage(String language) {
        this.language = language;
        saveConfig(this);
    }

    /**
     * setter for url
     * @param serverUrl
     */
    public void setServerUrl(String serverUrl) {
        this.serverUrl = serverUrl;
        saveConfig(this);
    }

    private static void saveConfig(Config config) {
        var mapper = new ObjectMapper();
        var file = new File(Main.configLocation + "/config.json");
        // If file is not present, construct from default values
        if(!file.exists()) {
            throw new IllegalStateException("Config file should exist at [" 
                + file.getAbsolutePath() + "]");
        }

        try {
            mapper.writeValue(file, config);
        } catch (IOException e) {
            System.err.println("Failed save config file");
        }
    }
}