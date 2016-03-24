package org.jboss.wise.test.endpoints;

import java.net.URL;
import org.jboss.arquillian.test.api.ArquillianResource;
import java.util.concurrent.TimeUnit;
import org.jboss.arquillian.drone.api.annotation.Drone;
import org.jboss.arquillian.graphene.Graphene;
import org.jboss.arquillian.graphene.page.Page;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.wise.test.utils.PropUtils;
import org.jboss.wise.test.utils.StartPage;
import org.jboss.wise.test.utils.WiseTest;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.Assert;
import org.openqa.selenium.WebDriver;

/**
 * Check handling of data type from start to finish
 */
@RunWith(Arquillian.class)
public class EndpointAuthenticationTestCase extends WiseTest {
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

      try {
         // trick integration-test phase into running this test correctly.
         String value = System.setProperty("suite.url", "");

         loadStepOneOfThree(PropUtils.get("homepage.securityDomain3.input.url"));

         // reset original value
         if (value != null) {
            System.setProperty("suite.url", value);
         }
      } catch (java.lang.RuntimeException e) {
         Assert.fail("Unsetting or Resetting system property failed.\n" + e);
      }
   }

   @Test
   public void stringTest(){
      // page: step 1
      confirmPageLoaded(PropUtils.get("page.endpoints"));
      checkStepOneData(PropUtils.get("endpoint.securityDomain3.string"), PropUtils.get("tag.wise-gwt-inputBox"));

      // page: step 2
      confirmPageLoaded(PropUtils.get("page.config"));
      checkStepTwoData("", false);  // datatype is not nillable; no checkbox
      checkMessageDisclosurePanel("<arg0/>");
      gotoSecurityStepThree();

      // page: step 3
      confirmPageLoaded(PropUtils.get("page.invoke"));
      checkStepThreeData(1);
      checkMessageDisclosurePanel("<return/>");
   }

}
