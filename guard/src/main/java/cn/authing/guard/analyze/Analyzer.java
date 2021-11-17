package cn.authing.guard.analyze;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Analyzer {
    static ExecutorService singleThreadExecutor;

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
}
