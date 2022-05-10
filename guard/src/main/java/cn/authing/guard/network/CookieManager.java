package cn.authing.guard.network;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CookieManager {

    static class Cookie {
        String name;
        String value;

        Cookie(String n, String v) {
            name = n;
            value = v;
        }
    }

    private static final Map<String, Cookie> cookies = new ConcurrentHashMap<>();

    public static void addCookies(okhttp3.Response response) {
        if (!response.headers("Set-Cookie").isEmpty()) {
            for (String header : response.headers("Set-Cookie")) {
                String[] data = header.split(";");
                String one = data[0];
                String[] parts = one.split("=");
                if (parts.length > 1) {
                    Cookie cookie = new Cookie(parts[0].trim(), parts[1].trim());
                    CookieManager.addCookie(cookie);
                }
            }
        }
    }

    public static void addCookie(Cookie cookie) {
        cookies.put(cookie.name, cookie);
    }

    public static String getCookie() {
        String s = "";
        for(String key : cookies.keySet()) {
            Cookie cookie = cookies.get(key);
            s += cookie.name + "=" + cookie.value + "; ";
        }
        return s;
    }

    public static void removeAllCookies() {
        cookies.clear();
    }
}
