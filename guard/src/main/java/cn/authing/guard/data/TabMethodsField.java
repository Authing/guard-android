package cn.authing.guard.data;

import org.json.JSONObject;

public class TabMethodsField {

    private String key;
    private String label;
    private String labelEn;
    private JSONObject i18n;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getLabelEn() {
        return labelEn;
    }

    public void setLabelEn(String labelEn) {
        this.labelEn = labelEn;
    }

    public JSONObject getI18n() {
        return i18n;
    }

    public void setI18n(JSONObject i18n) {
        this.i18n = i18n;
    }
}
