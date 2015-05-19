/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2013, Red Hat, Inc., and individual contributors
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
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.wise.gwt.client.MainServiceAsync;
import org.jboss.wise.gwt.client.event.BackEvent;
import org.jboss.wise.gwt.client.event.CancelledEvent;
import org.jboss.wise.gwt.shared.tree.element.RequestResponse;
import org.jboss.wise.gwt.shared.tree.element.TreeElement;
import org.jboss.wise.gwt.shared.WsdlInfo;

/**
 * User: rsearls
 * Date: 3/26/15
 */
public class InvocationPresenter implements Presenter {
   public interface Display {
      HasClickHandlers getBackButton();

      HasClickHandlers getCancelButton();

      HasClickHandlers getViewMessageButton();

      Tree getData();

      Widget asWidget();

      String getResponseMessage();

      void setData(RequestResponse result);
   }

   private final MainServiceAsync rpcService;
   private final HandlerManager eventBus;
   private final Display display;

   public InvocationPresenter(MainServiceAsync rpcService, HandlerManager eventBus, Display display) {

      this.rpcService = rpcService;
      this.eventBus = eventBus;
      this.display = display;
      bind();
   }

   public InvocationPresenter(MainServiceAsync rpcService, HandlerManager eventBus, Display display,
                              TreeElement treeElement, WsdlInfo wsdlInfo) {

      this.rpcService = rpcService;
      this.eventBus = eventBus;
      this.display = display;
      bind();

      rpcService.getPerformInvocationOutputTree(treeElement, wsdlInfo, new AsyncCallback<RequestResponse>() {
         public void onSuccess(RequestResponse result) {

            InvocationPresenter.this.display.setData(result);
         }

         public void onFailure(Throwable caught) {

            Window.alert("Error PerformInvocationOutputTree");
         }
      });
   }

   public void bind() {

      this.display.getBackButton().addClickHandler(new ClickHandler() {
         public void onClick(ClickEvent event) {

            eventBus.fireEvent(new BackEvent());
         }
      });

      this.display.getViewMessageButton().addClickHandler(new ClickHandler() {
         public void onClick(ClickEvent event) {
            Window.alert(display.getResponseMessage());
         }
      });

      this.display.getCancelButton().addClickHandler(new ClickHandler() {
         public void onClick(ClickEvent event) {

            eventBus.fireEvent(new CancelledEvent());
         }
      });


   }

   public void go(final HasWidgets container) {

      container.clear();
      container.add(display.asWidget());
   }

}
