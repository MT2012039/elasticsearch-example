package prompts;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;



public class Prompts {

    public static Map<String, String> promptMap;

    static {
        promptMap = new LinkedHashMap<>();
        promptMap.put("Opportunity", "Retrieve deals with their names and amounts, sorted by probability of closure from Salesforce");
        promptMap.put("Account", "Retrieve all Accounts with their names and industry types from Salesforce");
        promptMap.put("Contact", "List Contacts along with their affiliated Accounts and Opportunities from Salesforce");
        promptMap.put("Case", "List Cases along with their associated Accounts and Contacts from Salesforce");
        promptMap.put("Campaign", "Show Campaigns with their performance metrics, ROI, and comparative benchmarks from Salesforce");
    }

}
