package com.jcflion;

import com.jcflion.gray.GrayConfigManager;

/**
 * @author kanner
 */
public class App {

    public static void main(String[] args) throws InterruptedException {
        new InitConfig("app1", "http://127.0.0.1:8080").init();
        while (true) {
            Thread.sleep(5000L);
            System.out.println("cnf1.key1=" + ConfigManager.getConfig("cnf1.key1"));
            System.out.println("cnf1.key2=" + ConfigManager.getConfig("cnf1.key2"));
            System.out.println("account1 is allowed=" + GrayConfigManager.isAllowed("cnf1.switch", "account1"));
            System.out.println("account2 is allowed=" + GrayConfigManager.isAllowed("cnf1.switch", "account2"));
            System.out.println("account3 is allowed=" + GrayConfigManager.isAllowed("cnf1.switch", "account3"));
            System.out.println("cnf2.key3=" + ConfigManager.getConfig("cnf2.key3"));
            System.out.println("------------------------");
        }
    }
}
