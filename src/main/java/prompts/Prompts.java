package prompts;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;



public class Prompts {

    public static Map<String, String> promptMap;
    public static  Map<String, String> app_to_app_prompts;

    static {
        promptMap = new LinkedHashMap<>();
        promptMap.put("Competitor", "Retrieve Competitors with their SWOT analyses and competitive landscapes from Salesforce");
        promptMap.put("Case", "Show Cases with their resolution statuses and resolution times from Salesforce");
        promptMap.put("Opportunity", "Fetch Opportunities along with their associated Competitors and their strengths/weaknesses from Salesforce");
        promptMap.put("Lead", "List Leads along with their associated Campaigns and responses from Salesforce");
        promptMap.put("Product", "Retrieve Products with their associated Opportunities and revenue contributions from Salesforce");

        app_to_app_prompts = new LinkedHashMap<>();
        app_to_app_prompts.put("incident","\"Create a process that syncs ServiceNow incidents with Salesforce. Loop through all closed incidents in ServiceNow and update the corresponding cases in Salesforce.");
        app_to_app_prompts.put("user","Create a process to sync contacts between Salesforce and ServiceNow. When a contact is created or updated in Salesforce, create it as a user in ServiceNow. Search for duplicate user accounts in ServiceNow, if a corresponding contact does not exist, user account should be created.");
        app_to_app_prompts.put("case","Create a process to sync new and updated cases from Salesforce to ServiceNow.  If a new case is created in Salesforce, then search for a matching incident in ServiceNow by Salesforce description. Find the matching user in ServiceNow by email address. If no matching user is found in ServiceNow, creates a new user in ServiceNow. If no matching incident is found in ServiceNow, create a new incident in ServiceNow.");
        app_to_app_prompts.put("assets", "Create a process to sync all assets from Salesforce to Servicenow");
        app_to_app_prompts.put("campaign", "Create a live notification to servicenow users when there is a new campaign added in Salesforce");

    }



}
