package org.jboss.wise.test.navigation;

import java.net.URL;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.arquillian.drone.api.annotation.Drone;
import org.jboss.arquillian.graphene.Graphene;
import org.jboss.arquillian.graphene.page.Page;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.wise.test.utils.StartPage;
import org.jboss.wise.test.utils.PropUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.lang.Exception;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.jboss.wise.test.utils.WiseTest;


/**
 * Check that back button navigates to the previous page.
 */
@RunWith(Arquillian.class)
public class BackButtonNavigationTestCase extends WiseTest {

    @Drone
    private WebDriver browser;

    @Page
    private StartPage homePage;

    @ArquillianResource
    private URL baseURL;


    @Before
    public void before() {
        setBrowser(browser);
        userAuthentication(baseURL.toString());

        Graphene.goTo(StartPage.class);
        Graphene.waitModel().withTimeout(30, TimeUnit.SECONDS);

        loadStepOneOfThree();
        loadStepTwoOfThree();
        loadStepThreeOfThree();
    }

    /**
     * Travel back to the start page
     */
    @Test
    public void backButtonTest() {

        confirmPageLoaded(PropUtils.get("page.invoke"));

        executeBack();
        Graphene.waitModel().withTimeout(30, TimeUnit.SECONDS).until()
            .element(By.className(PropUtils.get("tag.wise-gwt-Button-back"))).is().present();
        confirmPageLoaded(PropUtils.get("page.config"));

        executeBack();
        // provide an extra sec so page display won't fail.
        try {
          Thread.currentThread().sleep(1000);
        } catch (Exception e) {
          // do nothing
        }

        Graphene.waitModel().withTimeout(30, TimeUnit.SECONDS).until()
            .element(By.className(PropUtils.get("tag.tree.item"))).is().present();
        confirmPageLoaded(PropUtils.get("page.endpoints"));

        executeBack();
        confirmPageLoaded(PropUtils.get("page.list"));
    }

    private void executeBack() {

        try {

            List<WebElement> buttonList = new ArrayList<WebElement>();
            buttonList = browser.findElements(By.className(
                PropUtils.get("tag.wise-gwt-Button")));

            String backLabel = PropUtils.get("label.wise-gwt-Button.Back");
            WebElement backButton = null;
            for (WebElement we : buttonList) {
                if (we.getText().equals(backLabel)) {
                    backButton = we;
                }
            }

            Assert.assertNotNull("Failed to find Back button on page, "
                + browser.getCurrentUrl(), backButton);
            backButton.click();

        } catch (Exception e1) {
            Assert.fail("Failed evaluate tag.wise-gwt-Button: " + e1.getMessage());
        }

    }
}
