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

import java.util.List;
import java.util.concurrent.TimeUnit;
import org.jboss.wise.test.utils.WiseTest;


/**
 * Check that the page loads and for the presence of the UI components on the page
 */
@RunWith(Arquillian.class)
public class EndpointsViewBasicComponentsTestCase extends WiseTest {
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
    }


    @Test
    public void stepOneOfThreeTest() {

        confirmPageLoaded(PropUtils.get("page.endpoints"));
        comfirmUIComponents();
        comfirmNavigationComponents();
        comfirmData();
    }

    private void comfirmUIComponents(){
        // check page title
        try {
            WebElement pageTitle = browser.findElement(
                By.className(PropUtils.get("tag.wiseStepLabel")));
            Assert.assertNotNull("Page title was expected but was not found.", pageTitle);
        } catch (Exception e1) {
            Assert.fail("Failed to evaluate tag.wiseStepLabel: " + e1.getMessage());
        }
    }

    private void comfirmNavigationComponents() {

        //- check for menu buttons
        try {

            List<WebElement> buttonList = browser.findElements(
                By.className(PropUtils.get("tag.wise-gwt-Button")));
            Assert.assertTrue("Endpoint button list should have 2 entries but found : "
                    + buttonList.size(), (2 == buttonList.size()));

            String backLabel = PropUtils.get("label.wise-gwt-Button.Back");
            String nextLabel = PropUtils.get("label.wise-gwt-Button.Next");
            for (WebElement we : buttonList) {
                if (we.getText().equals(backLabel)) {
                    Assert.assertTrue("Error " + backLabel + "is expected to be enabled, but is not.", we.isEnabled());
                } else if (we.getText().equals(nextLabel)) {
                    Assert.assertFalse("Error " + nextLabel + "is expected to be disabled, but is not.", we.isEnabled());
                } else {
                    Assert.fail("Unknown button with lable " + we.getText());
                }
            }

        } catch(Exception e4) {
            Assert.fail("Failed menu button evaluation on URL: " + browser.getCurrentUrl());
        }
    }

    private void comfirmData() {

        // check for expected endpoint list
        try {

            List<WebElement> treeItemsList = browser.findElements(
                By.className(PropUtils.get("tag.tree.item")));

            Assert.assertTrue("Endpoint list should have 13 entries but found : "
                    + treeItemsList.size(), (13 == treeItemsList.size()));

        } catch(Exception e5) {
            Assert.fail("Failed evaluating URL: " + browser.getCurrentUrl());
        }
    }
}
