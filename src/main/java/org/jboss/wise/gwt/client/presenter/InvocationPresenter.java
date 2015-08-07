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
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.event.logical.shared.OpenEvent;
import com.google.gwt.event.logical.shared.OpenHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.DisclosurePanel;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.wise.gwt.client.MainServiceAsync;
import org.jboss.wise.gwt.client.event.BackEvent;
import org.jboss.wise.gwt.client.event.CancelledEvent;
import org.jboss.wise.gwt.client.event.LoginRequestEvent;
import org.jboss.wise.gwt.shared.tree.element.RequestResponse;
import org.jboss.wise.gwt.shared.tree.element.TreeElement;
import org.jboss.wise.gwt.shared.WsdlInfo;
import org.jboss.wise.gwt.shared.WiseWebServiceException;

/**
 * User: rsearls
 * Date: 3/26/15
 */
public class InvocationPresenter implements Presenter {
   public interface Display {
      HasClickHandlers getBackButton();

      HasClickHandlers getCancelButton();

      DisclosurePanel getMessageDisclosurePanel();

      Tree getData();

      Widget asWidget();

      String getResponseMessage();

      void setData(RequestResponse result);

      void showResultMessage(String msg);

      void clearResultMessage();
   }

   private final HandlerManager eventBus;
   private final Display display;

   public InvocationPresenter(MainServiceAsync rpcService, HandlerManager eventBus, Display display) {

      this.eventBus = eventBus;
      this.display = display;
      bind();
   }

   public InvocationPresenter(MainServiceAsync rpcService, HandlerManager eventBus, Display display,
                              TreeElement treeElement, WsdlInfo wsdlInfo) {

      this.eventBus = eventBus;
      this.display = display;
      bind();

      rpcService.getPerformInvocationOutputTree(treeElement, wsdlInfo, new AsyncCallback<RequestResponse>() {
         public void onSuccess(RequestResponse result) {

            InvocationPresenter.this.display.setData(result);
         }

         public void onFailure(Throwable caught) {

            if (caught instanceof WiseWebServiceException) {
               InvocationPresenter.this.eventBus.fireEvent(new BackEvent());
               InvocationPresenter.this.eventBus.fireEvent(new LoginRequestEvent());
            } else {
               Window.alert("Error PerformInvocationOutputTree");
            }
         }
      });
   }

   public void bind() {

      this.display.getBackButton().addClickHandler(new ClickHandler() {
         public void onClick(ClickEvent event) {

            InvocationPresenter.this.display.clearResultMessage();
            eventBus.fireEvent(new BackEvent());
         }
      });

      this.display.getCancelButton().addClickHandler(new ClickHandler() {
         public void onClick(ClickEvent event) {

            InvocationPresenter.this.display.clearResultMessage();
            eventBus.fireEvent(new CancelledEvent());
         }
      });

      this.display.getMessageDisclosurePanel().addOpenHandler(new OpenHandler<DisclosurePanel>() {
         @Override
         public void onOpen(OpenEvent<DisclosurePanel> event) {
            InvocationPresenter.this.display.showResultMessage(display.getResponseMessage());
         }
      });

      this.display.getMessageDisclosurePanel().addCloseHandler(new CloseHandler<DisclosurePanel>() {
         @Override
         public void onClose(CloseEvent<DisclosurePanel> event) {
            // take no action
         }
      });
   }

   public void go(final HasWidgets container) {

      container.clear();
      container.add(display.asWidget());
   }
}
