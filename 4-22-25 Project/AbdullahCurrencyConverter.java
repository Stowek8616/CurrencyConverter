//import statements
import java.awt.event.*; //importing utility classes
import java.util.*; //importing swing library for creating GUI components
import javax.swing.*; //importing event handling classes for user input 
//these are not up to date 
//last updated 04/07/25

public class AbdullahCurrencyConverter {
    public static void main(String[] args) {
        // Define exchange rates
        Map<String, Double> exchangeRates = new HashMap<>();
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

        // Sort currencies in alphabetical order(used poe ai)
        List<String> sortedCurrencies = new ArrayList<>(exchangeRates.keySet()); //creates a list from the keys
        Collections.sort(sortedCurrencies); //Sort the list alphabetically

        // Create a JFrame(used poe ai)
        JFrame frame = new JFrame("Currency Converter"); 
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); //Exit the app when window closes
        frame.setSize(400, 300); //The window size 
        frame.setLayout(null);

        // Search bar(used poe ai)
        JTextField searchBar = new JTextField(); //Create a searchbar to search for countries 
        searchBar.setBounds(20, 20, 340, 30); //set the size
        frame.add(searchBar); //add the searchbar to the frame

        //Label for the fee input
        JLabel feeLabel = new JLabel("Fee (%):");
        feeLabel.setBounds(20, 70, 100, 30);

        // Dropdown for currency selection(used poe ai)
        JComboBox<String> currencyDropdown = new JComboBox<>(sortedCurrencies.toArray(new String[0])); //dropdown for the countries 
        currencyDropdown.setBounds(20, 70, 340, 30); //the size of the part 
        frame.add(currencyDropdown); //add dropdown to the frame

        // Convert button(used poe ai)
        JButton convertButton = new JButton("Convert"); //create a button called Convert
        convertButton.setBounds(20, 120, 340, 30); //set the position and size
        frame.add(convertButton); //add the button to the frame

        // Output label(used poe ai)
        JLabel outputLabel = new JLabel("");
        outputLabel.setBounds(20, 170, 340, 30); //set the position and size 
        frame.add(outputLabel); //add the label to the frame

        // Action listener for the convert button (used poe ai)
        convertButton.addActionListener(new ActionListener() { //makes sure when the button is clicked, it works
            public void actionPerformed(ActionEvent e) {
                try {
                    String usdInput = searchBar.getText(); //gets user input from the search bar
                    double usdAmount = Double.parseDouble(usdInput); //converts user input to a double
                    if (usdAmount <= 0) { //does not let user enter any negative number
                    JOptionPane.showMessageDialog(frame, "Please enter a positive amount."); // Show error if not
                    return; // Exit the method
                }
                    String selectedCurrency = (String) currencyDropdown.getSelectedItem(); //gets the selected currency from the dropdown
                    double convertedAmount = usdAmount * exchangeRates.get(selectedCurrency); //calculates the converted amount using the exchange rates
                    outputLabel.setText(String.format("%.2f USD = %.2f %s", usdAmount, convertedAmount, selectedCurrency)); //updates the label with the formatted result
                } catch (NumberFormatException ex) { //catches errors if theres no valid number 
                    JOptionPane.showMessageDialog(frame, "Please enter a valid number."); //comes after an error is inputted 
                }
            }
        });
        // Show the frame
        frame.setVisible(true);
    }
}
