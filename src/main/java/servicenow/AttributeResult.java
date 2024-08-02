package servicenow;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AttributeResult {

    @JsonProperty
    String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getInternalType() {
        return internalType;
    }

    public void setInternalType(String internalType) {
        this.internalType = internalType;
    }

    @JsonProperty
    String internalType;
}
