package org.jboss.wise.test.utils;

import org.jboss.arquillian.graphene.Graphene;
import org.jboss.hal.testsuite.util.PropUtils;
import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.concurrent.TimeUnit;


/**
 * Common actions for setting up page loading
 */
public abstract class WiseTest {

    protected WebDriver browser = null;

    protected void setBrowser (WebDriver browser) {
        this.browser = browser;
    }

    protected void loadStepOneOfThree() {

        WebElement inputBox = null;
        WebElement readWSDLButton = null;

        // Get inputBox
        try{
            inputBox = browser.findElement(By.className(PropUtils.get("homepage.input.box")));
        } catch (NoSuchElementException e1) {
            Assert.fail("Setup ERROR: Unable to find input box for URL: inputBoxLabel: "
                + PropUtils.get("homepage.input.box"));
        }

        // Get (next) button
        try{
            readWSDLButton = browser.findElement(By.className(PropUtils.get("homepage.read.wsdl.button")));
        } catch (NoSuchElementException e2) {
            Assert.fail("Setup ERROR: Unable to find input box for URL: inputBoxLabel: "
                + PropUtils.get("homepage.input.box"));
        }

        // Specify WSDL URL and click (next)
        try {
            inputBox.sendKeys(PropUtils.get("homepage.input.url"));
            readWSDLButton.click();
            Graphene.waitModel().withTimeout(30, TimeUnit.SECONDS).until()
                .element(By.className(PropUtils.get("tag.tree.item"))).is().visible();

        } catch (Exception e3) {
            Assert.fail("Setup ERROR: Failed to retrieve WSDL: "
                + PropUtils.get("homepage.input.url"));
        }
    }

    protected void loadStepTwoOfThree() {

        // retrieve 1 the (string) endpoint in the displayed tree
        String key = PropUtils.get("endpoint.string");
        WebElement element = treeItemlookup(key);
        Assert.assertNotNull("Setup ERROR: Failed to find endpoint, " + key, element);

        element.click();

        // confirm the next button is enabled based upon tree item selection
        WebElement nextButton = browser.findElement(By.className(
            PropUtils.get("tag.wise-gwt-Button-next")));
        Assert.assertTrue("Setup ERROR: Next button should be enabled but is not.",
            nextButton.isEnabled());

        nextButton.click();
        Graphene.waitModel().withTimeout(30, TimeUnit.SECONDS).until()
            .element(By.className(PropUtils.get("tag.gwt-SimpleCheckBox"))).is().visible();
    }

    protected void loadStepThreeOfThree() {

        try {
            WebElement inputBox = browser.findElement(
                By.className(PropUtils.get("tag.gwt-TextBox")));
            Assert.assertNotNull("Setup ERROR: TextBox was expected but was not found.",
                inputBox);
            inputBox.sendKeys("Hello");
        } catch (Exception e2) {
            Assert.fail("Setup ERROR: getting input TextBox: " + e2.getMessage());
        }

        try {
            // confirm the next button is enabled based upon tree item selection
            WebElement nextButton = browser.findElement(By.className(
                PropUtils.get("tag.wise-gwt-Button-next")));
            Assert.assertNotNull("Setup ERROR: Next button was expected but was not found.", nextButton);
            Assert.assertTrue("Setup ERROR: Next button should be enabled but is not.",
                nextButton.isEnabled());

            nextButton.click();
            Graphene.waitModel().withTimeout(30, TimeUnit.SECONDS).until()
                .element(By.className(PropUtils.get("tag.wise-gwt-Button-back"))).is().visible();

        } catch (Exception e3) {
            Assert.fail("Setup ERROR: getting next button: " + e3.getMessage());
        }
    }

    /**
     *
     * @param key
     * @param propKey
     */
    protected void checkStepOneData(String key, String propKey) {

        // retrieve the (string) endpoint in the displayed tree
        WebElement element = treeItemlookup(key);
        Assert.assertNotNull("Did not find endpoint, " + key, element);

        // select endpoint to be tested
        element.click();

        // confirm the next button is enabled based upon tree item selection
        WebElement nextButton = browser.findElement(By.className(
            PropUtils.get("tag.wise-gwt-Button-next")));
        Assert.assertTrue("Next button should be enabled but is not.",
            nextButton.isEnabled());

        nextButton.click();
        Graphene.waitModel().withTimeout(30, TimeUnit.SECONDS).until()
            .element(By.className(propKey)).is().present();
    }

    /**
     *
     * @param inputValue
     * @param isCheckboxPresent     true is a checkbox is expected on the pagetag.gwt-SimpleCheckBox
     */
    protected void checkStepTwoData(String inputValue, boolean isCheckboxPresent) {

        try {
            if (isCheckboxPresent) {
                // a required pre-condition before setting input
                WebElement checkBox = browser.findElement(By.className(
                    PropUtils.get("tag.gwt-SimpleCheckBox")));
                Assert.assertNotNull("At least 1 checkbox was expected to be present but none not found.",
                    checkBox);
                Assert.assertFalse("Checkbox should not be selected by default, but is registering as selected ",
                    checkBox.isSelected());
            }

            // set the input
            try {
                WebElement inputBox = browser.findElement(
                    By.className(PropUtils.get("tag.wise-gwt-inputBox")));
                Assert.assertNotNull("TextBox was expected but was not found.", inputBox);
                inputBox.click();
                inputBox.clear();
                inputBox.sendKeys(inputValue);
            } catch (Exception e2) {
                Assert.fail("Failed to find tag.wise-gwt-inputBox: " + e2.getMessage());
            }

            if (isCheckboxPresent) {
                // a required post-condition after setting input
                WebElement updateCheckBox = browser.findElement(By.className(
                    PropUtils.get("tag.gwt-SimpleCheckBox")));
                // TODO checkbox not showing as checked.
                Assert.assertTrue("Checkbox should be selected but is not registering as such",
                    updateCheckBox.isSelected());
            }

        } catch (Exception e3) {
            Assert.fail("Failed evaluate gwt-SimpleCheckBox: " + e3.getMessage());
        }

    }

    /**
     * The number of treeItems expected in the result list
     * @param resultCnt
     */
    protected void checkStepThreeData(int resultCnt) {

        try {
            List<WebElement>  resultsList = browser.findElements(By.className(
               PropUtils.get("tag.wise-result-treeItem")));
            Assert.assertTrue("Result list should have " + resultCnt + " entries but found : "
               + resultsList.size(), (resultCnt == resultsList.size()));
            //System.out.println("##### webElementList size: " + webElementList.size());

        } catch (Exception e1) {
            Assert.fail("Failed lookup of msg results text: " + e1.getMessage());
        }
    }

    /**
     *
     */
    protected void gotoStepThree() {
        try {
            WebElement nextButton = browser.findElement(By.className(
                PropUtils.get("tag.wise-gwt-Button-next")));
            Assert.assertNotNull("Next button was not found on page " + browser.getCurrentUrl(),
                nextButton);
            Assert.assertTrue("Next button should be enabled but is not.",
                nextButton.isEnabled());

            nextButton.click();

            Graphene.waitModel().withTimeout(30, TimeUnit.SECONDS).until()
                .element(By.className(PropUtils.get("tag.wise-result-treeItem"))).is().present();
        } catch (Exception e5) {
            Assert.fail("Failed use next button to process to next page.");
        }
    }

    /**
     *
     * @param pageTag
     */
    protected void confirmPageLoaded(String pageTag) {

        String currentUrl = browser.getCurrentUrl();
        // TODO fix msg;  should be generic
        Assert.assertTrue("Endpoint Config page not found. URL: " + currentUrl,
            currentUrl.endsWith(pageTag));

    }

    /**
     *  expected text of msg preview and result msg
     * @param msgText  text fragment expected to be contained in soap msg content
     */
    protected void checkMessageDisclosurePanel(String msgText) {

        try {
            WebElement msgResultPanel = browser.findElement(By.className(
                PropUtils.get("tag.wise-msg-preview.DisclosurePanel")));
            msgResultPanel.click();  // open panel
            Assert.assertNotNull("Failed to find the message preview panel.", msgResultPanel);

            By by = By.className(PropUtils.get("tag.wise-msg-preview.DisclosurePanel.content"));
            WebElement msgResultContent = browser.findElement(by);
            Assert.assertNotNull("Failed to find message preview content.", msgResultContent);

            // time is needed for text to be displayed by browser
            Graphene.waitModel().withTimeout(30, TimeUnit.SECONDS).until().element(by).is().visible();

            // debug
            //System.out.println("############ msg preview: " + msgResultContent.getText()
            //    + "\n  isDisplayed: " + msgResultContent.isDisplayed()
            //    + "\n  tagName: " + msgResultContent.getTagName()
            //    + "\n  att class: " + msgResultContent.getAttribute("class"));

            Assert.assertTrue("Expected to find [ " + msgText + " ] containted in msg but msg was [ "
               + msgResultContent.getText() + " ]", msgResultContent.getText().contains(msgText));

            msgResultPanel.click();  // close panel

        } catch (Exception e4) {
            Assert.fail("Failed evaluation message preview: " + e4.getMessage());
        }
    }

    /**
     * Select an endpoint method to be called.
     * @param key
     * @return
     */
    protected WebElement treeItemlookup(String key) {

        WebElement element = null;
        List<WebElement> treeItemsList = browser.findElements(
            By.className(PropUtils.get("tag.tree.item")));

        for (WebElement we : treeItemsList) {
            //System.out.println("## " + we.getText());
            if (we.getText().startsWith(key)) {
                element = we;
                break;
            }
        }

        return element;
    }

}
