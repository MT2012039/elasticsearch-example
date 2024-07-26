package salesforce;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Attribute {

    @JsonProperty
    String name;

    public Attribute(){}

    public Attribute(String name, String type) {
        this.name = name;
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @JsonProperty
    String type;
}
