import java.awt.Color;
import java.awt.Font;
import java.awt.event.*;
import java.util.HashMap;
import java.util.Map;
import javax.swing.*;


public class KyleCurrencyConverter {
    public static void main(String[] args) {
        // Set UI
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            
            // Minimal UI enhancements
            Font mainFont = new Font("Arial", Font.PLAIN, 15);
            UIManager.put("OptionPane.messageFont", mainFont);
            UIManager.put("TextField.font", mainFont);
            UIManager.put("ComboBox.font", mainFont);
            UIManager.put("Button.font", mainFont);
        } catch (Exception e) {
            // Default Look if it fails
        }
        
        // Define exchange rates
        Map<String, Double> exchangeRates = new HashMap<>();
        exchangeRates.put("Egyptian Pound (EGP)", 50.62);
        exchangeRates.put("Nigerian Naira (NGN)", 1536.31);
        exchangeRates.put("South African Rand (ZAR)", 18.80);
        exchangeRates.put("Saudi Riyal (SAR)", 3.75);
        exchangeRates.put("United Arab Emirates Dirham (AED)", 3.67);
        
        // Make input text box
        JTextField inputField = new JTextField(10);
        inputField.setBorder(BorderFactory.createCompoundBorder(inputField.getBorder(), BorderFactory.createEmptyBorder(3, 3, 3, 3)
        ));
        
        JPanel inputPanel = new JPanel();
        inputPanel.add(new JLabel("Enter amount in USD:"));
        inputPanel.add(inputField);
        
        int result = JOptionPane.showConfirmDialog(null, inputPanel, "Currency Converter - USD Input", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result != JOptionPane.OK_OPTION) return; // Exit if cancelled
        

        // Get USD from user
        String usdInput = inputField.getText();
        if (usdInput == null || usdInput.trim().isEmpty()) return; // Exit if filed is empty
        
        double usdAmount;
        try {
            usdAmount = Double.parseDouble(usdInput);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Invalid input. Please enter a numeric value.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Create searchable JComboBox
        JComboBox<String> comboBox = new JComboBox<>(exchangeRates.keySet().toArray(new String[0]));
        comboBox.setEditable(true);
        comboBox.setBackground(Color.WHITE);
        
        // Add search functionality
        JTextField editor = (JTextField) comboBox.getEditor().getEditorComponent();
        editor.setBorder(BorderFactory.createCompoundBorder( editor.getBorder(), BorderFactory.createEmptyBorder(3, 3, 3, 3)));
        
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
        
        // Add text showing the amount being converted
        JPanel selectionPanel = new JPanel();
        selectionPanel.setLayout(new BoxLayout(selectionPanel, BoxLayout.Y_AXIS));
        selectionPanel.add(new JLabel("Converting: " + String.format("%.2f USD", usdAmount)));
        selectionPanel.add(Box.createVerticalStrut(5));
        selectionPanel.add(new JLabel("Select target currency:"));
        selectionPanel.add(Box.createVerticalStrut(5));
        selectionPanel.add(comboBox);
        
        // Show the dropdown inside JOptionPane
        int option = JOptionPane.showConfirmDialog(null, selectionPanel,"Select a Currency", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        
        // Process selection
        
        if (option == JOptionPane.OK_OPTION) {
            String selectedCurrency = (String) comboBox.getSelectedItem();
            if (selectedCurrency != null && exchangeRates.containsKey(selectedCurrency)) {
                double convertedAmount = usdAmount * exchangeRates.get(selectedCurrency);
                
                // Display Result in HTML
                String resultMessage = String.format("<html><div width='200px' align='center'>"+
                "%.2f USD</b> equals<br>"+
                "<span style='color:#1976D2; font-size:14pt'><b>%.2f %s</b></span><br>" +
                "<small>Exchange rate: 1 USD = %.2f</small></div></html>",
                usdAmount, convertedAmount, selectedCurrency, exchangeRates.get(selectedCurrency));
                JLabel resultMessageLabel = new JLabel(resultMessage);
                JOptionPane.showMessageDialog(null, resultMessageLabel, "Conversion Result",JOptionPane.PLAIN_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(null, "Invalid selection.");
            }
        }
    }
}