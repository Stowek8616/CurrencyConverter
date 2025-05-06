// Import statements
import java.awt.*;
import java.awt.event.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.*;
import java.util.prefs.Preferences;
import javax.swing.*;
import javax.swing.Timer;
import javax.swing.border.EmptyBorder;
import javax.swing.text.*;

/**
 * CurrencyConverterTest - A currency conversion application with enhanced UI
 * that allows users to convert between USD and various foreign currencies with customizable fees.
 */
public class CurrencyConverterTest extends JFrame {
    // Application constants
    private static final String APP_NAME = "Currency Converter";
    private static final String PREF_LAST_CURRENCY = "lastCurrency";
    private static final String PREF_LAST_AMOUNT = "lastAmount";
    private static final String PREF_FEE_PERCENTAGE = "feePercentage";
    private static final String API_KEY = "13bcb8956948bd59e9bdb08757b5424a"; // Your API key
    private static final String API_URL = "https://api.exchangeratesapi.io/v1/latest?access_key=" + API_KEY + "&base=USD";
    
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
    private static Map<String, Double> exchangeRates = new HashMap<>();
    private final Preferences prefs;
    private boolean convertToUSD = false;
    private Timer rateUpdateTimer;

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
        
        // Initialize exchange rates
        initializeExchangeRates();
        
        // Launch the application on the Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            CurrencyConverterTest app = new CurrencyConverterTest();
            app.setVisible(true);
        });
    }
    
    /**
     * Initialize exchange rates with predefined values
     */
    private static void initializeExchangeRates() {
        // Middle Eastern countries
        exchangeRates.put("Afghanistan (AFN)", 71.00);
        exchangeRates.put("Armenia (AMD)", 391.14);
        exchangeRates.put("Bahrain (BHD)", 0.38);
        exchangeRates.put("Cyprus (EUR)", 0.92);
        exchangeRates.put("Egypt (EGP)", 51.39);
        exchangeRates.put("Georgia (GEL)", 2.75);
        exchangeRates.put("Iran (IRR)", 42100.00);
        exchangeRates.put("Iraq (IQD)", 1309.96);
        exchangeRates.put("Israel (ILS)", 3.78);
        exchangeRates.put("Jordan (JOD)", 0.71);
        exchangeRates.put("Kuwait (KWD)", 0.31);
        exchangeRates.put("Lebanon (LBP)", 90798.48);
        exchangeRates.put("Oman (OMR)", 0.38);
        exchangeRates.put("Palestine (ILS)", 3.78);
        exchangeRates.put("Qatar (QAR)", 3.64);
        exchangeRates.put("Saudi Arabia (SAR)", 3.75);
        exchangeRates.put("Syria (SYP)", 13001.81);
        exchangeRates.put("Turkey (TRY)", 38.01);
        exchangeRates.put("United Arab Emirates (AED)", 3.67);
        exchangeRates.put("Yemen (YER)", 245.65);
        // African countries
        exchangeRates.put("Algeria (DZD)", 133.41);
        exchangeRates.put("Angola (AOA)", 916.00);
        exchangeRates.put("Benin (XOF)", 599.58);
        exchangeRates.put("Botswana (BWP)", 14.09);
        exchangeRates.put("Burkina Faso (XOF)", 597.84);
        exchangeRates.put("Burundi (BIF)", 2976.63);
        exchangeRates.put("Cabo Verde (CVE)", 100.38);
        exchangeRates.put("Cameroon (XAF)", 599.57);
        exchangeRates.put("Central African Republic (XAF)", 599.57);
        exchangeRates.put("Chad (XAF)", 599.57);
        exchangeRates.put("Comoros (KMF)", 448.02);
        exchangeRates.put("Democratic Republic of the Congo (CDF)", 2906.33);
        exchangeRates.put("Republic of the Congo (XAF)", 2871.00);
        exchangeRates.put("Djibouti (DJF)", 178.38);
        exchangeRates.put("Equatorial Guinea (XAF)", 8657.72);
        exchangeRates.put("Eritrea (ERN)", 15.00);
        exchangeRates.put("Eswatini (ZAR)", 19.54);
        exchangeRates.put("Ethiopia (ETB)", 132.60);
        exchangeRates.put("Gabon (XAF)", 599.57);
        exchangeRates.put("Gambia (GMD)", 71.50);
        exchangeRates.put("Ghana (GHS)", 15.53);
        exchangeRates.put("Guinea (GNF)", 8667.85);
        exchangeRates.put("Guinea-Bissau (XOF)", 8650.41);
        exchangeRates.put("Ivory Coast (XOF)", 598.34);
        exchangeRates.put("Kenya (KES)", 129.71);
        exchangeRates.put("Lesotho (LSL)", 19.40);
        exchangeRates.put("Liberia (LRD)", 199.49);
        exchangeRates.put("Libya (LYD)", 5.56);
        exchangeRates.put("Madagascar (MGA)", 4675.33);
        exchangeRates.put("Malawi (MWK)", 1736.96);
        exchangeRates.put("Mali (XOF)", 598.34);
        exchangeRates.put("Mauritania (MRU)", 39.70);
        exchangeRates.put("Mauritius (MUR)", 45.12);
        exchangeRates.put("Morocco (MAD)", 9.54);
        exchangeRates.put("Mozambique (MZN)", 63.90);
        exchangeRates.put("Namibia (NAD)", 18.72);
        exchangeRates.put("Niger (XOF)", 1546.27);
        exchangeRates.put("Nigeria (NGN)", 1566.0);
        exchangeRates.put("Rwanda (RWF)", 1412.25);
        exchangeRates.put("São Tomé and Príncipe (STN)", 22281.80);
        exchangeRates.put("Senegal (XOF)", 598.34);
        exchangeRates.put("Seychelles (SCR)", 14.29);
        exchangeRates.put("Sierra Leone (SLL)", 22639.50);
        exchangeRates.put("Somalia (SOS)", 572.25);
        exchangeRates.put("South Africa (ZAR)", 19.50);
        exchangeRates.put("South Sudan (SSP)", 130.26);
        exchangeRates.put("Sudan (SDG)", 600.50);
        exchangeRates.put("Tanzania (TZS)", 2691.72);
        exchangeRates.put("Togo (XOF)", 598.35);
        exchangeRates.put("Tunisia (TND)", 3.07);
        exchangeRates.put("Uganda (UGX)", 3722.90);
        exchangeRates.put("Zambia (ZMW)", 28.02);
        exchangeRates.put("Zimbabwe (ZWL)", 322.00);
    }

    /**
     * Constructor: Creates the main application window and initializes all components
     */
    public CurrencyConverterTest() {
        super(APP_NAME);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        // Initialize user preferences
        prefs = Preferences.userNodeForPackage(CurrencyConverterTest.class);
        
        // Create main panel with borders
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));

        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
            mainPanel.setBackground(new Color(255, 255, 255));

        // Create panel for input fields in a grid layout
        JPanel inputPanel = new JPanel(new GridLayout(5, 2, 5, 10));
        inputPanel.setBackground(new Color(230, 230, 230));

        
        // Direction indicator
        directionLabel = new JLabel("USD → Foreign Currency");
        directionLabel.setFont(directionLabel.getFont().deriveFont(Font.BOLD));
        JPanel directionPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        directionPanel.add(directionLabel);
        
        // Amount input field
        JLabel amountLabel = new JLabel("Amount:");
        amountLabel.setForeground(new Color(0, 102, 204)); // Blue color
        amountField = new JTextField(prefs.get(PREF_LAST_AMOUNT, "1.00"), 10);
        
        // Configure amount field to accept only numeric input
        configureDecimalTextField(amountField);
        
        // Currency selection dropdown
        JLabel currencyLabel = new JLabel("Currency:");
        currencyLabel.setForeground(new Color(0, 100, 0)); // Set currency label to dark green
        currencyCombo = new JComboBox<>();
        currencyCombo.setEditable(true);
        
        // Setup fee components
        JLabel feeLabel = new JLabel("Fee (%):");
        feeLabel.setForeground(Color.RED); // Set fee label color to red
        feeField = new JTextField(prefs.get(PREF_FEE_PERCENTAGE, "2.5"), 5);
        configureDecimalTextField(feeField);
        
        applyFeeCheckbox = new JCheckBox("Apply fee to conversion");
        
        // Configure searchable dropdown with autocomplete
        setupSearchableComboBox();
        
        // Result display
        resultLabel = new JLabel("Enter an amount and select a currency");
        resultLabel.setFont(resultLabel.getFont().deriveFont(Font.BOLD));
        
        // Action buttons
        // Set button colors
        //https://teaching.csse.uwa.edu.au/units/CITS1001/colorinfo.html
        convertButton = new JButton("Convert");
        convertButton.setBackground(new Color(0, 102, 204)); // Blue color
        convertButton.setForeground(Color.BLACK);

        switchButton = new JButton("Switch Direction");
        switchButton.setBackground(new Color(0, 100, 0)); // Green color
        switchButton.setForeground(Color.BLACK);
        
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
                BorderFactory.createLineBorder(new Color(0, 102, 204), 2))); // Blue border
                //new EmptyBorder(10, 10, 10, 10)));
        resultPanel.add(resultLabel, BorderLayout.CENTER);
        resultPanel.setBackground(new Color(255, 255, 255));
        
    
        // Add panels to main layout
        mainPanel.add(directionPanel, BorderLayout.NORTH);
        mainPanel.add(inputPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        // Create a wrapper panel for main content and result
        JPanel wrapperPanel = new JPanel(new BorderLayout());
        wrapperPanel.add(mainPanel, BorderLayout.CENTER);
        wrapperPanel.add(resultPanel, BorderLayout.SOUTH);

        add(wrapperPanel);
        
        // Fill the currency dropdown
        refreshCurrencyList();
        
        // Restore previous settings from preferences
        String lastCurrency = prefs.get(PREF_LAST_CURRENCY, null);
        if (lastCurrency != null && exchangeRates.containsKey(lastCurrency)) {
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
        
        // Set up timer to update exchange rates
        rateUpdateTimer = new Timer(1000 * 60 * 60, e -> fetchExchangeRates()); // Refresh rates every hour
        rateUpdateTimer.start();
        
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
            public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) 
                    throws BadLocationException {
                if (isValidDecimalInput(fb.getDocument().getText(0, fb.getDocument().getLength()) + string)) {
                    super.insertString(fb, offset, string, attr);
                }
            }
            
            @Override
            public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) 
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
                for (String currency : exchangeRates.keySet()) {
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
        // Sort currencies in alphabetical order
        java.util.List<String> sortedCurrencies = new ArrayList<>(exchangeRates.keySet());
        Collections.sort(sortedCurrencies);
        
        DefaultComboBoxModel<String> model = new DefaultComboBoxModel<>();
        for (String currency : sortedCurrencies) {
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
            if (selectedCurrency == null || !exchangeRates.containsKey(selectedCurrency)) {
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
            Double rate = exchangeRates.get(selectedCurrency);
            
            if (convertToUSD) {
                // Convert from foreign currency to USD
                result = amount.divide(BigDecimal.valueOf(rate), 10, RoundingMode.HALF_UP);
                
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
                result = amount.multiply(BigDecimal.valueOf(rate));
                
                // Calculate and apply fee if needed
                if (applyFeeCheckbox.isSelected() && feePercentage.compareTo(BigDecimal.ZERO) > 0) {
                    feeAmount = result.multiply(feePercentage.divide(new BigDecimal("100")));
                    result = result.subtract(feeAmount);
                }
                
                // Format the result display
                DecimalFormat df = new DecimalFormat("#,##0.00");
                resultLabel.setText(String.format("<html>%s USD = %s %s<br>%s</html>", 
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
     * Fetch exchange rates from API (implementation currently commented out)
     */
    private static void fetchExchangeRates() {
        try {
            // This is a placeholder for API integration
            // The actual implementation would fetch rates from the API
            // but is currently using static rates defined in initializeExchangeRates()
            
            System.out.println("Exchange rates updated.");
        } catch (Exception e) {
            System.err.println("Error fetching exchange rates: " + e.getMessage());
        }
    }
}