package cn.authing.guard.util;

import java.util.HashMap;
import java.util.Map;

public class GlobalCountDown {

    private static int maxCountDown = 60;
    private static final Map<String, Integer> countDownMap = new HashMap<>();

    public static void start(String phone) {
        if (Util.isNull(phone)) {
            return;
        }

        Integer cd = countDownMap.get(phone);
        if (cd != null) {
            // already started
            return;
        }

        countDownMap.put(phone, maxCountDown);
        (new Thread(new CountDownTask(phone))).start();
    }

    public static int getFirstCountDown() {
        if (countDownMap.size() > 0) {
            Map.Entry<String, Integer> entry = countDownMap.entrySet().iterator().next();
            return entry.getValue();
        }
        return 0;
    }

    public static void setMaxCountDown(int cd) {
        maxCountDown = cd;
    }

    public static boolean isCountingDown() {
        if (countDownMap.size() > 0) {
            Map.Entry<String, Integer> entry = countDownMap.entrySet().iterator().next();
            Integer value = entry.getValue();
            return value != null && value > 0;
        }
        return false;
    }

    public static boolean isCountingDown(String phone) {
        Integer cd = countDownMap.get(phone);
        return cd != null && cd > 0;
    }

    private static class CountDownTask implements Runnable {

        private final String phone;

        CountDownTask(String phone) {
            this.phone = phone;
        }

        public void run() {
            while (true) {
                try {
                    Integer cd = countDownMap.get(phone);
                    if (cd == null) {
                        return;
                    }

                    if (cd == 0) {
                        countDownMap.remove(phone);
                        break;
                    }

                    Thread.sleep(1000);
                    cd--;
                    countDownMap.put(phone, cd);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
