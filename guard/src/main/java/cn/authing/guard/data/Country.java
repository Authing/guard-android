package cn.authing.guard.data;

import java.io.Serializable;

public class Country implements Serializable {
    private String abbrev;
    private String name;
    private String code;
    private String emoji;

    public Country(String abbrev, String name, String code, String emoji) {
        this.abbrev = abbrev;
        this.name = name;
        this.code = code;
        this.emoji = emoji;
    }

    public String getAbbrev() {
        return abbrev;
    }

    public void setAbbrev(String abbrev) {
        this.abbrev = abbrev;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getEmoji() {
        return emoji;
    }

    public void setEmoji(String emoji) {
        this.emoji = emoji;
    }
}
