package client.utils;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class CurrencyTest {

    @Test
    void round() {
        double x = 1;
        assertEquals(1.0, Currency.round(x));
    }

    @Test
    void getCurrencyUsed() {
        Currency.setCurrencyUsed("EUR");
        assertEquals("EUR", Currency.getCurrencyUsed());
    }

    @Test
    void getCurrencies() {
        Map<String, Double> mp = Currency.getCurrencies("EUR", LocalDate.now());
        assertNotNull(mp);
    }

    @Test
    void setCurrencyUsed() {
        Currency.setCurrencyUsed("EUR");
        assertEquals("EUR", Currency.getCurrencyUsed());
    }
}