package client.utils;

import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * The Currency class holds exchange rates from Euro to other currencies.
 */
public class Currency {
    private static String currencyUsed = "EUR";   /// either EUR RON USD or CHF
    private static double EUR_TO_USD = 1.18;
    private static double EUR_TO_CHF = 1.10;
    private static double EUR_TO_RON = 4.95;

    /**
     * Get the exchange rate from Euro to USD.
     *
     * @return The exchange rate from Euro to USD.
     */
    public static double getRateUSD() {
        return EUR_TO_USD;
    }

    /**
     * Get the exchange rate from Euro to CHF.
     *
     * @return The exchange rate from Euro to CHF.
     */
    public static double getRateCHF() {
        return EUR_TO_CHF;
    }

    /**
     * Get the exchange rate from Euro to RON.
     *
     * @return The exchange rate from Euro to RON.
     */
    public static double getRateRON() {
        return EUR_TO_RON;
    }

    /**
     * gets the used currency
     * @return
     */
    public static String getCurrencyUsed() {
        return currencyUsed;
    }

    /**
     * Get the exchange rate from Euro to the current currency.
     *
     * @return The exchange rate from Euro to the specified currency.
     */
    public static double getRate() {
        switch (currencyUsed) {
            case "USD":
                return EUR_TO_USD;
            case "CHF":
                return EUR_TO_CHF;
            case "RON":
                return EUR_TO_RON;
            default:
                throw new IllegalArgumentException("Unsupported currency code: " + currencyUsed);
        }
    }

    /**
     *
     * @param currencyUsed
     * sets the currency used
     */
    public static void setCurrencyUsed(String currencyUsed) {
        Currency.currencyUsed = currencyUsed;
    }

    /**
     * Update the currency exchange rates with a daily fluctuation of ±10%.
     */
    public static void updateExchangeRates() {
        Random random = new Random();
        double fluctuation = random.nextDouble() * 0.2 - 0.1;
        EUR_TO_USD *= (1 + fluctuation);
        fluctuation = random.nextDouble() * 0.2 - 0.1;
        EUR_TO_CHF *= (1 + fluctuation);
        fluctuation = random.nextDouble() * 0.2 - 0.1;
        EUR_TO_RON *= (1 + fluctuation);
    }

    /**
     * Schedule daily update of currency exchange rates.
     */
    public static void scheduleDailyUpdate() {
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(Currency::updateExchangeRates, 0, 1, TimeUnit.DAYS);
    }
}
