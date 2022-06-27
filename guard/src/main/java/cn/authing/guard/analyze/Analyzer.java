package cn.authing.guard.analyze;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Analyzer {
    static ExecutorService singleThreadExecutor;
    static final JSONObject componentMap = new JSONObject();

    private static Timer sTimer;

    private static ExecutorService getExecutor() {
        if (singleThreadExecutor == null) {
            singleThreadExecutor = Executors.newSingleThreadExecutor();
        }
        return singleThreadExecutor;
    }

    public static void reportSDKUsage() {
        SDKUsageTask task = new SDKUsageTask();
        getExecutor().submit(task);
    }

    public static void report(String name) {
//        try {
//            synchronized (componentMap) {
//                if (componentMap.has(name)) {
//                    int count = componentMap.getInt(name);
//                    componentMap.put(name, ++count);
//                } else {
//                    componentMap.put(name, 1);
//                }
//            }
//
//            if (sTimer != null) {
//                sTimer.cancel();
//            }
//            sTimer = new Timer();
//            sTimer.schedule(new ReportTimerTask(), 3000);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }

    static void clearComponents() {
        synchronized (componentMap) {
            Iterator<String> keys = componentMap.keys();
            List<String> ks = new ArrayList<>();
            while(keys.hasNext()) {
                ks.add(keys.next());
            }

            for (String k : ks) {
                componentMap.remove(k);
            }
        }
    }

    private static class ReportTimerTask extends TimerTask {
        @Override
        public void run() {
            try {
                ComponentTask task = new ComponentTask(copyMap());
                getExecutor().submit(task);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    private static JSONObject copyMap() {
        JSONObject ret = new JSONObject();
        synchronized (componentMap) {
            for (Iterator<String> it = Analyzer.componentMap.keys(); it.hasNext(); ) {
                String key = it.next();
                try {
                    ret.put(key, Analyzer.componentMap.get(key));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return ret;
    }
}
