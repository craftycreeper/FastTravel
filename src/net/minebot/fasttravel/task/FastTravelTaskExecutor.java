package net.minebot.fasttravel.task;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by oneill011990 on 17.08.2014.
 */
public class FastTravelTaskExecutor {

    private static ExecutorService exec;

    public static void init(){
        exec = Executors.newCachedThreadPool();
    }

    public static ExecutorService getExecutor(){
        return exec;
    }
}
