package com.coconason.dtf.integration.test;

import com.coconason.dtf.demo.SpringDemoApplication;
import com.coconason.dtf.demo2.SpringDemo2Application;
import com.coconason.dtf.demo3.SpringDemo3Application;
import com.coconason.dtf.manager.ManagerServerApplication;
import org.junit.BeforeClass;
import org.junit.Test;

public class BaseIT {
    
    @BeforeClass
    public static void initialization() throws Exception{
//        Thread t = new Thread(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    ManagerServerApplication.main(new String[1]);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        });
//        t.start();
//        Thread.sleep(10000);
        SpringDemoApplication.main(new String[1]);
        Thread.sleep(10000);
        SpringDemo2Application.main(new String[1]);
        Thread.sleep(10000);
        SpringDemo3Application.main(new String[1]);
    }
    
    @Test
    public void finalSynchronizeTest() {
    }
    
}
