package cn.authing.guard.data;

import java.io.Serializable;
import java.util.List;

public class ExtendedField implements Serializable {
    private String type;
    private String inputType;
    private String name;
    private String label;
    private boolean isRequired;
    private List<ValidateRule> validateRule;

    // entered by user
    private String value;

    public static class ValidateRule implements Serializable {
        private String type;
        private String content;
        private String error;

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public String getError() {
            return error;
        }

        public void setError(String error) {
            this.error = error;
        }
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getInputType() {
        return inputType;
    }

    public void setInputType(String inputType) {
        this.inputType = inputType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public boolean isRequired() {
        return isRequired;
    }

    public void setRequired(boolean required) {
        isRequired = required;
    }

    public List<ValidateRule> getValidateRule() {
        return validateRule;
    }

    public void setValidateRule(List<ValidateRule> validateRule) {
        this.validateRule = validateRule;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public ExtendedField clone() {
        ExtendedField field = new ExtendedField();

        field.type = this.type;
        field.inputType = this.inputType;
        field.name = this.name;
        field.label = this.label;
        field.isRequired = this.isRequired;
        field.value = this.value;
        return field;
    }
}
