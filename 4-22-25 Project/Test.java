/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.mycompany.kylecurrencyconverter;

import java.awt.*;
import java.awt.event.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.*;
import java.util.prefs.Preferences;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.text.*;

/**
 * KyleCurrencyConverter - A currency conversion application that allows users
 to convert between USD and various foreign currencies with customizable fees.
 */
public class KyleCurrencyConverter extends JFrame {
    // Application constants
    private static final String APP_NAME = "Jordi Currency Converter";
    private static final String PREF_LAST_CURRENCY = "lastCurrency";
    private static final String PREF_LAST_AMOUNT = "lastAmount";
    private static final String PREF_FEE_PERCENTAGE = "feePercentage";
    
    // UI Components
    private final JTextField amountField;
    private final JComboBox<String> currencyCombo;
    private final JLabel resultLabel;
    private final JButton convertButton;
    private final JButton switchButton;
    private final JLabel directionLabel;
    private final JTextField feeField;
    private final JCheckBox applyFeeCheckbox;
    
    // Data model and preferences
    private final CurrencyModel model;
    private final Preferences prefs;
    private boolean convertToUSD = false;

    /**
     * Application entry point
     * @param args command line arguments (not used)
     */
    public static void main(String[] args) {
        try {
            // Set system look and feel for better UI integration
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        // Launch the application on the Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            KyleCurrencyConverter app = new KyleCurrencyConverter();
            app.setVisible(true);
        });
    }

    /**
     * Constructor: Creates the main application window and initializes all components
     */
    public KyleCurrencyConverter() {
        super(APP_NAME);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        // Initialize user preferences and currency data model
        prefs = Preferences.userNodeForPackage(KyleCurrencyConverter.class);
        model = new CurrencyModel();
        
        // Create main panel with borders
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        // Create panel for input fields in a grid layout
        JPanel inputPanel = new JPanel(new GridLayout(5, 2, 5, 10));
        
        // Direction indicator
        directionLabel = new JLabel("USD → Foreign Currency");
        directionLabel.setFont(directionLabel.getFont().deriveFont(Font.BOLD));
        JPanel directionPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        directionPanel.add(directionLabel);
        
        // Amount input field
        JLabel amountLabel = new JLabel("Amount:");
        amountField = new JTextField(prefs.get(PREF_LAST_AMOUNT, "1.00"), 10);
        
        // Configure amount field to accept only numeric input
        configureDecimalTextField(amountField);
        
        // Currency selection dropdown
        JLabel currencyLabel = new JLabel("Currency:");
        currencyCombo = new JComboBox<>();
        currencyCombo.setEditable(true);
        
        // Setup fee components
        JLabel feeLabel = new JLabel("Fee (%):");
        feeField = new JTextField(prefs.get(PREF_FEE_PERCENTAGE, "2.5"), 5);
        configureDecimalTextField(feeField);
        
        applyFeeCheckbox = new JCheckBox("Apply fee to conversion");
        
        // Configure searchable dropdown with autocomplete
        setupSearchableComboBox();
        
        // Result display
        resultLabel = new JLabel("Enter an amount and select a currency");
        resultLabel.setFont(resultLabel.getFont().deriveFont(Font.BOLD));
        
        // Action buttons
        convertButton = new JButton("Convert");
        switchButton = new JButton("Switch Direction");
        
        // Add components to input panel
        inputPanel.add(amountLabel);
        inputPanel.add(amountField);
        inputPanel.add(currencyLabel);
        inputPanel.add(currencyCombo);
        inputPanel.add(feeLabel);
        inputPanel.add(feeField);
        inputPanel.add(new JLabel()); // Empty cell for spacing
        inputPanel.add(applyFeeCheckbox);
        
        // Button panel with centered layout
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        buttonPanel.add(convertButton);
        buttonPanel.add(switchButton);
        
        // Results panel with border and title
        JPanel resultPanel = new JPanel(new BorderLayout());
        resultPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("Result"),
                new EmptyBorder(10, 10, 10, 10)));
        resultPanel.add(resultLabel, BorderLayout.CENTER);
        
        // Add panels to main layout
        mainPanel.add(directionPanel, BorderLayout.NORTH);
        mainPanel.add(inputPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        // Create a wrapper panel for main content and result
        JPanel wrapperPanel = new JPanel(new BorderLayout());
        wrapperPanel.add(mainPanel, BorderLayout.CENTER);
        wrapperPanel.add(resultPanel, BorderLayout.SOUTH);
        
        add(wrapperPanel);
        
        // Load currency data
        model.loadRates();
        refreshCurrencyList();
        
        // Restore previous settings from preferences
        String lastCurrency = prefs.get(PREF_LAST_CURRENCY, null);
        if (lastCurrency != null && model.isCurrencyAvailable(lastCurrency)) {
            currencyCombo.setSelectedItem(lastCurrency);
        }
        
        // Add action listeners
        convertButton.addActionListener(e -> performConversion());
        
        switchButton.addActionListener(e -> {
            convertToUSD = !convertToUSD;
            updateDirectionLabel();
        });
        
        // Allow Enter key to trigger conversion
        amountField.addActionListener(e -> performConversion());
        feeField.addActionListener(e -> performConversion());
        
        // Set window size and position
        setSize(450, 350);
        setLocationRelativeTo(null);
    }
    
    /**
     * Configures text field to accept only numeric/decimal input
     * @param textField the text field to configure
     */
    private void configureDecimalTextField(JTextField textField) {
        ((AbstractDocument) textField.getDocument()).setDocumentFilter(new DocumentFilter() {
            @Override
            public void insertString(DocumentFilter.FilterBypass fb, int offset, String string, AttributeSet attr) 
                    throws BadLocationException {
                if (isValidDecimalInput(fb.getDocument().getText(0, fb.getDocument().getLength()) + string)) {
                    super.insertString(fb, offset, string, attr);
                }
            }
            
            @Override
            public void replace(DocumentFilter.FilterBypass fb, int offset, int length, String text, AttributeSet attrs) 
                    throws BadLocationException {
                String currentText = fb.getDocument().getText(0, fb.getDocument().getLength());
                String newText = currentText.substring(0, offset) + text + 
                                 currentText.substring(offset + length);
                if (isValidDecimalInput(newText)) {
                    super.replace(fb, offset, length, text, attrs);
                }
            }
            
            private boolean isValidDecimalInput(String text) {
                // Allow empty string
                if (text.isEmpty()) {
                    return true;
                }
                
                // Check for valid decimal format
                try {
                    // Check if it's a valid decimal number with optional decimal point
                    if (text.matches("^\\d*\\.?\\d*$")) {
                        return true;
                    }
                } catch (NumberFormatException e) {
                    return false;
                }
                return false;
            }
        });
    }
    
    /**
     * Sets up the combo box with search/autocomplete functionality
     */
    private void setupSearchableComboBox() {
        JTextField editor = (JTextField) currencyCombo.getEditor().getEditorComponent();
        editor.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                // Skip if up/down arrow keys are pressed (to navigate through dropdown)
                if (e.getKeyCode() == KeyEvent.VK_UP || e.getKeyCode() == KeyEvent.VK_DOWN) {
                    return;
                }
                
                String input = editor.getText().toLowerCase();
                if (input.isEmpty()) {
                    refreshCurrencyList();
                    return;
                }
                
                // Filter currencies based on input text
                DefaultComboBoxModel<String> filteredModel = new DefaultComboBoxModel<>();
                for (String currency : KyleCurrencyConverter.this.model.getCurrencies()) {
                    if (currency.toLowerCase().contains(input)) {
                        filteredModel.addElement(currency);
                    }
                }
                
                // Update dropdown with filtered results
                currencyCombo.setModel(filteredModel);
                currencyCombo.setSelectedItem(input);
                currencyCombo.showPopup();
            }
        });
    }
    
    /**
     * Refreshes the currency dropdown with all available currencies
     */
    private void refreshCurrencyList() {
        DefaultComboBoxModel<String> model = new DefaultComboBoxModel<>();
        for (String currency : this.model.getCurrencies()) {
            model.addElement(currency);
        }
        currencyCombo.setModel(model);
    }
    
    /**
     * Updates the direction label to show current conversion direction
     */
    private void updateDirectionLabel() {
        if (convertToUSD) {
            directionLabel.setText("Foreign Currency → USD");
        } else {
            directionLabel.setText("USD → Foreign Currency");
        }
    }
    
    /**
     * Performs the conversion calculation and updates the result
     */
    private void performConversion() {
        try {
            // Get and validate input amount
            String amountText = amountField.getText().trim();
            if (amountText.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter an amount.",
                        "Input Error", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            BigDecimal amount;
            try {
                amount = new BigDecimal(amountText);
                if (amount.compareTo(BigDecimal.ZERO) < 0) {
                    JOptionPane.showMessageDialog(this, "Please enter a positive amount.",
                            "Input Error", JOptionPane.WARNING_MESSAGE);
                    return;
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid amount format. Please enter a valid number.",
                        "Input Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Get selected currency
            String selectedCurrency = (String) currencyCombo.getSelectedItem();
            if (selectedCurrency == null || !model.isCurrencyAvailable(selectedCurrency)) {
                JOptionPane.showMessageDialog(this, "Please select a valid currency.",
                        "Input Error", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            // Get fee percentage if applicable
            BigDecimal feePercentage = BigDecimal.ZERO;
            if (applyFeeCheckbox.isSelected()) {
                try {
                    String feeText = feeField.getText().trim();
                    if (!feeText.isEmpty()) {
                        feePercentage = new BigDecimal(feeText);
                        if (feePercentage.compareTo(BigDecimal.ZERO) < 0) {
                            JOptionPane.showMessageDialog(this, "Fee percentage cannot be negative.",
                                    "Input Error", JOptionPane.WARNING_MESSAGE);
                            return;
                        }
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Invalid fee format. Please enter a valid number.",
                            "Input Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }
            
            // Perform conversion based on direction
            BigDecimal result;
            BigDecimal feeAmount = BigDecimal.ZERO;
            String currencyCode = getCurrencyCode(selectedCurrency);
            
            if (convertToUSD) {
                // Convert from foreign currency to USD
                result = model.convertToUSD(amount, selectedCurrency);
                
                // Calculate and apply fee if needed
                if (applyFeeCheckbox.isSelected() && feePercentage.compareTo(BigDecimal.ZERO) > 0) {
                    feeAmount = result.multiply(feePercentage.divide(new BigDecimal("100")));
                    result = result.subtract(feeAmount);
                }
                
                // Format the result display
                DecimalFormat df = new DecimalFormat("#,##0.00");
                resultLabel.setText(String.format("<html>%s %s = %s USD<br>%s</html>", 
                        df.format(amount), 
                        currencyCode, 
                        df.format(result),
                        formatFeeDisplay(feeAmount, feePercentage)));
            } else {
                // Convert from USD to foreign currency
                result = model.convertFromUSD(amount, selectedCurrency);
                
                // Calculate and apply fee if needed
                if (applyFeeCheckbox.isSelected() && feePercentage.compareTo(BigDecimal.ZERO) > 0) {
                    feeAmount = result.multiply(feePercentage.divide(new BigDecimal("100")));
                    result = result.subtract(feeAmount);
                }
                
                // Format the result display
                DecimalFormat df = new DecimalFormat("#,##0.00");
                resultLabel.setText(String.format("<html><span style='color:#008000'>%s USD = %s %s<br>%s</html>", 
                        df.format(amount),
                        df.format(result), 
                        currencyCode,
                        formatFeeDisplay(feeAmount, feePercentage)));
            }
            
            // Save preferences
            prefs.put(PREF_LAST_CURRENCY, selectedCurrency);
            prefs.put(PREF_LAST_AMOUNT, amountText);
            prefs.put(PREF_FEE_PERCENTAGE, feeField.getText().trim());
            
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error during conversion: " + ex.getMessage(),
                    "Conversion Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }
    
    /**
     * Creates a formatted fee display string
     * @param feeAmount the calculated fee amount
     * @param feePercentage the fee percentage
     * @return formatted string for display
     */
    private String formatFeeDisplay(BigDecimal feeAmount, BigDecimal feePercentage) {
        if (feeAmount.compareTo(BigDecimal.ZERO) > 0) {
            DecimalFormat df = new DecimalFormat("#,##0.00");
            return String.format("Fee (%s%%): %s", 
                    feePercentage.toString(), 
                    df.format(feeAmount));
        }
        return "";
    }
    
    /**
     * Extracts the currency code from the full currency name
     * @param currencyName full currency name (e.g., "United States Dollar (USD)")
     * @return currency code (e.g., "USD")
     */
    private String getCurrencyCode(String currencyName) {
        int openParenIndex = currencyName.indexOf('(');
        int closeParenIndex = currencyName.indexOf(')');
        
        if (openParenIndex >= 0 && closeParenIndex > openParenIndex) {
            return currencyName.substring(openParenIndex + 1, closeParenIndex);
        }
        return currencyName;
    }
    
    /**
     * Currency model class handling currency data and conversion logic
     */
    private static class CurrencyModel {
        private final Map<String, BigDecimal> exchangeRates = new LinkedHashMap<>();
        
        /**
         * Loads currency exchange rates from predefined data
         * In a production app, this would typically fetch from an API
         */
        public void loadRates() {
            String[][] rates = {
                {"Afghanistan (AFN)", "71.00"}, {"Armenia (AMD)", "391.14"}, {"Bahrain (BHD)", "0.38"},
                {"Cyprus (EUR)", "0.92"}, {"Egypt (EGP)", "51.39"}, {"Georgia (GEL)", "2.75"},
                {"Iran (IRR)", "42100.00"}, {"Iraq (IQD)", "1309.96"}, {"Israel (ILS)", "3.78"},
                {"Jordan (JOD)", "0.71"}, {"Kuwait (KWD)", "0.31"}, {"Lebanon (LBP)", "90798.48"},
                {"Oman (OMR)", "0.38"}, {"Palestine (ILS)", "3.78"}, {"Qatar (QAR)", "3.64"},
                {"Saudi Arabia (SAR)", "3.75"}, {"Syria (SYP)", "13001.81"}, {"Turkey (TRY)", "38.01"},
                {"United Arab Emirates (AED)", "3.67"}, {"Yemen (YER)", "245.65"}, {"Algeria (DZD)", "133.41"},
                {"Angola (AOA)", "916.00"}, {"Benin (XOF)", "599.58"}, {"Botswana (BWP)", "14.09"},
                {"Burkina Faso (XOF)", "597.84"}, {"Burundi (BIF)", "2976.63"}, {"Cabo Verde (CVE)", "100.38"},
                {"Cameroon (XAF)", "599.57"}, {"Central African Republic (XAF)", "599.57"}, {"Chad (XAF)", "599.57"},
                {"Comoros (KMF)", "448.02"}, {"Democratic Republic of the Congo (CDF)", "2906.33"},
                {"Republic of the Congo (XAF)", "2871.00"}, {"Djibouti (DJF)", "178.38"},
                {"Equatorial Guinea (XAF)", "8657.72"}, {"Eritrea (ERN)", "15.00"}, {"Eswatini (ZAR)", "19.54"},
                {"Ethiopia (ETB)", "132.60"}, {"Gabon (XAF)", "599.57"}, {"Gambia (GMD)", "71.50"},
                {"Ghana (GHS)", "15.53"}, {"Guinea (GNF)", "8667.85"}, {"Guinea-Bissau (XOF)", "8650.41"},
                {"Ivory Coast (XOF)", "598.34"}, {"Kenya (KES)", "129.71"}, {"Lesotho (LSL)", "19.40"},
                {"Liberia (LRD)", "199.49"}, {"Libya (LYD)", "5.56"}, {"Madagascar (MGA)", "4675.33"},
                {"Malawi (MWK)", "1736.96"}, {"Mali (XOF)", "598.34"}, {"Mauritania (MRU)", "39.70"},
                {"Mauritius (MUR)", "45.12"}, {"Morocco (MAD)", "9.54"}, {"Mozambique (MZN)", "63.90"},
                {"Namibia (NAD)", "18.72"}, {"Niger (XOF)", "1546.27"}, {"Nigeria (NGN)", "1566.0"},
                {"Rwanda (RWF)", "1412.25"}, {"São Tomé and Príncipe (STN)", "22281.80"},
                {"Senegal (XOF)", "598.34"}, {"Seychelles (SCR)", "14.29"}, {"Sierra Leone (SLL)", "22639.50"},
                {"Somalia (SOS)", "572.25"}, {"South Africa (ZAR)", "19.50"}, {"South Sudan (SSP)", "130.26"},
                {"Sudan (SDG)", "600.50"}, {"Tanzania (TZS)", "2691.72"}, {"Togo (XOF)", "598.35"},
                {"Tunisia (TND)", "3.07"}, {"Uganda (UGX)", "3722.90"}, {"Zambia (ZMW)", "28.02"},
                {"Zimbabwe (ZWL)", "322.00"}
            };

            // Store the exchange rates in the map
            for (String[] rate : rates) {
                exchangeRates.put(rate[0], new BigDecimal(rate[1]));
            }
        }
        
        /**
         * Gets the set of all available currencies
         * @return set of currency names
         */
        public Set<String> getCurrencies() {
            return exchangeRates.keySet();
        }
        
        /**
         * Checks if a currency is available in the database
         * @param currency currency name to check
         * @return true if the currency exists
         */
        public boolean isCurrencyAvailable(String currency) {
            return exchangeRates.containsKey(currency);
        }
        
        /**
         * Converts USD to a foreign currency
         * @param usdAmount amount in USD
         * @param toCurrency target currency
         * @return converted amount in target currency (not rounded)
         */
        public BigDecimal convertFromUSD(BigDecimal usdAmount, String toCurrency) {
            BigDecimal rate = exchangeRates.get(toCurrency);
            // Don't round, keep full precision
            return usdAmount.multiply(rate);
        }
        
        /**
         * Converts from a foreign currency to USD
         * @param foreignAmount amount in foreign currency
         * @param fromCurrency source currency
         * @return converted amount in USD (not rounded)
         */
        public BigDecimal convertToUSD(BigDecimal foreignAmount, String fromCurrency) {
            BigDecimal rate = exchangeRates.get(fromCurrency);
            // Don't round, keep full precision
            return foreignAmount.divide(rate, 10, RoundingMode.HALF_UP);
        }
    }
}


/*import java.awt.*;
import java.awt.event.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.prefs.Preferences;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class KyleCurrencyConverter extends JFrame {
    private static final String APP_NAME = "Jordi Currency Converter";
    private static final String PREF_LAST_CURRENCY = "lastCurrency";
    private static final String PREF_LAST_AMOUNT = "lastAmount";
    
    private final JTextField amountField;
    private final JComboBox<String> currencyCombo;
    private final JLabel resultLabel;
    private final JButton convertButton;
    private final JButton switchButton;
    private final JLabel directionLabel;
    
    private final CurrencyModel model;
    private final Preferences prefs;
    private boolean convertToUSD = false;

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        SwingUtilities.invokeLater(() -> {
            KyleCurrencyConverter app = new KyleCurrencyConverter();
            app.setVisible(true);
        });
    }

    public KyleCurrencyConverter() {
        super(APP_NAME);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        prefs = Preferences.userNodeForPackage(KyleCurrencyConverter.class);
        model = new CurrencyModel();
        
        // Setup UI components
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        // Input panel
        JPanel inputPanel = new JPanel(new GridLayout(3, 2, 5, 10));
        
        // Direction label
        directionLabel = new JLabel("USD → Foreign Currency");
        directionLabel.setFont(directionLabel.getFont().deriveFont(Font.BOLD));
        JPanel directionPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        directionPanel.add(directionLabel);
        
        // Amount field
        JLabel amountLabel = new JLabel("Amount:");
        amountField = new JTextField(prefs.get(PREF_LAST_AMOUNT, "1.00"), 10);
        
        // Currency selector
        JLabel currencyLabel = new JLabel("Currency:");
        currencyCombo = new JComboBox<>();
        currencyCombo.setEditable(true);
        
        // Setup searchable dropdown
        setupSearchableComboBox();
        
        // Result display
        resultLabel = new JLabel("Enter an amount and select a currency");
        resultLabel.setFont(resultLabel.getFont().deriveFont(Font.BOLD));
        
        // Action buttons
        convertButton = new JButton("Convert");
        switchButton = new JButton("Switch Direction");
        
        // Add components to input panel
        inputPanel.add(amountLabel);
        inputPanel.add(amountField);
        inputPanel.add(currencyLabel);
        inputPanel.add(currencyCombo);
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        buttonPanel.add(convertButton);
        buttonPanel.add(switchButton);
        
        // Result panel
        JPanel resultPanel = new JPanel(new BorderLayout());
        resultPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("Result"),
                new EmptyBorder(10, 10, 10, 10)));
        resultPanel.add(resultLabel, BorderLayout.CENTER);
        
        // Add panels to main layout
        mainPanel.add(directionPanel, BorderLayout.NORTH);
        mainPanel.add(inputPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        JPanel wrapperPanel = new JPanel(new BorderLayout());
        wrapperPanel.add(mainPanel, BorderLayout.CENTER);
        wrapperPanel.add(resultPanel, BorderLayout.SOUTH);
        
        add(wrapperPanel);
        
        // Load data
        model.loadRates();
        refreshCurrencyList();
        
        // Select last used currency if available
        String lastCurrency = prefs.get(PREF_LAST_CURRENCY, null);
        if (lastCurrency != null) {
            currencyCombo.setSelectedItem(lastCurrency);
        }
        
        // Setup actions
        convertButton.addActionListener(e -> performConversion());
        
        switchButton.addActionListener(e -> {
            convertToUSD = !convertToUSD;
            updateDirectionLabel();
        });
        
        amountField.addActionListener(e -> performConversion());
        
        // Set size and position
        setSize(400, 300);
        setLocationRelativeTo(null);
    }
    
    private void setupSearchableComboBox() {
        JTextField editor = (JTextField) currencyCombo.getEditor().getEditorComponent();
        editor.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                // Skip if up/down arrow keys are pressed
                if (e.getKeyCode() == KeyEvent.VK_UP || e.getKeyCode() == KeyEvent.VK_DOWN) {
                    return;
                }
                
                String input = editor.getText().toLowerCase();
                if (input.isEmpty()) {
                    refreshCurrencyList();
                    return;
                }
                
                DefaultComboBoxModel<String> model = new DefaultComboBoxModel<>();
                for (String currency : KyleCurrencyConverter.this.model.getCurrencies()) {
                    if (currency.toLowerCase().contains(input)) {
                        model.addElement(currency);
                    }
                }
                
                currencyCombo.setModel(model);
                currencyCombo.setSelectedItem(input);
                currencyCombo.showPopup();
            }
        });
    }
    
    private void refreshCurrencyList() {
        DefaultComboBoxModel<String> model = new DefaultComboBoxModel<>();
        for (String currency : this.model.getCurrencies()) {
            model.addElement(currency);
        }
        currencyCombo.setModel(model);
    }
    
    private void updateDirectionLabel() {
        if (convertToUSD) {
            directionLabel.setText("Foreign Currency → USD");
        } else {
            directionLabel.setText("USD → Foreign Currency");
        }
    }
    
    private void performConversion() {
        try {
            // Get and validate input amount
            String amountText = amountField.getText().trim();
            if (amountText.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter an amount.",
                        "Input Error", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            BigDecimal amount;
            try {
                amount = new BigDecimal(amountText);
                if (amount.compareTo(BigDecimal.ZERO) < 0) {
                    JOptionPane.showMessageDialog(this, "Please enter a positive amount.",
                            "Input Error", JOptionPane.WARNING_MESSAGE);
                    return;
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid amount format. Please enter a valid number.",
                        "Input Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Get selected currency
            String selectedCurrency = (String) currencyCombo.getSelectedItem();
            if (selectedCurrency == null || !model.isCurrencyAvailable(selectedCurrency)) {
                JOptionPane.showMessageDialog(this, "Please select a valid currency.",
                        "Input Error", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            // Perform conversion
            BigDecimal result;
            if (convertToUSD) {
                result = model.convertToUSD(amount, selectedCurrency);
                resultLabel.setText(String.format("%s %s = %.2f USD", 
                        amount.toString(), 
                        getCurrencyCode(selectedCurrency), 
                        result.doubleValue()));
            } else {
                result = model.convertFromUSD(amount, selectedCurrency);
                resultLabel.setText(String.format("%.2f USD = %s %s", 
                        amount.doubleValue(), 
                        result.toString(), 
                        getCurrencyCode(selectedCurrency)));
            }
            
            // Save preferences
            prefs.put(PREF_LAST_CURRENCY, selectedCurrency);
            prefs.put(PREF_LAST_AMOUNT, amountText);
            
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error during conversion: " + ex.getMessage(),
                    "Conversion Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }
    
    private String getCurrencyCode(String currencyName) {
        int openParenIndex = currencyName.indexOf('(');
        int closeParenIndex = currencyName.indexOf(')');
        
        if (openParenIndex >= 0 && closeParenIndex > openParenIndex) {
            return currencyName.substring(openParenIndex + 1, closeParenIndex);
        }
        return currencyName;
    }
    
    // Model class to handle currency data and conversion logic
    private static class CurrencyModel {
        private final Map<String, BigDecimal> exchangeRates = new LinkedHashMap<>();
        
        public void loadRates() {
            String[][] rates = {
                {"Afghanistan (AFN)", "71.00"}, {"Armenia (AMD)", "391.14"}, {"Bahrain (BHD)", "0.38"},
                {"Cyprus (EUR)", "0.92"}, {"Egypt (EGP)", "51.39"}, {"Georgia (GEL)", "2.75"},
                {"Iran (IRR)", "42100.00"}, {"Iraq (IQD)", "1309.96"}, {"Israel (ILS)", "3.78"},
                {"Jordan (JOD)", "0.71"}, {"Kuwait (KWD)", "0.31"}, {"Lebanon (LBP)", "90798.48"},
                {"Oman (OMR)", "0.38"}, {"Palestine (ILS)", "3.78"}, {"Qatar (QAR)", "3.64"},
                {"Saudi Arabia (SAR)", "3.75"}, {"Syria (SYP)", "13001.81"}, {"Turkey (TRY)", "38.01"},
                {"United Arab Emirates (AED)", "3.67"}, {"Yemen (YER)", "245.65"}, {"Algeria (DZD)", "133.41"},
                {"Angola (AOA)", "916.00"}, {"Benin (XOF)", "599.58"}, {"Botswana (BWP)", "14.09"},
                {"Burkina Faso (XOF)", "597.84"}, {"Burundi (BIF)", "2976.63"}, {"Cabo Verde (CVE)", "100.38"},
                {"Cameroon (XAF)", "599.57"}, {"Central African Republic (XAF)", "599.57"}, {"Chad (XAF)", "599.57"},
                {"Comoros (KMF)", "448.02"}, {"Democratic Republic of the Congo (CDF)", "2906.33"},
                {"Republic of the Congo (XAF)", "2871.00"}, {"Djibouti (DJF)", "178.38"},
                {"Equatorial Guinea (XAF)", "8657.72"}, {"Eritrea (ERN)", "15.00"}, {"Eswatini (ZAR)", "19.54"},
                {"Ethiopia (ETB)", "132.60"}, {"Gabon (XAF)", "599.57"}, {"Gambia (GMD)", "71.50"},
                {"Ghana (GHS)", "15.53"}, {"Guinea (GNF)", "8667.85"}, {"Guinea-Bissau (XOF)", "8650.41"},
                {"Ivory Coast (XOF)", "598.34"}, {"Kenya (KES)", "129.71"}, {"Lesotho (LSL)", "19.40"},
                {"Liberia (LRD)", "199.49"}, {"Libya (LYD)", "5.56"}, {"Madagascar (MGA)", "4675.33"},
                {"Malawi (MWK)", "1736.96"}, {"Mali (XOF)", "598.34"}, {"Mauritania (MRU)", "39.70"},
                {"Mauritius (MUR)", "45.12"}, {"Morocco (MAD)", "9.54"}, {"Mozambique (MZN)", "63.90"},
                {"Namibia (NAD)", "18.72"}, {"Niger (XOF)", "1546.27"}, {"Nigeria (NGN)", "1566.0"},
                {"Rwanda (RWF)", "1412.25"}, {"São Tomé and Príncipe (STN)", "22281.80"},
                {"Senegal (XOF)", "598.34"}, {"Seychelles (SCR)", "14.29"}, {"Sierra Leone (SLL)", "22639.50"},
                {"Somalia (SOS)", "572.25"}, {"South Africa (ZAR)", "19.50"}, {"South Sudan (SSP)", "130.26"},
                {"Sudan (SDG)", "600.50"}, {"Tanzania (TZS)", "2691.72"}, {"Togo (XOF)", "598.35"},
                {"Tunisia (TND)", "3.07"}, {"Uganda (UGX)", "3722.90"}, {"Zambia (ZMW)", "28.02"},
                {"Zimbabwe (ZWL)", "322.00"}
            };

            for (String[] rate : rates) {
                exchangeRates.put(rate[0], new BigDecimal(rate[1]));
            }
        }
        
        public Set<String> getCurrencies() {
            return exchangeRates.keySet();
        }
        
        public boolean isCurrencyAvailable(String currency) {
            return exchangeRates.containsKey(currency);
        }
        
        public BigDecimal convertFromUSD(BigDecimal usdAmount, String toCurrency) {
            BigDecimal rate = exchangeRates.get(toCurrency);
            return usdAmount.multiply(rate).setScale(2, RoundingMode.HALF_UP);
        }
        
        public BigDecimal convertToUSD(BigDecimal foreignAmount, String fromCurrency) {
            BigDecimal rate = exchangeRates.get(fromCurrency);
            return foreignAmount.divide(rate, 2, RoundingMode.HALF_UP);
        }
    }
}
*/

/* import java.awt.event.*;
import java.util.*;
import javax.swing.*;

public class KyleCurrencyConverter {
    public static void main(String[] args) {
        // Define exchange rates (static for now, can be updated dynamically)
        Map<String, Double> exchangeRates = new LinkedHashMap<>();
        addExchangeRates(exchangeRates);

        while (true) { // Loop to allow multiple conversions
            // Get USD amount from user
            String usdInput = JOptionPane.showInputDialog("Enter amount in USD:");
            if (usdInput == null) return; // Exit if canceled
            double usdAmount;
            try {
                usdAmount = Double.parseDouble(usdInput);
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(null, "Invalid input. Please enter a numeric value.");
                continue; // Restart the loop
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

            if (option != JOptionPane.OK_OPTION) return; // Exit if canceled

            String selectedCurrency = (String) comboBox.getSelectedItem();
            if (selectedCurrency != null && exchangeRates.containsKey(selectedCurrency)) {
                double convertedAmount = usdAmount * exchangeRates.get(selectedCurrency);
                JOptionPane.showMessageDialog(null,
                        String.format("%.2f USD = %.2f %s", usdAmount, convertedAmount, selectedCurrency));
            } else {
                JOptionPane.showMessageDialog(null, "Invalid selection.");
            }

            // Ask if the user wants to perform another conversion
            int goAgain = JOptionPane.showConfirmDialog(null, "Do you want to perform another conversion?",
                    "Continue?", JOptionPane.YES_NO_OPTION);
            if (goAgain != JOptionPane.YES_OPTION) {
                break; // Exit the loop if the user chooses "No"
            }
        }
    }

    // Method to add exchange rates and remove duplicates
    private static void addExchangeRates(Map<String, Double> exchangeRates) {
        String[][] rates = {
            {"Afghanistan (AFN)", "71.00"}, {"Armenia (AMD)", "391.14"}, {"Bahrain (BHD)", "0.38"},
            {"Cyprus (EUR)", "0.92"}, {"Egypt (EGP)", "51.39"}, {"Georgia (GEL)", "2.75"},
            {"Iran (IRR)", "42100.00"}, {"Iraq (IQD)", "1309.96"}, {"Israel (ILS)", "3.78"},
            {"Jordan (JOD)", "0.71"}, {"Kuwait (KWD)", "0.31"}, {"Lebanon (LBP)", "90798.48"},
            {"Oman (OMR)", "0.38"}, {"Palestine (ILS)", "3.78"}, {"Qatar (QAR)", "3.64"},
            {"Saudi Arabia (SAR)", "3.75"}, {"Syria (SYP)", "13001.81"}, {"Turkey (TRY)", "38.01"},
            {"United Arab Emirates (AED)", "3.67"}, {"Yemen (YER)", "245.65"}, {"Algeria (DZD)", "133.41"},
            {"Angola (AOA)", "916.00"}, {"Benin (XOF)", "599.58"}, {"Botswana (BWP)", "14.09"},
            {"Burkina Faso (XOF)", "597.84"}, {"Burundi (BIF)", "2976.63"}, {"Cabo Verde (CVE)", "100.38"},
            {"Cameroon (XAF)", "599.57"}, {"Central African Republic (XAF)", "599.57"}, {"Chad (XAF)", "599.57"},
            {"Comoros (KMF)", "448.02"}, {"Democratic Republic of the Congo (CDF)", "2906.33"},
            {"Republic of the Congo (XAF)", "2871.00"}, {"Djibouti (DJF)", "178.38"},
            {"Equatorial Guinea (XAF)", "8657.72"}, {"Eritrea (ERN)", "15.00"}, {"Eswatini (ZAR)", "19.54"},
            {"Ethiopia (ETB)", "132.60"}, {"Gabon (XAF)", "599.57"}, {"Gambia (GMD)", "71.50"},
            {"Ghana (GHS)", "15.53"}, {"Guinea (GNF)", "8667.85"}, {"Guinea-Bissau (XOF)", "8650.41"},
            {"Ivory Coast (XOF)", "598.34"}, {"Kenya (KES)", "129.71"}, {"Lesotho (LSL)", "19.40"},
            {"Liberia (LRD)", "199.49"}, {"Libya (LYD)", "5.56"}, {"Madagascar (MGA)", "4675.33"},
            {"Malawi (MWK)", "1736.96"}, {"Mali (XOF)", "598.34"}, {"Mauritania (MRU)", "39.70"},
            {"Mauritius (MUR)", "45.12"}, {"Morocco (MAD)", "9.54"}, {"Mozambique (MZN)", "63.90"},
            {"Namibia (NAD)", "18.72"}, {"Niger (XOF)", "1546.27"}, {"Nigeria (NGN)", "1566.0"},
            {"Rwanda (RWF)", "1412.25"}, {"São Tomé and Príncipe (STN)", "22281.80"},
            {"Senegal (XOF)", "598.34"}, {"Seychelles (SCR)", "14.29"}, {"Sierra Leone (SLL)", "22639.50"},
            {"Somalia (SOS)", "572.25"}, {"South Africa (ZAR)", "19.50"}, {"South Sudan (SSP)", "130.26"},
            {"Sudan (SDG)", "600.50"}, {"Tanzania (TZS)", "2691.72"}, {"Togo (XOF)", "598.35"},
            {"Tunisia (TND)", "3.07"}, {"Uganda (UGX)", "3722.90"}, {"Zambia (ZMW)", "28.02"},
            {"Zimbabwe (ZWL)", "322.00"}
        };

        for (String[] rate : rates) {
            exchangeRates.put(rate[0], Double.parseDouble(rate[1]));
        }

        // Use a Set to check for duplicates
        Set<String> uniqueCurrencies = new HashSet<>();
        for (String[] currency : rates) {
            if (!uniqueCurrencies.contains(currency[0])) {
                uniqueCurrencies.add(currency[0]);
                exchangeRates.put(currency[0], Double.parseDouble(currency[1]));
            }
        }April2025
    }
 */
