package cn.authing.guard.network;

public interface Receiver {

    void onOpen();

    void onReceiverMessage(String msg);

    void onError(String errorMsg);

}
