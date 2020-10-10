package HybridFramework;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.support.PageFactory;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.*;
import java.util.Iterator;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

public class LoginPage extends LoginObjectPage {
    WebDriver driver;
    String[][] testdata ;

    @DataProvider(name = "loginData")
    public Object[] Exceldata() throws IOException {
        testdata=TestDataExcel();
        return testdata ;
    }

    public String[][] TestDataExcel() throws IOException {

        int rowCount, columnCount;
        String excelFilePath = "C:\\GITPROJ\\AutomationFramework\\src\\test\\java\\TestData\\LoginTestdata.xlsx";
        FileInputStream inputStream = new FileInputStream(new File(excelFilePath));
        Workbook workbook = new XSSFWorkbook(inputStream);
        Sheet Sheet = workbook.getSheetAt(0);
        Iterator<Row> iterator = Sheet.iterator();
        Row nextRow = iterator.next();
        rowCount = Sheet.getPhysicalNumberOfRows();
        columnCount = nextRow.getLastCellNum();
        testdata = new String[rowCount-1][columnCount];
        /*Below values can be used on Loop if necessary*/
        /*int rowStart = Sheet.getFirstRowNum();*/
        /*int rowEnd = Sheet.getLastRowNum();*/
        for (int i = 1; i < rowCount; i++) {
            Row row = Sheet.getRow(i);
            for (int j = 0; j <columnCount; j++) {
                Cell cell = row.getCell(j);
                String value=String.valueOf(cell);
                testdata[i-1][j] = value;/*cell.getStringCellValue();*/
            }
        }

        return testdata;
    }

    @BeforeSuite
    public void driverinitialis() throws IOException {
        /*Setting the driver*/
        String configloc="C:\\GITPROJ\\AutomationFramework\\src\\test\\java\\HybridFramework\\Config.Properties";
        FileInputStream Locator = new FileInputStream(configloc);
        Properties properties;
        properties = new Properties();
        properties.load(Locator);

        String BrowserName = properties.getProperty("browser");
        String DriverLocation=properties.getProperty("driverlocation");
        String URL=properties.getProperty("URL");
        if (BrowserName.equalsIgnoreCase("chrome")) {
            System.setProperty("webdriver.chrome.driver",DriverLocation);
            driver = new ChromeDriver();
        }
        else if (BrowserName.equalsIgnoreCase("firefox"))
            driver = new InternetExplorerDriver();
        driver.get(URL);
        driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(5000, TimeUnit.MILLISECONDS);
        PageFactory.initElements(driver, LoginObjectPage.class);


    }

    @Test(priority = 0, dataProvider = "loginData")
    public void loginwithcorrect(String uname,String upassword) throws InterruptedException {
        username.sendKeys(uname);
        password.sendKeys(upassword);
        LoginButton.click();
    }

    @Test(priority = 0)
    public void loginwithincorrect() throws InterruptedException {
        username.sendKeys("admin");
        password.sendKeys("admin");
        LoginButton.click();

    }

    @AfterSuite
    public void driverclose() {
        driver.quit();
    }

}
/*
class PropertiesLoader {
    public static Properties loadProperties(String resourceFileName) throws IOException {
        Properties configuration = new Properties();
        InputStream inputStream = PropertiesLoader.class
                .getClassLoader()
                .getResourceAsStream(resourceFileName);
        configuration.load(inputStream);
        inputStream.close();
        return configuration;
    }
}*/
