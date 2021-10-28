package cn.authing.guard;

public interface Callback<T> {
    void call(boolean ok, T data);
}
