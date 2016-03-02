package org.jboss.wise.test.navigation;

import java.net.URL;
import org.jboss.arquillian.test.api.ArquillianResource;
import java.lang.Override;
import org.jboss.arquillian.drone.api.annotation.Drone;
import org.jboss.arquillian.graphene.Graphene;
import org.jboss.arquillian.graphene.page.Page;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.wise.test.utils.StartPage;
import org.jboss.wise.test.utils.PropUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.Assert;
import org.junit.runner.RunWith;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.By;

import java.util.concurrent.TimeUnit;
import java.util.ArrayList;
import java.util.List;
import org.jboss.wise.test.utils.WiseTest;

/**
 * Check handling of lists
 */
@RunWith(Arquillian.class)
public class ListAddRemoveEntryTestCase extends WiseTest {
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
   public void listOfStringTest(){
      // page: step 1
      confirmPageLoaded(PropUtils.get("page.endpoints"));
      checkStepOneData(PropUtils.get("endpoint.list"),
         PropUtils.get("tag.wise-gwt-button-add"));

      // page: step 2
      confirmPageLoaded(PropUtils.get("page.config"));

      List<String> inputList = new ArrayList<String>();
      inputList.add("hello");
      inputList.add("goodbye");
      checkStepTwoData(inputList);
   }

   private void checkStepTwoData(List<String> inputList) {

      try {

         WebElement addButton = browser.findElement(By.className(
            PropUtils.get("tag.wise-gwt-button-add")));
         Assert.assertNotNull("An 'Add' button is expected but not found for URL: "
            + browser.getCurrentUrl(), addButton);

         // generate inputboxes
         for(int i=0; i < inputList.size(); i++) {
            addButton.click();
         }

         // time is needed for text to be displayed by browser
         Graphene.waitModel().withTimeout(30, TimeUnit.SECONDS).until().element(
            By.className(PropUtils.get("tag.wise-gwt-inputBox"))).is().present();

         // get set of generated inputboxes
         List<WebElement> webElementList = browser.findElements(By.className(
            PropUtils.get("tag.wise-input-row")));
         Assert.assertTrue("Expected to find " + inputList.size() + " input items but found "
            + webElementList.size(), inputList.size() == webElementList.size());

         // set input data
         for(int i=0; i < inputList.size(); i++) {
            WebElement we = webElementList.get(i);
            WebElement inputBox = we.findElement(By.className(
               PropUtils.get("tag.wise-gwt-inputBox")));
            Assert.assertNotNull("Did not find inputbox for item number, " + i, inputBox);
            inputBox.click(); // put focus on inputbox
            inputBox.sendKeys(inputList.get(i));

            WebElement checkbox = we.findElement(By.className(
               PropUtils.get("tag.gwt-SimpleCheckBox")));
            Assert.assertNotNull("Did not find checkbox for item number, " + i, checkbox);
            Assert.assertTrue("Expected checkbox for item number " + i + " to be select but is not",
               checkbox.isSelected());
         }

         // confirm remove button works
         List<WebElement> removeButtonList = browser.findElements(By.className(
            PropUtils.get("tag.wise-gwt-button-remove")));
         Assert.assertNotNull("Did not find remove button for item number ", removeButtonList);

         for (int i = 0; i < removeButtonList.size(); i++) {
            WebElement removeButton = browser.findElement(By.className(
               PropUtils.get("tag.wise-gwt-button-remove")));
            Assert.assertNotNull("Did not find remove button for item number, " + i, removeButton);
            removeButton.click();
         }

         // result list
         List<WebElement> resultList = browser.findElements(By.className(
            PropUtils.get("tag.wise-input-row")));
         Assert.assertFalse("Expected to find 0 input items but found "
            + resultList.size(), resultList.isEmpty());

      } catch (Exception e3) {
         Assert.fail("Failed processing list of strings: " + e3.getMessage());
      }

   }
}
