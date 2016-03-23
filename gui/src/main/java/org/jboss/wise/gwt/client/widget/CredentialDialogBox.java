package org.jboss.wise.gwt.client.widget;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import org.jboss.wise.core.exception.WiseAuthenticationException;
import org.jboss.wise.gwt.client.MainServiceAsync;
import org.jboss.wise.gwt.client.event.LoginCancelEvent;
import org.jboss.wise.gwt.client.event.LoginEvent;
import org.jboss.wise.gwt.shared.WsdlInfo;
import org.jboss.wise.gwt.shared.tree.element.RequestResponse;
import org.jboss.wise.gwt.shared.tree.element.TreeElement;

/**
 * User: rsearls
 * Date: 6/23/15
 */
public class CredentialDialogBox extends DialogBox {

   private final HandlerManager eventBus;

   private TextBox userName;
   private PasswordTextBox password;
   private Label errorMsg = new Label("Unknown username or password.");
   private Button cancel;
   private Button send;
   private final TreeElement pNode;
   private final MainServiceAsync rpcService;

   public CredentialDialogBox(final MainServiceAsync rpcService,
                              final HandlerManager eventBus, String username, TreeElement pNode) {
      this.rpcService = rpcService;
      this.pNode = pNode;
      this.eventBus = eventBus;
      addStyleName("wise-authentication-dialog");

      setGlassEnabled(true);
      setAnimationEnabled(false);
      setText("Authentication Required");

      HorizontalPanel errorMsgPanel = new HorizontalPanel();
      errorMsgPanel.add(errorMsg);
      errorMsg.addStyleName("wise-credentials-error");
      errorMsg.setVisible(false);

      HorizontalPanel userNamePanel = new HorizontalPanel();
      Label uLabel = new Label("User name:");
      this.userName = new TextBox();
      this.userName.setValue(username);
      this.userName.addStyleName("wise-credential-username");
      userNamePanel.add(uLabel);
      userNamePanel.add(this.userName);

      HorizontalPanel passwdPanel = new HorizontalPanel();
      Label pLabel = new Label("Password:");
      password = new PasswordTextBox();
      password.addStyleName("wise-credential-password");
      passwdPanel.add(pLabel);
      passwdPanel.add(password);

      HorizontalPanel menu = new HorizontalPanel();
      cancel = new Button("Cancel");
      cancel.addStyleName("wise-credential-cancel-button");
      cancel.addClickHandler(new ClickHandler() {
         @Override
         public void onClick(ClickEvent event) {
            eventBus.fireEvent(new LoginCancelEvent());
               CredentialDialogBox.this.hide();
         }
      });

      send = new Button("Log In");
      send.addStyleName("wise-credential-send-button");
      send.addClickHandler(new ClickHandler() {
         @Override
         public void onClick(ClickEvent event) {


            WsdlInfo wsdlInfo = new WsdlInfo();
            wsdlInfo.setUser(userName.getText());
            wsdlInfo.setPassword(password.getText());

            rpcService.getPerformInvocationOutputTree(CredentialDialogBox.this.pNode, wsdlInfo, new AsyncCallback<RequestResponse>() {

               public void onSuccess(RequestResponse result) {
                  eventBus.fireEvent(new LoginEvent(userName.getText(), password.getText()));
                  CredentialDialogBox.this.hide();
                  errorMsg.setVisible(false);
               }

               public void onFailure(Throwable caught) {
                  if (caught instanceof WiseAuthenticationException) {
                     errorMsg.setVisible(true);
                  }
               }
            });
         }
      });

      menu.add(cancel);
      menu.add(send);

      VerticalPanel verticalPanel = new VerticalPanel();
      verticalPanel.add(errorMsgPanel);
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
