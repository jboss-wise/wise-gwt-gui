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
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.concurrent.TimeUnit;
import org.jboss.wise.test.utils.WiseTest;


/**
 * Check that the page loads and for the presence of the UI components on the page
 */
@RunWith(Arquillian.class)
public class WsdlViewBasicComponentsTestCase extends WiseTest {
    @Drone
    private WebDriver browser;

    @Page
    private StartPage homePage;

    @Before
    public void before() {
        setBrowser(browser);

        Graphene.goTo(StartPage.class);
        Graphene.waitModel().withTimeout(30, TimeUnit.SECONDS);
    }

    @Test
    public void pageLoadTest() {

        confirmPageLoaded(PropUtils.get("page.list"));
        comfirmUIComponents();
    }

    private void comfirmUIComponents() {

        try {
            Graphene.waitModel().withTimeout(30, TimeUnit.SECONDS).until()
               .element(By.className(PropUtils.get("homepage.input.box"))).is().visible();

            String inputBoxTag = PropUtils.get("homepage.input.box");
            WebElement element = browser.findElement(By.className(inputBoxTag));
        } catch (NoSuchElementException e1) {
            Assert.fail("Unable to find input box for URL inputBox");
        }

        try {
            String buttonTag = PropUtils.get("homepage.read.wsdl.button");
            WebElement element = browser.findElement(By.className(buttonTag));

            String label = PropUtils.get("homepage.read.wsdl.label");
            Assert.assertTrue("Home page should have a [" + label +"] button but found text, "
                + element.getText(), element.getText().equals(label));

            Assert.assertTrue("Button [" + label +"] should be enabled but is not.", element.isEnabled());

        } catch (NoSuchElementException e2) {
            Assert.fail("Unable to find button for " + PropUtils.get("homepage.read.wsdl.label"));
        }

    }
}
