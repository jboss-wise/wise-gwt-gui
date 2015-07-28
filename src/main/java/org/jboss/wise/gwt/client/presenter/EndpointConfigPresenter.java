/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2015, Red Hat, Inc., and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.jboss.wise.gwt.client.presenter;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.wise.gwt.client.MainServiceAsync;
import org.jboss.wise.gwt.client.event.BackEvent;
import org.jboss.wise.gwt.client.event.CancelledEvent;
import org.jboss.wise.gwt.client.event.InvocationEvent;
import org.jboss.wise.gwt.client.event.LoginCancelEvent;
import org.jboss.wise.gwt.client.event.LoginCancelEventHandler;
import org.jboss.wise.gwt.client.event.LoginEvent;
import org.jboss.wise.gwt.client.event.LoginEventHandler;
import org.jboss.wise.gwt.client.event.LoginRequestEvent;
import org.jboss.wise.gwt.client.event.LoginRequestEventHandler;
import org.jboss.wise.gwt.client.widget.CredentialDialogBox;
import org.jboss.wise.gwt.shared.tree.element.RequestResponse;
import org.jboss.wise.gwt.shared.tree.element.TreeElement;
import org.jboss.wise.gwt.shared.WsdlInfo;

/**
 * User: rsearls
 * Date: 3/9/15
 */
public class EndpointConfigPresenter implements Presenter {

   public interface Display {
      HasClickHandlers getInvokeButton();

      HasClickHandlers getPreviewButton();

      HasClickHandlers getCancelButton();

      HasClickHandlers getBackButton();

      Widget asWidget();

      void setData(RequestResponse data);

      TreeElement getParamsConfig();

      WsdlInfo getWsdlInfo();

      String getOtherServerURL();
   }


   private final MainServiceAsync rpcService;
   private final HandlerManager eventBus;
   private final Display display;

   private boolean isLoginEvent = false;
   private WsdlInfo wsdlInfo = new WsdlInfo();  // todo temp placeholder

   public EndpointConfigPresenter(MainServiceAsync rpcService, HandlerManager eventBus, Display display) {

      this.rpcService = rpcService;
      this.eventBus = eventBus;
      this.display = display;
      bind();
   }

   public EndpointConfigPresenter(MainServiceAsync rpcService, HandlerManager eventBus, Display display, String id) {

      this.rpcService = rpcService;
      this.eventBus = eventBus;
      this.display = display;
      bind();

      rpcService.getEndpointReflection(id, new AsyncCallback<RequestResponse>() {
         public void onSuccess(RequestResponse result) {

            EndpointConfigPresenter.this.display.setData(result);
         }

         public void onFailure(Throwable caught) {

            Window.alert("Error retrieving endpoint reflections");
         }
      });


   }

   public void bind() {

      // must promote for endpoint credentials
      eventBus.addHandler(LoginRequestEvent.TYPE,
         new LoginRequestEventHandler() {
            public void onRequestLogin(LoginRequestEvent event) {
               isLoginEvent = true;
            }
         });

      // record collected login credentials
      eventBus.addHandler(LoginEvent.TYPE,
         new LoginEventHandler() {
            public void onLogin(LoginEvent event) {

               EndpointConfigPresenter.this.wsdlInfo.setUser(event.getUsername());
               EndpointConfigPresenter.this.wsdlInfo.setPassword(event.getPassword());
               doInvoke();
            }
         });

      eventBus.addHandler(LoginCancelEvent.TYPE,
         new LoginCancelEventHandler() {
            @Override
            public void onLoginCancel(LoginCancelEvent event) {
               EndpointConfigPresenter.this.wsdlInfo.setUser("");
               EndpointConfigPresenter.this.wsdlInfo.setPassword("");
            }
         });


      this.display.getInvokeButton().addClickHandler(new ClickHandler() {
         public void onClick(ClickEvent event) {

            doInvoke();
         }
      });

      this.display.getPreviewButton().addClickHandler(new ClickHandler() {
         public void onClick(ClickEvent event) {

            doPreview();
         }
      });

      this.display.getCancelButton().addClickHandler(new ClickHandler() {
         public void onClick(ClickEvent event) {

            eventBus.fireEvent(new CancelledEvent());
         }
      });

      this.display.getBackButton().addClickHandler(new ClickHandler() {
         public void onClick(ClickEvent event) {

            eventBus.fireEvent(new BackEvent());
         }
      });
   }

   public void go(final HasWidgets container) {

      container.clear();
      container.add(display.asWidget());

      if (isLoginEvent) {
         doLogin();
         isLoginEvent = false;
      }
   }

   private void doInvoke() {

      TreeElement pNode = EndpointConfigPresenter.this.display.getParamsConfig();
      this.wsdlInfo.setWsdl(EndpointConfigPresenter.this.display.getOtherServerURL());

      if (pNode == null) {
         Window.alert("getParamsConfig returned NULL");
      } else {
         eventBus.fireEvent(new InvocationEvent(pNode, this.wsdlInfo));
      }
   }

   private void doPreview() {

      TreeElement pNode = EndpointConfigPresenter.this.display.getParamsConfig();
      if (pNode == null) {
         Window.alert("getParamsConfig returned NULL");
      } else {
         rpcService.getRequestPreview(pNode, new AsyncCallback<String>() {
            public void onSuccess(String result) {

               if (result == null) {
                  Window.alert("getRequestPreview returned a NULL string ");
               } else {
                  Window.alert(result);
               }
            }

            public void onFailure(Throwable caught) {

               Window.alert("Error processing getRequestPreview");
            }
         });
      }
   }

   private void doLogin() {

      CredentialDialogBox cDialogBox = new CredentialDialogBox(eventBus, this.wsdlInfo.getUser(),
         "Endpoint credentials required");

      int left = Window.getClientWidth()/ 4;
      int top = Window.getClientHeight()/ 4;
      cDialogBox.setPopupPosition(left, top);
      cDialogBox.show();
   }
}
