package org.jboss.wise.gwt.client.widget;

import com.google.gwt.user.client.ui.DisclosurePanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;

/**
 * User: rsearls
 * Date: 8/3/15
 */
public class URLOverridePanel extends HorizontalPanel {

   private TextBox address;

   public URLOverridePanel() {

      DisclosurePanel urlOverrideDisclosure = new DisclosurePanel();
      add(urlOverrideDisclosure);
      urlOverrideDisclosure.setHeader(new Label("Run the service endpoint on another server"));

      HorizontalPanel hPanel = new HorizontalPanel();
      Label label = new Label("URL: ");
      address = new TextBox();
      address.setWidth("28em");
      hPanel.add(label);
      hPanel.add(address);
      urlOverrideDisclosure.setContent(hPanel);
   }

   public String getAddress() {
      return address.getValue();
   }
}
