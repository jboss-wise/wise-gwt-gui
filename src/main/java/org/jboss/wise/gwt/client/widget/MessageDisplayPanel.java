package org.jboss.wise.gwt.client.widget;

import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DisclosurePanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import org.jboss.wise.gwt.client.util.Resources;

/**
 * A standard layout for display of SOAP messages.
 * User: rsearls
 * Date: 7/31/15
 */
public class MessageDisplayPanel extends HorizontalPanel {

   private Label msgPreviewLabel = new Label();
   private DisclosurePanel previewlMsgDisclosure = new DisclosurePanel(
      Resources.INSTANCE.openImage(), Resources.INSTANCE.closeImage(), "Message");
   private Button refreshPreviewMsgButton = new Button();

   public MessageDisplayPanel () {
      add(previewlMsgDisclosure);
      msgPreviewLabel.getElement().getStyle().setProperty("whiteSpace", "pre");
      VerticalPanel previewMsgVerticalPanel = new VerticalPanel();
      refreshPreviewMsgButton.setText("refresh");
      refreshPreviewMsgButton.addStyleName("wise-gwt-Button");
      previewMsgVerticalPanel.add(refreshPreviewMsgButton);
      ScrollPanel previewScrollPanel = new ScrollPanel(msgPreviewLabel);
      previewMsgVerticalPanel.add(previewScrollPanel);
      previewMsgVerticalPanel.addStyleName("wise-message-content");
      previewlMsgDisclosure.setContent(previewMsgVerticalPanel);
   }

   /**
    * set usage of refresh button as needed.
    * @param flag
    */
   public void setDisplayRefreshButton(boolean flag){
      refreshPreviewMsgButton.setVisible(flag);
   }

   public void setHeaderTitle(String title) {
      if (title != null) {
         previewlMsgDisclosure.getHeaderTextAccessor().setText(title);
      }
   }

   public DisclosurePanel getDisclosurePanel() {
      return previewlMsgDisclosure;
   }

   public HasClickHandlers getRefreshButton() {
      return refreshPreviewMsgButton;
   }


   public void showMessage(String msg) {

      if (msg != null) {
         msgPreviewLabel.setText(msg);
      } else {
         msgPreviewLabel.setText("Message was NULL");
      }

      if (!previewlMsgDisclosure.isOpen()) {
         previewlMsgDisclosure.setOpen(true);
      }
   }

   public void clearMessage() {
      msgPreviewLabel.setText("");
   }
}
