package cn.authing.guard.data;

public class Agreement {
    private String title;
    private boolean isRequired;
    private String lang;
    private int availableAt;

    private boolean showAtLogin;
    private boolean showAtRegister;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isRequired() {
        return isRequired;
    }

    public void setRequired(boolean required) {
        isRequired = required;
    }

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

    public int getAvailableAt() {
        return availableAt;
    }

    public void setAvailableAt(int availableAt) {
        this.availableAt = availableAt;
        if (availableAt == 0) {
            showAtRegister = true;
        } else if (availableAt == 1) {
            showAtLogin = true;
        } else if (availableAt == 2) {
            showAtLogin = true;
            showAtRegister = true;
        }
    }

    public boolean isShowAtLogin() {
        return showAtLogin;
    }

    public boolean isShowAtRegister() {
        return showAtRegister;
    }
}
