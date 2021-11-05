package cn.authing.guard.data;

public class Country {
    private String shortName;
    private String name;
    private String en;
    private String code;

    public Country(String shortName, String name, String en, String code) {
        this.shortName = shortName;
        this.name = name;
        this.en = en;
        this.code = code;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEn() {
        return en;
    }

    public void setEn(String en) {
        this.en = en;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
