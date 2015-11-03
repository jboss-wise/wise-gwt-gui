package org.jboss.wise.gwt.client.widget;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ValueBoxBase;
import com.google.gwt.user.client.ui.VerticalPanel;
import org.jboss.wise.gwt.client.event.LoginCancelEvent;
import org.jboss.wise.gwt.client.event.LoginEvent;

/**
 * User: rsearls
 * Date: 6/23/15
 */
public class CredentialDialogBox extends DialogBox {

   private final HandlerManager eventBus;

   private TextBox userName;
   private PasswordTextBox password;
   private Button cancel;
   private Button send;

   public CredentialDialogBox(final HandlerManager eventBus, String username) {
      this.eventBus = eventBus;

      setGlassEnabled(true);
      setAnimationEnabled(false);
      setText("Authentication Required");

      HorizontalPanel userNamePanel = new HorizontalPanel();
      Label uLabel = new Label("User name:");
      this.userName = new TextBox();
      this.userName.setValue(username);
      userNamePanel.add(uLabel);
      userNamePanel.add(this.userName);

      HorizontalPanel passwdPanel = new HorizontalPanel();
      Label pLabel = new Label("Password:");
      password = new PasswordTextBox();
      passwdPanel.add(pLabel);
      passwdPanel.add(password);

      HorizontalPanel menu = new HorizontalPanel();
      cancel = new Button("Cancel");
      cancel.addClickHandler(new ClickHandler() {
         @Override
         public void onClick(ClickEvent event) {
            eventBus.fireEvent(new LoginCancelEvent());
               CredentialDialogBox.this.hide();
         }
      });

      send = new Button("Log In");
      send.addClickHandler(new ClickHandler() {
         @Override
         public void onClick(ClickEvent event) {
            eventBus.fireEvent(new LoginEvent(userName.getText(), password.getText()));
            CredentialDialogBox.this.hide();
         }
      });

      menu.add(cancel);
      menu.add(send);

      VerticalPanel verticalPanel = new VerticalPanel();
      verticalPanel.add(userNamePanel);
      verticalPanel.add(passwdPanel);
      verticalPanel.add(menu);

      setWidget(verticalPanel);
   }

   public String getUsername() {
      return userName.getText();
   }

   public String getPassword() {
      return password.getText();
   }
}
