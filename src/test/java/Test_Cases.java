import io.appium.java_client.AppiumDriver;
import io.appium.java_client.MobileBy;
import io.appium.java_client.MobileElement;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.ios.IOSDriver;
import io.appium.java_client.remote.AutomationName;
import io.appium.java_client.remote.MobileCapabilityType;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import javax.annotation.Nullable;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * This test class contain Hybrid app test cases for Android and IOS
 * Pratik Patel.
 */
public class Test_Cases {

    private static String ANDROID_APP = getAbsolutePath("src/test/resources/hybrid_app.apk");
    private static String IOS_APP = getAbsolutePath("src/test/resources/hybrid_app.app.zip");

    private static By webViewOption = new MobileBy.ByAccessibilityId("Webview Demo");
    private static By urlInput = MobileBy.AccessibilityId("urlInput");
    private static By goBtn = MobileBy.AccessibilityId("navigateBtn");
    private static By clearBtn = MobileBy.AccessibilityId("clearBtn");


    @BeforeTest
    public void beforeMethod() {

    }


    @Test
    public void androidTest() throws MalformedURLException {
        DesiredCapabilities caps = DesiredCapabilities.android();
        caps.setCapability(MobileCapabilityType.PLATFORM_NAME, "Android");
        caps.setCapability(MobileCapabilityType.PLATFORM_VERSION, "8.0");
        caps.setCapability(MobileCapabilityType.DEVICE_NAME, "c4e3f3cd");
        caps.setCapability(MobileCapabilityType.AUTOMATION_NAME, AutomationName.ANDROID_UIAUTOMATOR2);
        caps.setCapability(MobileCapabilityType.APP, ANDROID_APP);

        AndroidDriver driver = new AndroidDriver(new URL("http://localhost:4723/wd/hub"), caps);
        testCode(driver);
    }

    @Test
    public void iOSTest() throws MalformedURLException {
        DesiredCapabilities caps = DesiredCapabilities.iphone();
        caps.setCapability(MobileCapabilityType.PLATFORM_NAME, "iOS");
        caps.setCapability(MobileCapabilityType.PLATFORM_VERSION, "11.4");
        caps.setCapability(MobileCapabilityType.DEVICE_NAME, "iPhone X");
        caps.setCapability(MobileCapabilityType.APP, IOS_APP);

        IOSDriver driver = new IOSDriver(new URL("http://localhost:4723/wd/hub"), caps);
        testCode(driver);
    }

    public void testCode(AppiumDriver driver) {
        final String expectedTitle = "Appium Pro: The Awesome Appium Tips Newsletter";
        WebDriverWait wait = new WebDriverWait(driver, 10);

        wait.until(ExpectedConditions.visibilityOf(driver.findElement(webViewOption))).click();
//        System.out.println(driver.getContextHandles());
//        wait.until(ExpectedConditions.visibilityOf(driver.findElement(urlInput))).sendKeys("https://appiumpro.com");
//        driver.findElement(goBtn).click();
//
//        String webviewContext = getWebviewContext(driver);
//        System.out.println(webviewContext);
//        driver.context(webviewContext);
//        System.out.println(webviewContext);


        MobileElement input = (MobileElement) wait
                .until(ExpectedConditions.presenceOfElementLocated(urlInput));

        // Get into the webview and assert that we're not yet at the correct page
        String webContext = getWebviewContext(driver);
        driver.context(webContext);
        Assert.assertNotEquals(driver.getTitle(), expectedTitle);

        // Go back into the native context and automate the URL button
        driver.context("NATIVE_APP");
        input.sendKeys("https://google.com");
        WebElement navigate = driver.findElement(goBtn);
        navigate.click();

        // Assert that going to Google is not allowed
        try {
            Thread.sleep(1000); // cheap way to ensure alert has time to show
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        driver.switchTo().alert().accept();

        // Now try to go to Appium Pro
        driver.findElement(clearBtn).click();
        input.sendKeys("https://appiumpro.com");
        navigate.click();

        // Go back into the webview and assert that the title is correct
        driver.context(webContext);
        wait.until(ExpectedConditions.titleIs(expectedTitle));
        Assert.assertEquals(expectedTitle, driver.getTitle(), "Title didn't matched!");

    }

    @Nullable
    public String getWebviewContext(AppiumDriver driver) {
        List<String> contextHandles = new ArrayList(driver.getContextHandles());
        for (String context : contextHandles) {
            if (!context.equals("NATIVE_APP"))
                return context;
        }
        return null;
    }

    @AfterMethod
    public void afterMethod() {

    }


    private static String getAbsolutePath(String appRelativePath) {
        File file = new File(appRelativePath);
        return file.getAbsolutePath();
    }

}
