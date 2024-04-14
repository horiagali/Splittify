package client.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CurrencyTest {

    @Test
    void round() {
        Double x = 1.0;
        Double y = Currency.round(x);
        assertTrue(Double.compare(y, x) == 0);
    }

    @Test
    void round2() {
        double x = 1.1;
        Double y = Currency.round(x);
        assertTrue(Double.compare(y, x) == 0);
    }

    @Test
    void round3() {
        double x = 1.2;
        Double y = Currency.round(x);
        assertTrue(Double.compare(y, x) == 0);
    }

    @Test
    void getCurrencyUsed() {
        Currency.setCurrencyUsed("EUR");
        assertEquals("EUR", Currency.getCurrencyUsed());
    }
    @Test
    void getCurrencyUsed1() {
        Currency.setCurrencyUsed("CHF");
        assertEquals("CHF", Currency.getCurrencyUsed());
    }
    @Test
    void getCurrencyUsed2() {
        Currency.setCurrencyUsed("USD");
        assertEquals("USD", Currency.getCurrencyUsed());
    }
    @Test
    void getCurrencyUsed3() {
        Currency.setCurrencyUsed("RON");
        assertEquals("RON", Currency.getCurrencyUsed());
    }


    @Test
    void setCurrencyUsed() {
        Currency.setCurrencyUsed("EUR");
        assertEquals("EUR", Currency.getCurrencyUsed());
    }
    @Test
    void setCurrencyUsed2() {
        Currency.setCurrencyUsed("USD");
        assertEquals("USD", Currency.getCurrencyUsed());
    }
    @Test
    void setCurrencyUsed3() {
        Currency.setCurrencyUsed("CHF");
        assertEquals("CHF", Currency.getCurrencyUsed());
    }
    @Test
    void setCurrencyUsed4() {
        Currency.setCurrencyUsed("RON");
        assertEquals("RON", Currency.getCurrencyUsed());
    }

}