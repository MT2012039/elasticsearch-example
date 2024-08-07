package query.salesforce.validator;

import com.force.api.ApiException;
import com.force.api.ApiSession;
import com.force.api.ForceApi;
import salesforce.AccessToken;

public class SyntaxValidator {

    ForceApi forceApi;
    public SyntaxValidator() {
        ApiSession session = new ApiSession(AccessToken.AT, "https://informatica-7b-dev-ed.develop.my.salesforce.com");
        forceApi = new ForceApi(session);
    }
    public boolean isSyntaxValid(String query) {
        try {
            forceApi.query(query);
            return true;
        } catch (ApiException apiException) {
            return false;
        }
    }
}
