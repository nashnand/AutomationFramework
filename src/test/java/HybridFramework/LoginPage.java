package HybridFramework;

import com.relevantcodes.extentreports.ExtentReports;
import com.relevantcodes.extentreports.ExtentTest;
import com.relevantcodes.extentreports.LogStatus;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.support.PageFactory;
import org.testng.Assert;
import org.testng.SkipException;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import javax.swing.*;
import java.io.*;
import java.util.Iterator;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

public class LoginPage extends LoginObjectPage {
    WebDriver driver;
    String[][] testdata;
    static ExtentTest test;
    static ExtentReports report;

    @DataProvider(name = "loginData")
    public Object[] TestDataExcel() throws IOException {

        int rowCount, columnCount;
        String excelFilePath = "C:\\GITPROJ\\AutomationFramework\\src\\test\\java\\TestData\\LoginTestdata.xlsx";
        FileInputStream inputStream = new FileInputStream(new File(excelFilePath));
        Workbook workbook = new XSSFWorkbook(inputStream);
        Sheet Sheet = workbook.getSheetAt(0);
        Iterator<Row> iterator = Sheet.iterator();
        Row nextRow = iterator.next();
        rowCount = Sheet.getPhysicalNumberOfRows();
        columnCount = nextRow.getLastCellNum();
        testdata = new String[rowCount - 1][columnCount - 1];
        /*Below values can be used on Loop if necessary*/
        /*int rowStart = Sheet.getFirstRowNum();*/
        /*int rowEnd = Sheet.getLastRowNum();*/
        for (int i = 1; i < rowCount; i++) {  /*we are starting from first row so i=1,since first row is header*/
            Row row = Sheet.getRow(i);
            for (int j = 1; j < columnCount; j++) {
                Cell cell = row.getCell(j);
                String value = String.valueOf(cell);
                testdata[i - 1][j - 1] = value;/*cell.getStringCellValue();*/
            }
        }

        return testdata;
    }

    @BeforeSuite
    public void driverinitialis() throws IOException {
        /*Setting the driver*/
        String configloc = "C:\\GITPROJ\\AutomationFramework\\src\\test\\java\\HybridFramework\\Config.Properties";
        FileInputStream Locator = new FileInputStream(configloc);
        Properties properties;
        properties = new Properties();
        properties.load(Locator);

        String BrowserName = properties.getProperty("browser");
        String DriverLocation = properties.getProperty("driverlocation");
        String URL = properties.getProperty("URL");
        if (BrowserName.equalsIgnoreCase("chrome")) {
            System.setProperty("webdriver.chrome.driver", DriverLocation);
            driver = new ChromeDriver();
        } else if (BrowserName.equalsIgnoreCase("firefox"))
            driver = new InternetExplorerDriver();
        driver.get(URL);
        driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(5000, TimeUnit.MILLISECONDS);
        PageFactory.initElements(driver, LoginObjectPage.class);
        /*Extend Report*/
        report = new ExtentReports(System.getProperty("user.dir") + "\\ExtentReportResults.html");
        test = report.startTest("OrangeHRM");
    }

    @Test(priority = 0, dataProvider = "loginData")
    public void LoginFunctionality(String TestID, String Description, String ExecutionStatus, String uname, String upassword) throws InterruptedException, IOException {
        if (ExecutionStatus.equals("Yes")) {
            username.sendKeys(uname);
            password.sendKeys(upassword);
            LoginButton.click();

            switch (TestID) {
                case "TC001":
                case "TC002":
                case "TC003":
                    Assert.assertEquals(Failuremessage.getText(), "Invalid credentials");
                    test.log(LogStatus.PASS, "Test Passed:"+Description);
                    Thread.sleep(5000);
                    break;
                case "TC004":
                    if (driver.getCurrentUrl().equals("https://opensource-demo.orangehrmlive.com/index.php/dashboard"))
                        test.log(LogStatus.PASS, "Test Passed:"+Description);
                    else {
                        test.log(LogStatus.FAIL, "Test Failed:"+Description);
                    }
                    /*Thread.sleep(5000);*/
                    /*  default: statement;*/
            }

        } else {
                       test.log(LogStatus.SKIP, "Test skipped : "+Description);
                       throw new SkipException("Skipping / Ignoring");
        }
    }

/*    @Test(priority = 1, dataProvider = "loginData")
    public void loginInvalidPassword(String TestID,String Descript,String ExecutionStatus,String uname,String upassword) throws InterruptedException {
        if(ExecutionStatus.equals("Yes") && TestID.equals("TC002")) {
            username.sendKeys(uname);
            password.sendKeys(upassword);
            LoginButton.click();
        }
    }
    @Test(priority = 2, dataProvider = "loginData")
    public void loginInvalidUsername(String TestID,String Descript,String ExecutionStatus,String uname,String upassword) throws InterruptedException {
        if(ExecutionStatus.equals("Yes") && TestID.equals("TC003")) {
            username.sendKeys(uname);
            password.sendKeys(upassword);
            LoginButton.click();
        }
    }
    @Test(priority = 3, dataProvider = "loginData")
    public void loginInvalidUserNamePassword(String TestID,String Descript,String ExecutionStatus,String uname,String upassword) throws InterruptedException {
        if(ExecutionStatus.equals("Yes") && TestID.equals("TC004")) {
            username.sendKeys(uname);
            password.sendKeys(upassword);
            LoginButton.click();
        }
    }*/


    @AfterSuite
    public void driverclose() {
        report.endTest(test);
        report.flush();
        driver.quit();
    }

}
