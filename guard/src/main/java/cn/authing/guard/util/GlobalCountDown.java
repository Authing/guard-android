package cn.authing.guard.util;

public class GlobalCountDown {

    public static int countDown;

    private static Thread thread;
    private static Object lock = new Object();

    public static void start() {
        if (countDown != 0) {
            return;
        }

        countDown = 60;
        if (thread == null) {
            thread = new Thread(new CountDownTask());
            thread.start();
        } else {
            synchronized (lock) {
                lock.notify();
            }
        }
    }

    private static class CountDownTask implements Runnable {
        public void run() {
            while (true) {
                try {
                    if (countDown == 0) {
                        synchronized (lock) {
                            lock.wait();
                        }
                    }

                    Thread.sleep(1000);
                    countDown--;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
