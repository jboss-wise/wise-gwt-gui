package org.jboss.wise.test.utils;

import org.jboss.arquillian.graphene.Graphene;
import org.jboss.wise.test.utils.PropUtils;
import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.Set;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.Alert;
import org.openqa.selenium.security.UserAndPassword;
import org.openqa.selenium.TimeoutException;


/**
 * Common actions for setting up page loading
 */
public abstract class WiseTest {

    protected WebDriver browser = null;

    protected void setBrowser (WebDriver browser) {
        this.browser = browser;
    }

   /**
    * provide user credentials to authentication dialog
    * @param baseURL
    */
    protected void userAuthentication(String baseURL) {
        String url = baseURL;
        if (baseURL.contains("?wsdl")) {
            url = baseURL.substring(0, baseURL.indexOf("?wsdl"));
        }

        // an ending slash is required for credentials to be processed
        browser.navigate().to(url + "/");
        //System.out.println("## A: browser URL: " + url + "/");

        // give slower machines a change to load page
        try {
            Thread.sleep(1 *1000);
        } catch (Exception e) {
            Assert.fail("sleep issue: " + e.getMessage());
        }
    }

   protected void loadStepOneOfThree() {
      loadStepOneOfThree(PropUtils.get("homepage.input.url"));

   }
    protected void loadStepOneOfThree(String homepageInputUrl) {

        WebElement inputBox = null;
        WebElement readWSDLButton = null;

        // Get inputBox
        try{
            Graphene.waitModel().withTimeout(30, TimeUnit.SECONDS).until()
               .element(By.className(PropUtils.get("homepage.input.box"))).is().visible();

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

            // property runs test with 2 different URLs
            // One requires input be set the other does not
            String value = System.getProperty("suite.url");
            if (value == null || value.isEmpty() || value.endsWith("wise")) {
                inputBox.clear();
                inputBox.sendKeys(homepageInputUrl);
            }

            readWSDLButton.click();
            Graphene.waitModel().withTimeout(30, TimeUnit.SECONDS).until()
                .element(By.className(PropUtils.get("tag.tree.item"))).is().visible();

        } catch (Exception e3) {
            Assert.fail("Setup ERROR: Failed to retrieve WSDL: "
                + homepageInputUrl);
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
            Assert.fail("Failed use next button to progress to next page.");
        }
    }

   protected void gotoSecurityStepThree() {

      try {
         WebElement nextButton = browser.findElement(By.className(
            PropUtils.get("tag.wise-gwt-Button-next")));
         Assert.assertNotNull("Next button was not found on page " + browser.getCurrentUrl(),
            nextButton);
         Assert.assertTrue("Next button should be enabled but is not.",
            nextButton.isEnabled());

         nextButton.click();

         Graphene.waitModel().withTimeout(30, TimeUnit.SECONDS).until()
            .element(By.className(PropUtils.get("tag.wise-authentication-dialog"))).is().present();

      } catch (Exception e5) {
         Assert.fail("Failed use next button to progress to next page.");
      }

      try {
         WebElement usernameInputBox = browser.findElement(
            By.className(PropUtils.get("tag.wise-credential-username")));
         Assert.assertNotNull("Username TextBox was expected but was not found.", usernameInputBox);
         usernameInputBox.click();
         usernameInputBox.clear();
         usernameInputBox.sendKeys(PropUtils.get("tag.username-credential"));
      } catch (Exception e2) {
         Assert.fail("Failed to find tag.wise-credential-username: " + e2.getMessage());
      }

      try {
         WebElement passwordInputBox = browser.findElement(
            By.className(PropUtils.get("tag.wise-credential-password")));
         Assert.assertNotNull("Password TextBox was expected but was not found.", passwordInputBox);
         passwordInputBox.click();
         passwordInputBox.clear();
         passwordInputBox.sendKeys(PropUtils.get("tag.password-credential"));
      } catch (Exception e3) {
         Assert.fail("Failed to find tag.password-credential: " + e3.getMessage());
      }

      try {

         WebElement sendButton = browser.findElement(By.className(
            PropUtils.get("tag.wise-credential-send-button")));
         Assert.assertNotNull("Login button was not found in dialog " + browser.getCurrentUrl(),
            sendButton);

         sendButton.click();

         Graphene.waitModel().withTimeout(30, TimeUnit.SECONDS).until()
            .element(By.className(PropUtils.get("tag.wise-result-treeItem"))).is().present();
      } catch (Exception e5) {
         Assert.fail("Failed use login button to progress to next page. \n" + e5);
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

    protected void confirmDialogDisplay() {
        try {
            WebElement nextButton = browser.findElement(By.className(
               PropUtils.get("tag.wise-gwt-Button-next")));
            Assert.assertNotNull("Next button was not found on page " + browser.getCurrentUrl(),
               nextButton);
            Assert.assertTrue("Next button should be enabled but is not.",
               nextButton.isEnabled());

            nextButton.click();

            Graphene.waitModel().withTimeout(10, TimeUnit.SECONDS).until()
               .element(By.className(PropUtils.get("tag.wise-authentication-dialog"))).is().present();

            WebElement authDialog = browser.findElement(By.className(
               PropUtils.get("tag.wise-authentication-dialog")));
            Assert.assertNotNull("Authenitcation Dialog was not found on page " + browser.getCurrentUrl(),
               authDialog);

        } catch (Exception e5) {
            Assert.fail("Failed to display Authenitcation Dialog.");
        }
    }

   protected void confirmWindowAlertDisplay() {

      WebElement nextButton = browser.findElement(By.className(
         PropUtils.get("tag.wise-gwt-Button-next")));
      Assert.assertNotNull("Next button was not found on page " + browser.getCurrentUrl(),
         nextButton);
      Assert.assertTrue("Next button should be enabled but is not.",
         nextButton.isEnabled());

      nextButton.click();
      //Graphene.waitModel().withTimeout(10, TimeUnit.SECONDS);

      try {
         Alert a = browser.switchTo().alert();
         Assert.assertNotNull("Error dialog was not found on the page", a);
         //System.out.println("### Alert text: " + a.getText()); // debug
      } catch (org.openqa.selenium.NoAlertPresentException e) {
          Assert.assertFalse("Selenium alert issue: " + e.getMessage(),
              e.getMessage().startsWith("WARNING:"));
      }

   }
}
