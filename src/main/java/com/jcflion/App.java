package com.jcflion;

/**
 * @author kanner
 */
public class App {

    public static void main(String[] args) throws InterruptedException {
        new InitConfig("app1", "http://127.0.0.1:8080").init();
        Thread.sleep(5000L);
        System.out.println(ConfigManager.getConfig("app1-test-file.cflion-key1"));
    }
}
