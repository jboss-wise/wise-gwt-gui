package org.jboss.wise.test.endpoints;

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
 * Check handling of data type from start to finish
 */
@RunWith(Arquillian.class)
public class RefreshPreviewMsgTestCase extends WiseTest {
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
   }

   @Test
   public void listOfStringTest(){
      // page: step 1
      confirmPageLoaded(PropUtils.get("page.endpoints"));
      checkStepOneData(PropUtils.get("endpoint.list"), PropUtils.get("tag.wise-gwt-button-add"));

      // page: step 2
      confirmPageLoaded(PropUtils.get("page.config"));

      List<String> inputList = new ArrayList<String>();
      inputList.add("hello");
      inputList.add("goodbye");
      checkStepTwoData(inputList);
      gotoStepThree();

      // page: step 3
      confirmPageLoaded(PropUtils.get("page.invoke"));
      checkStepThreeData(1);
      checkMessageDisclosurePanel("<return>goodbye</return>");
   }

   private void checkStepTwoData(List<String> inputList) {

      try {

         WebElement addButton = browser.findElement(By.className(
            PropUtils.get("tag.wise-gwt-button-add")));
         Assert.assertNotNull("An 'Add' button is expected but not found for URL: "
            + browser.getCurrentUrl(), addButton);

         // generate inputboxes
         addButton.click();

         // time is needed for text to be displayed by browser
         Graphene.waitModel().withTimeout(30, TimeUnit.SECONDS).until().element(
            By.className(PropUtils.get("tag.wise-gwt-inputBox"))).is().present();

         WebElement inputBox = browser.findElement(By.className(
            PropUtils.get("tag.wise-gwt-inputBox")));
         Assert.assertNotNull("Input box was not found.", inputBox);
         inputBox.click(); // put focus on inputbox
         inputBox.sendKeys(inputList.get(0));
         //Thread.currentThread().sleep(5000);
         checkMessageDisclosurePanel("<arg0>hello</arg0>");

         inputBox.clear();
         inputBox.sendKeys(inputList.get(1));

         WebElement refreshButton = browser.findElement(By.className(
            PropUtils.get("tag.wise-gwt-button-refresh")));
         Assert.assertNotNull("Refresh button was not found.", refreshButton);
         refreshButton.click();

         checkMessageDisclosurePanel("<arg0>goodbye</arg0>");

      } catch (Exception e3) {
         Assert.fail("Failed processing refresh button: " + e3.getMessage());
      }

   }

}
