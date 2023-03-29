package cn.authing.otp;

public class TOTPBindResult {

    public static final int BIND_SUCCESS = 200;
    public static final int UPDATED_ACCOUNT = 201;
    public static final int BIND_FAILURE = 500;

    private int code = BIND_FAILURE;
    private String message;
    private TOTPEntity historyTotp;
    private TOTPEntity newTotp;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public TOTPEntity getHistoryTotp() {
        return historyTotp;
    }

    public void setHistoryTotp(TOTPEntity historyTotp) {
        this.historyTotp = historyTotp;
    }

    public TOTPEntity getNewTotp() {
        return newTotp;
    }

    public void setNewTotp(TOTPEntity newTotp) {
        this.newTotp = newTotp;
    }
}
