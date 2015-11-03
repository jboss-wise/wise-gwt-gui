package org.jboss.wise.test.navigation;

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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.jboss.wise.test.utils.WiseTest;


/**
 * Check that the page loads and for the presence of the UI components on the page
 */
@RunWith(Arquillian.class)
public class InvocationViewBasicComponentsTestCase extends WiseTest {

    @Drone
    private WebDriver browser;

    @Page
    private StartPage homePage;


    @Before
    public void before() {
        setBrowser(browser);

        Graphene.goTo(StartPage.class);
        Graphene.waitModel().withTimeout(30, TimeUnit.SECONDS);

        loadStepOneOfThree();
        loadStepTwoOfThree();
        loadStepThreeOfThree();
    }

    /**
     * Confirm the endpoint conf page (Step 3 of 3 ) loaded
     */
    @Test
    public void stepThreeOfThreeTest() {
        confirmPageLoaded(PropUtils.get("page.invoke"));
        comfirmUIComponents();
    }

    private void comfirmUIComponents() {
        // check page title
        try {
            WebElement pageTitle = browser.findElement(
                By.className(PropUtils.get("tag.wiseStepLabel")));
            Assert.assertNotNull("Page title was expected but was not found.", pageTitle);
        } catch (Exception e1) {
            Assert.fail("Failed to evaluate tag.wiseStepLabel: " + e1.getMessage());
        }

        // check for disclosure Panels
        try {
            List<WebElement> disclosurePanelList = browser.findElements(By.className(
                PropUtils.get("tag.gwt-DisclosurePanel")));
            Assert.assertTrue("1 disclosure Panels expected but "
                    + disclosurePanelList.size() + " panels found.",
                1 == disclosurePanelList.size());

        } catch (Exception e4) {
            Assert.fail("Failed evaluate gwt-DisclosurePanel: " + e4.getMessage());
        }

        comfirmNavigationComponents();
    }

    private void comfirmNavigationComponents() {

        //- check menu buttons
        try {

            List<WebElement> buttonList = new ArrayList<WebElement>();
            try {
                buttonList = browser.findElements(By.className(
                    PropUtils.get("tag.wise-gwt-Button")));
                Assert.assertTrue("Endpoint button list should have 3 entries but found : "
                    + buttonList.size(), (3 == buttonList.size()));
            } catch (Exception e1) {
                Assert.fail("Failed evaluate tag.wise-gwt-Button: " + e1.getMessage());
            }


            String backLabel = PropUtils.get("label.wise-gwt-Button.Back");
            String cancelLabel = PropUtils.get("label.wise-gwt-Button.Cancel");
            for (WebElement we : buttonList) {
                if (we.isDisplayed()) {
                    if (we.getText().equals(backLabel)) {
                        Assert.assertTrue("Error " + backLabel + "is expected to be enabled, but is not.", we.isEnabled());
                    } else if (we.getText().equals(cancelLabel)) {
                        Assert.assertTrue("Error " + cancelLabel + "is expected to be enabled, but is not.", we.isEnabled());
                    }  else {
                        Assert.fail("Unknown button with label " + we.getText());
                    }
                }
            }

        } catch(Exception e2) {
            Assert.fail("Failed menu button evaluation on URL: " + browser.getCurrentUrl());
        }
    }
}
