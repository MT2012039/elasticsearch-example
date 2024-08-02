package servicenow;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Description {

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @JsonProperty
    String description;

}
