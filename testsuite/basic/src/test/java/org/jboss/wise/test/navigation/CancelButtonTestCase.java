package org.jboss.wise.test.navigation;

import org.jboss.arquillian.drone.api.annotation.Drone;
import org.jboss.arquillian.graphene.Graphene;
import org.jboss.arquillian.graphene.page.Page;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.wise.test.utils.PropUtils;
import org.jboss.wise.test.utils.StartPage;
import org.jboss.wise.test.utils.WiseTest;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Check that cancel button sends user to the start page.
 */
@RunWith(Arquillian.class) public class CancelButtonTestCase extends WiseTest {

    @Drone private WebDriver browser;

    @Page private StartPage homePage;

    @ArquillianResource private URL baseURL;

    @Before public void before() {
        setBrowser(browser);
        userAuthentication(baseURL.toString());

        Graphene.goTo(StartPage.class);
        Graphene.waitModel().withTimeout(30, TimeUnit.SECONDS);

        loadStepOneOfThree();
        loadStepTwoOfThree();
        loadStepThreeOfThree();
    }

    /**
     * Confirm the endpoint conf page (Step 3 of 3 ) loaded
     */
    @Test public void cancelButtonTest() {
        confirmPageLoaded(PropUtils.get("page.invoke"));
        executeCancel();
        confirmPageLoaded(PropUtils.get("page.list"));
    }

    private void executeCancel() {

        //- check menu buttons
        try {

            List<WebElement> buttonList = new ArrayList<WebElement>();
            buttonList = browser.findElements(By.className(PropUtils.get("tag.wise-gwt-Button")));

            String cancelLabel = PropUtils.get("label.wise-gwt-Button.Cancel");
            WebElement cancelButton = null;
            for (WebElement we : buttonList) {
                if (we.getText().equals(cancelLabel)) {
                    cancelButton = we;
                }
            }

            Assert.assertNotNull("Failed to find Cancel button on page, " + browser.getCurrentUrl(), cancelButton);
            cancelButton.click();

        } catch (Exception e1) {
            Assert.fail("Failed evaluate tag.wise-gwt-Button: " + e1.getMessage());
        }

    }
}
