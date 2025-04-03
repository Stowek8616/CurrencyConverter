import java.util.HashMap;
import java.util.Map;
import javax.swing.*;

public class CurrencyConverterTest {
    public static void main(String[] args) 
    {
        // Define exchange rates (static for now, can be updated dynamically)
        Map<String, Double> exchangeRates = new HashMap<>();
        exchangeRates.put("Egyptian Pound (EGP)", 50.62); // 1 USD = 50.62 EGP
        exchangeRates.put("Nigerian Naira (NGN)", 1536.31); // 1 USD = 1536.31 NGN
        exchangeRates.put("South African Rand (ZAR)", 18.80); // 1 USD = 18.80 ZAR
        exchangeRates.put("Saudi Riyal (SAR)", 3.75); // 1 USD = 3.75 SAR
        exchangeRates.put("United Arab Emirates Dirham (AED)", 3.67); // 1 USD = 3.67 AED

        // Get USD amount from user
        String usdInput = JOptionPane.showInputDialog("Enter amount in USD:");
        double usdAmount = Double.parseDouble(usdInput);

        // Select currency
        Object[] currencies = exchangeRates.keySet().toArray();
        String selectedCurrency = (String) JOptionPane.showInputDialog(
                null,
                "Select a currency:",
                "Currency Converter",
                JOptionPane.QUESTION_MESSAGE,
                null,
                currencies,
                currencies[0]);
        // Convert currency
        double convertedAmount = usdAmount * exchangeRates.get(selectedCurrency);

        // Display result
        JOptionPane.showMessageDialog(null, 
                String.format("%.2f USD = %.2f %s", usdAmount, convertedAmount, selectedCurrency)); 
    }
}
