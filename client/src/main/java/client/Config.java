package client;


import com.fasterxml.jackson.annotation.JsonAutoDetect;


@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY, 
                getterVisibility = JsonAutoDetect.Visibility.NONE, 
                isGetterVisibility = JsonAutoDetect.Visibility.NONE, 
                setterVisibility = JsonAutoDetect.Visibility.NONE)
public class Config {
    private String serverUrl;
    private String language;

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
    }

    /**
     * setter for url
     * @param serverUrl
     */
    public void setServerUrl(String serverUrl) {
        this.serverUrl = serverUrl;
    }
}