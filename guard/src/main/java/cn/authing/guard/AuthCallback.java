package cn.authing.guard;

public interface AuthCallback<T> {
    void call(int code, String message, T userInfo);
}