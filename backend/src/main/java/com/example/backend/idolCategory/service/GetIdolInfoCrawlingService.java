package com.example.backend.idolCategory.service;

import com.example.backend.idolCategory.dto.IdolCategoryInfoDTO;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@Transactional
public class GetIdolInfoCrawlingService {
    public ResponseEntity<List<IdolCategoryInfoDTO>> getIdolInfo(String artist) {
        ChromeOptions options = new ChromeOptions();
        //options.addArguments("--start-maximized");
        options.addArguments("--headless"); // 크롬창 띄우지 않고 실행

        System.setProperty("webdriver.chrome.driver", "/usr/local/bin/chromedriver");

        WebDriver driver = new ChromeDriver(options);

        List<IdolCategoryInfoDTO> idolCategoryInfoResponse = new ArrayList<>();

        try {
            driver.get("https://music.bugs.co.kr/search/artist?q=" + artist);
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(3));

            List<WebElement> searchResults = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector("li.like_group")));

            for (WebElement result : searchResults) {
                String listItemText = result.getText();
                WebElement imgElement = result.findElement(By.tagName("img"));

                WebElement subInfoElement = result.findElement(By.cssSelector("p.subInfo"));

                // span.artistGenre 요소의 텍스트 가져오기
                WebElement artistGenreElement = findElementOrNull(subInfoElement, By.cssSelector("span.artistGenre"));
                // span.artistType 요소의 텍스트 가져오기
                WebElement artistTypeElement = findElementOrNull(subInfoElement, By.cssSelector("span.artistType"));

                String artistImg = imgElement.getAttribute("src");


                if (artistTypeElement != null && artistGenreElement != null) {
                    String artistName = extractArtistName(listItemText);
                    String artistGenre = artistGenreElement.getText();
                    String artistType = artistTypeElement.getText();
                    idolCategoryInfoResponse.add(new IdolCategoryInfoDTO(artistName, artistImg, artistGenre, artistType));
                }
                /* else {
                    log.error(artistTypeElement+"!!!");
                    System.out.println("Artist Type not found for the current like_group element.");
                }*/
            }
        }catch (Exception e){
            log.error("exception: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } finally {
            driver.quit();
        }

        return ResponseEntity.ok().body(idolCategoryInfoResponse);
    }

    private WebElement findElementOrNull(WebElement parent, By by) {
        java.util.List<WebElement> elements = parent.findElements(by);
        return elements.isEmpty() ? null : elements.get(0);
    }

    private String extractArtistName(String liString) {
        String[] parts = liString.split("\\n");
        System.out.println("Text inside <li>: " + parts[0]);

        return parts[0];
    }
}
