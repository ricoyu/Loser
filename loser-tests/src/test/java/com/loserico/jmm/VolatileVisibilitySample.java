package com.loserico.jmm;

import org.apache.poi.util.SystemOutLogger;

import java.util.concurrent.Callable;

/**
 * <p>
 * Copyright: (C), 2019 2019-09-15 17:24
 * <p>
 * Company: Sexy Uncle Inc.
 *
 * @version 1.0
 * @author: Rico Yu ricoyu520@gmail.com
 */
public class VolatileVisibilitySample {
    private boolean initFlag = false;

    public void refresh() {
        this.initFlag = true;
        String threadName = Thread.currentThread().getName();
        System.out.println("线程: " + threadName + "修改共享变量initFlag");
    }

    public void load() {
        String threadName = Thread.currentThread().getName();
        while (!initFlag) {
        }

        System.out.println("线程: " + threadName + "当前线程嗅探到initFlag状态的改变");
    }

    public static void main(String[] args) throws InterruptedException {
        VolatileVisibilitySample sample = new VolatileVisibilitySample();
        Thread threadA = new Thread(() -> {
            sample.refresh();
        }, "threadA");
        Thread threadB = new Thread(() -> {
            sample.load();
        }, "threadA");
        for (int i = 0; i < 123; i++) {
            Callable c = () -> "a";
        }
        threadB.start();
        Thread.sleep(1000);
        threadA.start();
    }
}
