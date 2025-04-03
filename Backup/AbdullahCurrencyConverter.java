import java.util.HashMap;
import java.util.Map;
import javax.swing.*;

//add a search bar 
//add a droup down bar (with JOPTION)

public class AbdullahCurrencyConverter {
    public static void main(String[] args) 
    {
        // Define exchange rates (static for now, can be updated dynamically)
        Map<String, Double> exchangeRates = new HashMap<>();
        //exchangeRates.put("Egyptian Pound (EGP)", 50.62); // 1 USD = 50.62 EGP
        //exchangeRates.put("Nigerian Naira (NGN)", 1536.31); // 1 USD = 1536.31 NGN
        //exchangeRates.put("South African Rand (ZAR)", 18.80); // 1 USD = 18.80 ZAR
        //exchangeRates.put("Saudi Riyal (SAR)", 3.75); // 1 USD = 3.75 SAR
        //exchangeRates.put("United Arab Emirates Dirham (AED)", 3.67); // 1 USD = 3.67 AED
        //the first 20 are The Middle East
        exchangeRates.put("Afghanistan (AFN)", 71.60);
        exchangeRates.put("Armenia (AMD)", 391.32);
        exchangeRates.put("Bahrain (BHD)", 0.38);
        exchangeRates.put("Cyprus (EUR)", 0.90);
        exchangeRates.put("Egypt (EGP)", 50.59);
        exchangeRates.put("Georgia (GEL)", 2.76);
        exchangeRates.put("Iran (IRR)", 42,1112.50);
        exchangeRates.put("Iraq (IQD)", 1,309.88 );
        exchangeRates.put("Israel (ILS)", 3.70);
        exchangeRates.put("Jordan (JOD)", 0.71);
        exchangeRates.put("Kuwait (KWD)", 0.31 );
        exchangeRates.put("Lebanon (LBP)", 89,596);
        exchangeRates.put("Oman (OMR)", 0.38);
        exchangeRates.put("Palestine (ILS)", 3.70);
        exchangeRates.put("Qatar (QAR)", 3.65);
        exchangeRates.put("Saudi Arabia (SAR)", 3.75);
        exchangeRates.put("Syria (SYP)", 13,002);
        exchangeRates.put("Turkey (TRY)", 37.95);
        exchangeRates.put("United Arab Emirates (AED)", 3.67);
        exchangeRates.put("Yemen (YER)", 245.65);
        exchangeRates.put(" ");
        exchangeRates.put(" ");
        exchangeRates.put(" ");
        exchangeRates.put(" ");
        exchangeRates.put(" ");
        exchangeRates.put(" ");
        exchangeRates.put(" ");
        exchangeRates.put(" ");
        exchangeRates.put(" ");
        exchangeRates.put(" ");
        exchangeRates.put(" ");
        exchangeRates.put(" ");
        exchangeRates.put(" ");
        exchangeRates.put(" ");
        exchangeRates.put(" ");
        exchangeRates.put(" ");
        exchangeRates.put(" ");
        exchangeRates.put(" ");
        exchangeRates.put(" ");
        exchangeRates.put(" ");
        exchangeRates.put(" ");
        exchangeRates.put(" ");
        exchangeRates.put(" ");
        exchangeRates.put(" ");
        exchangeRates.put(" ");
        exchangeRates.put(" ");
        exchangeRates.put(" ");
        exchangeRates.put(" ");
        exchangeRates.put(" ");
        exchangeRates.put(" ");
        exchangeRates.put(" ");
        exchangeRates.put(" ");
        exchangeRates.put(" ");
        exchangeRates.put(" ");
        exchangeRates.put(" ");
        exchangeRates.put(" ");
        exchangeRates.put(" ");
        exchangeRates.put(" ");
        exchangeRates.put(" ");
        exchangeRates.put(" ");
        exchangeRates.put(" ");
        exchangeRates.put(" ");
        exchangeRates.put(" ");
        exchangeRates.put(" ");
        exchangeRates.put(" ");
        exchangeRates.put(" ");
        exchangeRates.put(" ");
        exchangeRates.put(" ");
        exchangeRates.put(" ");
        exchangeRates.put(" ");
        exchangeRates.put(" ");
        exchangeRates.put(" ");
        exchangeRates.put(" ");
        exchangeRates.put(" ");
        exchangeRates.put(" ");
        exchangeRates.put(" ");
        exchangeRates.put(" ");
        exchangeRates.put(" ");
        exchangeRates.put(" ");
        exchangeRates.put(" ");
        exchangeRates.put(" ");
        exchangeRates.put(" ");
        exchangeRates.put(" ");
        exchangeRates.put(" ");
        exchangeRates.put(" ");


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
public static void switch_method(){

}
    
}
