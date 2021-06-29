package com.atguigu3.chapter03_gui;

import io.appium.java_client.AppiumDriver;
import org.openqa.selenium.remote.DesiredCapabilities;


import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.TimeUnit;

/**
 * @description:
 * @author: malichun
 * @time: 2021/6/17/0017 16:23
 */
public class Test {
    public static void main(String[] args) throws MalformedURLException, InterruptedException {
        DesiredCapabilities capabilities = new DesiredCapabilities();

        capabilities.setCapability("platformName", "Android");
//        capabilities.setCapability("deviceName","8DF6R17503000072");
//        capabilities.setCapability("platformVersion", "8.0.0");

        capabilities.setCapability("unicodeKeyboard","True");
        capabilities.setCapability("resetKeyboard","True");

        capabilities.setCapability("autoGrantPermissions", "True");

        //   capabilities.setCapability("app", "C:\\Users\\del\\Desktop\\b\\jsq.apk");

        capabilities.setCapability("appPackage", "com.zhongan.insurance");
        capabilities.setCapability("appActivity", " com.zhongan.insurance.ui.activity.AppStartActivity");




        AppiumDriver driver = new AppiumDriver(new URL("https://www.baidu.com/"), capabilities);


        driver.manage().timeouts().implicitlyWait(500, TimeUnit.SECONDS);


        Thread.sleep(5000);

//        System.out.println(driver.currentActivity());

        Thread.sleep(5000);

        System.out.println(driver.getCapabilities());

        Thread.sleep(5000);


        driver.findElementById("com.youba.calculate:id/btn_one").click();

        Thread.sleep(5000);

        driver.findElementById("com.youba.calculate:id/btn_plus").click();

        Thread.sleep(5000);

        driver.findElementById("com.youba.calculate:id/btn_two").click();

        Thread.sleep(5000);

        driver.findElementById("com.youba.calculate:id/btn_equal").click();

        Thread.sleep(5000);

        System.out.println(driver.findElementById("com.youba.calculate:id/tv_display").getText());






        Thread.sleep(5000);

        driver.quit();

    }
}
