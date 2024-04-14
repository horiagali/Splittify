package client.utils;

import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.Map;

/**
 * The Currency class holds exchange rates from Euro to other currencies.
 */
public class Currency {
    private static String currencyUsed = "EUR";
    private static final RestTemplate restTemplate = new RestTemplate();
    private static final String
            EXCHANGE_RATES_URL = "http://localhost:8080/api/exchange-rates/pseudo";


    /**
     * Rounds up to 2 decimals.
     *
     * @param x the number to round
     * @return the rounded number
     */
    public static double round(double x) {
        return Math.round(x * 100.0) / 100.0;
    }

    /**
     * Gets the used currency.
     *
     * @return the used currency
     */
    public static String getCurrencyUsed() {
        return currencyUsed;
    }

    /***
     * gets the hash map with currencies after adding a new key with the params
     * @param symbols
     * @param date
     * @return returns the map
     */
    public static Map<String, Double> getCurrencies(String symbols, LocalDate date) {
        String url = EXCHANGE_RATES_URL + "?symbols=" + symbols + "&date=" + date.toString();

        try {
            ResponseEntity<Map> responseEntity = restTemplate.getForEntity(url, Map.class);

            if (responseEntity.getStatusCode().is2xxSuccessful()) {
                Map<String, Double> exchangeRates = responseEntity.getBody();
                System.out.println("Received exchange rates: " + exchangeRates);
                return exchangeRates;
            } else {
                throw new RuntimeException("Failed to fetch currencies and exchange rates." +
                        " Status code: " + responseEntity.getStatusCodeValue());
            }

        } catch (Exception e) {
            throw new RuntimeException("Error fetching exchange rates: " + e.getMessage(), e);
        }
    }



    /**
     * Get the exchange rate from Euro to the current currency.
     * @param date the date of the rate
     * @return The exchange rate from Euro to the specified currency.
     * @throws RateFetchException if an error occurs while fetching the exchange rate
     */
    public static double getRate(LocalDate date) {
        Map<String, Double> map = getCurrencies(currencyUsed,date);
        System.out.println("this are the rates" + map.toString());
        Double rate = map.getOrDefault(date + currencyUsed,null);
        if (rate == null) {
            throw new RateFetchException("Exchange rate not available for "
                    + currencyUsed + " on " + date);
        }
        return rate;
    }

    /**
     * Sets the currency used.
     *
     * @param currencyUsed the currency to set
     */
    public static void setCurrencyUsed(String currencyUsed) {
        Currency.currencyUsed = currencyUsed;
    }

    /**
     * Custom unchecked exception for rate fetching errors.
     */
    public static class RateFetchException extends RuntimeException {
        /**
         * the exception
         * @param message
         */
        public RateFetchException(String message) {
            super(message);
        }
    }
}
