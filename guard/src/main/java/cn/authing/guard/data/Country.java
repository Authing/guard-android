package cn.authing.guard.data;

import androidx.annotation.NonNull;

import java.io.Serializable;
import java.util.Comparator;

import cn.authing.guard.util.Util;

public class Country implements Serializable {
    private String abbreviation;
    private String cn;
    private String cnPy;
    private String firstSpell;
    private String en;
    private String code;
    private String emoji;

    public Country(String abbreviation, String cn, String cnPy, String en, String code, String emoji) {
        this.abbreviation = abbreviation;
        this.cn = cn;
        this.cnPy = cnPy;
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

    public String getNamePy() {
        return cnPy;
    }

    public void setNamePy(String namePy) {
        this.cnPy = namePy;
    }

    public String getFirstSpell() {
        return firstSpell;
    }

    public void setFirstSpell(String firstSpell) {
        this.firstSpell = firstSpell;
    }

    @NonNull
    public String toString() {
        return cn;
    }

    public static class ComparatorPY implements Comparator<Country> {
        @Override
        public int compare(Country lhs, Country rhs) {
            String str1 = Util.isCn() ? lhs.getNamePy() : lhs.getEnName();
            String str2 = Util.isCn() ? rhs.getNamePy() : rhs.getEnName();
            return str1.compareToIgnoreCase(str2);
        }
    }
}
