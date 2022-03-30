package cn.authing.guard.data;

import androidx.annotation.NonNull;

import java.io.Serializable;

public class Country implements Serializable {
    private String abbreviation;
    private String cn;
    private String en;
    private String code;
    private String emoji;

    public Country(String abbreviation, String cn, String en, String code, String emoji) {
        this.abbreviation = abbreviation;
        this.cn = cn;
        this.en = en;
        this.code = code;
        this.emoji = emoji;
    }

    public String getAbbrev() {
        return abbreviation;
    }

    public void setAbbrev(String abbrev) {
        this.abbreviation = abbrev;
    }

    public String getName() {
        return cn;
    }

    public void setName(String name) {
        this.cn = name;
    }

    public String getEnName() {
        return en;
    }

    public void setEnName(String enName) {
        this.en = enName;
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

    @NonNull
    public String toString() {
        return cn;
    }
}
