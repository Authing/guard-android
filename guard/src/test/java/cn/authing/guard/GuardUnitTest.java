package cn.authing.guard;

import org.junit.Test;
import static org.junit.Assert.*;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;

import cn.authing.guard.network.AuthClient;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class GuardUnitTest {
    @Test
    public void updatePhone() throws ExecutionException, InterruptedException {
        CompletableFuture<Integer> future = new CompletableFuture<>();
        Executors.newCachedThreadPool().submit(() -> {
            AuthClient.loginByAccount("test", "111111", (c, m, d)->{
                AuthClient.updatePhone(null, "13418585237", "1136",
                        null, "13632530515", "5731",
                        (code, message, data)->{
                    future.complete(code);
                });
            });

        });
        assertEquals((Integer) 200, future.get());
    }
}
