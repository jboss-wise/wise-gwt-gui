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
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.Widget;
import java.util.List;
import org.jboss.wise.gwt.client.MainServiceAsync;
import org.jboss.wise.gwt.client.event.BackEvent;
import org.jboss.wise.gwt.client.event.EndpointConfigEvent;
import org.jboss.wise.gwt.shared.Service;
import org.jboss.wise.gwt.shared.WsdlInfo;

/**
 * User: rsearls
 * Date: 3/6/15
 */
public class EndpointsPresenter implements Presenter {
   public interface Display {
      HasClickHandlers getBackButton();

      Tree getData();

      String getId(TreeItem tItem);

      Widget asWidget();

      void setData(List<Service> data);
   }

   private final MainServiceAsync rpcService;
   private final HandlerManager eventBus;
   private final Display display;

   public EndpointsPresenter(MainServiceAsync rpcService, HandlerManager eventBus, Display display) {

      this.rpcService = rpcService;
      this.eventBus = eventBus;
      this.display = display;
      bind();
   }

   public EndpointsPresenter(MainServiceAsync rpcService, HandlerManager eventBus, Display display, WsdlInfo wsdlInfo) {

      this.rpcService = rpcService;
      this.eventBus = eventBus;
      this.display = display;
      bind();

      rpcService.getEndpoints(wsdlInfo, new AsyncCallback<List<Service>>() {
         public void onSuccess(List<Service> result) {

            EndpointsPresenter.this.display.setData(result);
         }

         public void onFailure(Throwable caught) {

            Window.alert("Error retrieving endpoints");
         }
      });
   }

   public void bind() {

      this.display.getBackButton().addClickHandler(new ClickHandler() {
         public void onClick(ClickEvent event) {

            eventBus.fireEvent(new BackEvent());
         }
      });

      this.display.getData().addSelectionHandler(new SelectionHandler<TreeItem>() {
         public void onSelection(SelectionEvent<TreeItem> event) {

            TreeItem tItem = event.getSelectedItem();
            String id = display.getId(tItem);
            if (id != null) {
               eventBus.fireEvent(new EndpointConfigEvent(id));
            }
         }
      });
   }

   public void go(final HasWidgets container) {

      container.clear();
      container.add(display.asWidget());
   }

}
