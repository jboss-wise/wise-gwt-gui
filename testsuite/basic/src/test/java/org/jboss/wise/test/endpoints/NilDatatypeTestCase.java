package org.jboss.wise.test.endpoints;

import java.util.concurrent.TimeUnit;
import org.jboss.arquillian.drone.api.annotation.Drone;
import org.jboss.arquillian.graphene.Graphene;
import org.jboss.arquillian.graphene.page.Page;
import org.jboss.arquillian.junit.Arquillian;
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

/**
 * Check handling of data type from start to finish
 */
@RunWith(Arquillian.class)
public class NilDatatypeTestCase extends WiseTest {
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
   public void stringTest(){
      // page: step 1
      confirmPageLoaded(PropUtils.get("page.endpoints"));
      checkStepOneData(PropUtils.get("endpoint.string"), PropUtils.get("tag.wise-gwt-inputBox"));

      // page: step 2
      confirmPageLoaded(PropUtils.get("page.config"));
      checkStepTwoData();
      checkMessageDisclosurePanel("<ns2:echoString xmlns:ns2=\"http://ws.jboss.org/datatypes\"/>");
      gotoStepThree();

      // page: step 3
      confirmPageLoaded(PropUtils.get("page.invoke"));
      checkStepThreeData(1);
      checkMessageDisclosurePanel("<ns2:echoStringResponse xmlns:ns2=\"http://ws.jboss.org/datatypes\"/>");
   }

   private void checkStepTwoData() {

      try {

         WebElement checkBox = browser.findElement(By.className(
            PropUtils.get("tag.gwt-SimpleCheckBox")));
         Assert.assertNotNull("At least 1 checkbox was expected to be present but none not found.",
            checkBox);

         WebElement updateCheckBox = browser.findElement(By.className(
            PropUtils.get("tag.gwt-SimpleCheckBox")));

      } catch (Exception e3) {
         Assert.fail("Failed evaluate gwt-SimpleCheckBox: " + e3.getMessage());
      }

   }

}
