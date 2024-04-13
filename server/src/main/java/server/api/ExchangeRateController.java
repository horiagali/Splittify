package server.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@RestController
@RequestMapping("/api/exchange-rates")
public class ExchangeRateController {

    /**
     * Endpoint for fetching pseudo exchange rates with EUR as the base currency.
     * @param symbols The target currency.
     * @param date the date of the rate
     * @return ResponseEntity containing pseudo exchange rates.
     */
    @GetMapping("/pseudo")
    public ResponseEntity<Map<String, Double>> getPseudoExchangeRates(
            @RequestParam("symbols") String symbols,
            @RequestParam("date") LocalDate date) {
        Map<String, Double> pseudoRates = generatePseudoRates(symbols, date);
        System.out.println("these are the rates: " + pseudoRates.toString());
        System.out.println(pseudoRates.getOrDefault(date + symbols,null) + "was gotten");
        return ResponseEntity.ok(pseudoRates);
    }

    /**
     * generates a fake rate from euro to symbol
     * @param symbols
     * @return
     */
    private Map<String, Double> generatePseudoRates(String symbols, LocalDate currentDate) {
        Map<String, Double> baseRates = new HashMap<>();


        System.out.println("Generating rate for: " + currentDate.getDayOfMonth()+
                currentDate.getMonth() + currentDate.getYear() + symbols
                );
        baseRates.put("USD", 1.2);
        baseRates.put("CHF", 1.1);
        baseRates.put("RON",4.9);
        baseRates.put("EUR",1.0);
        Map<String, Double> pseudoRates = new HashMap<>();
        Random random = new Random(hashDateAndSymbols(currentDate, symbols));
            if (baseRates.containsKey(symbols) && !symbols.equals("EUR")) {
                double baseRate = baseRates.get(symbols);
                double adjustment = (random.nextDouble() * 0.2) - 0.1;
                double pseudoRate = baseRate * (1 + adjustment);
                pseudoRates.put(currentDate.toString()+symbols, pseudoRate);

        }
        if (symbols.equals("EUR")) pseudoRates.put(currentDate + symbols,1.0);

        return pseudoRates;
    }

    private long hashDateAndSymbols(LocalDate date, String symbols) {
        String combined = date.toString() + symbols;
        return combined.hashCode();
    }
}
