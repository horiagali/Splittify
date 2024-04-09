package client;


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.databind.ObjectMapper;

import client.utils.ServerUtils;


@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY, 
                getterVisibility = JsonAutoDetect.Visibility.NONE, 
                isGetterVisibility = JsonAutoDetect.Visibility.NONE, 
                setterVisibility = JsonAutoDetect.Visibility.NONE)
public class Config {
    private String serverUrl = "http://localhost:8080/";
    private String language = "english";
    private ArrayList<Long> eventIds;

    private String username;

    private String password;
    private String host;
    private Integer port;

    /**
     * getter for host
     * @return host
     */
    public String getHost() {
        return host;
    }

    /**
     * setter for host
     * @param host
     */
    public void setHost(String host) {
        this.host = host;
    }

    /**
     * getter for port
     * @return port
     */
    public Integer getPort() {
        return port;
    }

    /**
     * setter for port
     * @param port
     */
    public void setPort(Integer port) {
        this.port = port;
    }

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
     * getter for event Ids;
     * @return and arraylist of ids
     */
    public ArrayList<Long> getEventIds() {
        return eventIds;
    }

    /**
     * removes an id from the list
     * @param id to remove
     */
    public void removeId(Long id) {
        eventIds.remove(id);
        saveConfig(this, serverUrl);
    }

    /**
     * adds id to list
     * @param id to add
     */
    public void addId(Long id) {
        if(!eventIds.contains((Object) id))
        eventIds.add(id);
    }

    /**
     * setter for language
     * @param language
     */
    public void setLanguage(String language) {
        this.language = language;
        saveConfig(this, getServerUrl());
    }

    /**
     * setter for url
     * @param serverUrl
     */
    public void setServerUrl(String serverUrl) {
        ServerUtils.setServer(serverUrl);
        this.serverUrl = serverUrl;
        saveConfig(this, serverUrl);
    }

    /**
     * getter for email
     * @return the email
     */
    public String getUsername() {
        return username;
    }

    /**
     * setter for email
     * @param email
     */
    public void setUsername(String email) {
        this.username = email;
    }

    /**
     * getter for password
     * @return the password
     */
    public String getPassword() {
        return password;
    }

    /**
     * setter for password
     * @param password
     */
    public void setPassword(String password) {
        this.password = password;
    }

    private static void saveConfig(Config config, String serverUrl) {
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