import java.awt.event.*;
import java.util.HashMap;
import java.util.Map;
import javax.swing.*;

public class JordiCurrencyConverter {
    public static void main(String[] args) {
        // Define exchange rates
        Map<String, Double> exchangeRates = new HashMap<>();
        exchangeRates.put("Egyptian Pound (EGP)", 50.62);
        exchangeRates.put("Nigerian Naira (NGN)", 1536.31);
        exchangeRates.put("South African Rand (ZAR)", 18.80);
        exchangeRates.put("Saudi Riyal (SAR)", 3.75);
        exchangeRates.put("United Arab Emirates Dirham (AED)", 3.67);

        // Get USD amount from user
        String usdInput = JOptionPane.showInputDialog("Enter amount in USD:");
        if (usdInput == null || usdInput.trim().isEmpty()) return; // Exit if canceled
        double usdAmount;
        try {
            usdAmount = Double.parseDouble(usdInput);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Invalid input. Please enter a numeric value.");
            return;
        }

        // Create searchable JComboBox
        JComboBox<String> comboBox = new JComboBox<>(exchangeRates.keySet().toArray(new String[0]));
        comboBox.setEditable(true);

        // Add search functionality
        JTextField editor = (JTextField) comboBox.getEditor().getEditorComponent();
        editor.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                String input = editor.getText().toLowerCase();
                comboBox.removeAllItems();

                for (String currency : exchangeRates.keySet()) {
                    if (currency.toLowerCase().contains(input)) {
                        comboBox.addItem(currency);
                    }
                }

                editor.setText(input);
                comboBox.showPopup();
            }
        });

        // Show the dropdown inside JOptionPane
        int option = JOptionPane.showConfirmDialog(null, comboBox, "Select a currency",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);

        // Process selection
        if (option == JOptionPane.OK_OPTION) {
            String selectedCurrency = (String) comboBox.getSelectedItem();
            if (selectedCurrency != null && exchangeRates.containsKey(selectedCurrency)) {
                double convertedAmount = usdAmount * exchangeRates.get(selectedCurrency);
                JOptionPane.showMessageDialog(null,
                        String.format("%.2f USD = %.2f %s", usdAmount, convertedAmount, selectedCurrency));
            } else {
                JOptionPane.showMessageDialog(null, "Invalid selection.");
            }
        }
    }
}
