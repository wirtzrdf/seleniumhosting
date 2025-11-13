package com.example.selenium_rag;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SeleniumService {
    @Autowired DiccionarioRepository diccionarioRepository;
    
    public ArrayList<String> findWordsRag(ArrayList<String> searchedWords) throws Exception {
        WebDriver driver = null;
        ArrayList<String> respostas = new ArrayList<>();
        try {
            // WebDriverManager.chromedriver().clearDriverCache().setup();
            // WebDriverManager.chromedriver().forceDownload().setup();
            // WebDriverManager.chromedriver().driverVersion("140.0.7339.185").setup();
            // WebDriverManager.chromedriver()
            // .browserVersion(getChromeVersion()) // detecta la versión instalada
            // .setup();
            // System.setProperty("webdriver.chrome.driver",
            // "chrome-win64\\chromedriver.exe");
            // WebDriverManager.chromedriver()
            // .avoidBrowserDetection() // evita problemas de detección de versión
            // .setup();
            //
            ChromeOptions options = new ChromeOptions();
            options.addArguments("--headless");
            options.addArguments("--disable-gpu");
            options.addArguments("--window-size=1920,1080");
            options.addArguments("--no-sandbox");
            options.addArguments("--disable-dev-shm-usage");
            driver = new ChromeDriver(options);
            for (String searchedWord : searchedWords) {
                driver.get("https://academia.gal/dicionario");
                WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
                if (searchedWord == null || searchedWord.isEmpty()) {
                    respostas.add("null: INCORRECTO1");
                    continue;
                }
                // relocalizar el input y el botón en cada iteración
                WebElement findInput = wait.until(ExpectedConditions.elementToBeClickable(
                        By.id("_com_ideit_ragportal_liferay_dictionary_NormalSearchPortlet_fieldSearchNoun")));
                findInput.clear();
                findInput.sendKeys(searchedWord);

                By buscarBtnBy = By.cssSelector("button[aria-label='Buscar']");
                WebElement buscarBtn = wait.until(ExpectedConditions.elementToBeClickable(buscarBtnBy));
                // Scroll the button to the center to avoid sticky header overlay
                ((JavascriptExecutor) driver).executeScript(
                        "arguments[0].scrollIntoView({block:'center', inline:'center'});", buscarBtn);
                wait.until(ExpectedConditions.elementToBeClickable(buscarBtn)).click();
                
                wait.until(ExpectedConditions.or(
                        ExpectedConditions.visibilityOfElementLocated(By.cssSelector("span.Lemma__LemmaSign")),
                        ExpectedConditions.visibilityOfElementLocated(By.cssSelector("p.no-available"))));
                
                 List<WebElement> span =
                 driver.findElements(By.cssSelector("span.Lemma__LemmaSign"));
                if (!span.isEmpty()) {
                    // Re-locate the element just before accessing its attribute
                    List<WebElement> freshSpans = driver.findElements(By.cssSelector("span.Lemma__LemmaSign"));
                    if (!freshSpans.isEmpty()) {
                        WebElement freshSpan = freshSpans.get(0);
                        String clases = freshSpan.getDomAttribute("class");
                        List<String> listaClases = Arrays.asList(clases.split("\\s+"));
                        if (listaClases.contains("condenado")) {
                            respostas.add(searchedWord + " :INCORRECTO 2");
                        } else {
                            List<WebElement> freshDefs = driver
                                    .findElements(By.cssSelector("span.Definition__Definition"));
                            if (!freshDefs.isEmpty()) {
                                WebElement freshDef = freshDefs.get(0);
                                respostas.add(searchedWord + " :" + freshDef.getText());
                            } else {
                                respostas.add(searchedWord + " :SIN DEFINICIÓN");
                            }
                        }
                    } else {
                        respostas.add(searchedWord + " :INCORRECTO 3");
                    }
                } else {
                    respostas.add(searchedWord + " :INCORRECTO 4");
                }
                // if (driver != null)
                // driver.quit();
                diccionarioRepository.save(
                    new Diccionario (null, searchedWord, respostas.get(respostas.size()-1)));            
            }

            return respostas;
        } catch (Exception e) {
            e.printStackTrace();
            throw e; // lanza la excepción al llamante
            // return null;
        } finally {
            if (driver != null)
                driver.quit();
        }
    }
}
