package no.steria.kata.javaee;

import static org.fest.assertions.Assertions.assertThat;

import org.eclipse.jetty.plus.jndi.EnvEntry;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.webapp.WebAppContext;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;

public class PersonWebTest {

    @Test
    public void shouldCreateAndDisplayPerson() throws Exception {
        DataSources.initDatabaseSchema(DataSources.inMemoryDataSource("webTest"));
        new EnvEntry("jdbc/personDs", DataSources.inMemoryDataSource("webTest"));

        Server server = new Server(0);
        server.setHandler(new WebAppContext("src/main/webapp", "/"));
        server.start();

        String url = "http://localhost:" + server.getConnectors()[0].getLocalPort() + "/";

        WebDriver browser = createBrowser();
        browser.get(url);

        browser.findElement(By.linkText("Create person")).click();
        browser.findElement(By.name("full_name")).sendKeys("Darth Vader");
        browser.findElement(By.name("createPerson")).click();

        browser.findElement(By.linkText("Find people")).click();
        browser.findElement(By.name("name_query")).sendKeys("th vad");
        browser.findElement(By.name("findPeople")).click();

        assertThat(browser.getPageSource()).contains("<li>Darth Vader</li>");

    }

    protected HtmlUnitDriver createBrowser() {
        return new HtmlUnitDriver() {
            @Override
            public WebElement findElement(By by) {
                try {
                    return super.findElement(by);
                } catch (NoSuchElementException e) {
                    throw new NoSuchElementException("Can't find " + by + " in " + getPageSource());
                }
            }
        };
    }
}
