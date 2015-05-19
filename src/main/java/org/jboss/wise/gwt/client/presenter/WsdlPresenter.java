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
import org.jboss.wise.gwt.client.MainServiceAsync;
import org.jboss.wise.gwt.client.event.SendWsdlEvent;
import org.jboss.wise.gwt.client.view.WsdlView;
import org.jboss.wise.gwt.shared.WsdlAddress;
import org.jboss.wise.gwt.shared.WsdlInfo;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.Widget;

import java.util.ArrayList;
import java.util.List;

/**
 * User: rsearls
 * Date: 3/5/15
 */
public class WsdlPresenter implements Presenter {

   private List<WsdlAddress> wsdlAddress;

   public interface Display {
      HasClickHandlers getSendButton();

      HasClickHandlers getList();

      void setData(List<String> data);

      int getClickedRow(ClickEvent event);

      Widget asWidget();
   }

   private final MainServiceAsync rpcService;
   private final HandlerManager eventBus;
   private final Display display;

   public WsdlPresenter(MainServiceAsync rpcService, HandlerManager eventBus, Display view) {

      this.rpcService = rpcService;
      this.eventBus = eventBus;
      this.display = view;
   }

   public void bind() {

      display.getSendButton().addClickHandler(new ClickHandler() {
         public void onClick(ClickEvent event) {

            WsdlView view = (WsdlView) display;
            eventBus.fireEvent(new SendWsdlEvent(new WsdlInfo(
               view.getWsdlAddress().getValue(),
               view.getUser().getValue(),
               view.getPassword().getValue())));
         }
      });

      display.getList().addClickHandler(new ClickHandler() {
         public void onClick(ClickEvent event) {

            int selectedRow = display.getClickedRow(event);

            if (selectedRow >= 0) {
               String id = wsdlAddress.get(selectedRow).getDisplayName();
               WsdlView view = (WsdlView) display;
               view.setWsdlAddress(id);
            }
         }
      });
   }

   public void go(final HasWidgets address) {

      bind();
      address.clear();
      address.add(display.asWidget());
      fetchAddressDetails();
   }

   public void sortAddressDetails() {

      for (int i = 0; i < wsdlAddress.size(); ++i) {
         for (int j = 0; j < wsdlAddress.size() - 1; ++j) {
            if (wsdlAddress.get(j).getDisplayName().compareToIgnoreCase(wsdlAddress.get(j + 1).getDisplayName()) >= 0) {
               WsdlAddress tmp = wsdlAddress.get(j);
               wsdlAddress.set(j, wsdlAddress.get(j + 1));
               wsdlAddress.set(j + 1, tmp);
            }
         }
      }
   }

   private void fetchAddressDetails() {

      rpcService.getAddressDetails(new AsyncCallback<ArrayList<WsdlAddress>>() {
         public void onSuccess(ArrayList<WsdlAddress> result) {

            wsdlAddress = result;
            sortAddressDetails();
            List<String> data = new ArrayList<String>();

            for (int i = 0; i < result.size(); ++i) {
               data.add(wsdlAddress.get(i).getDisplayName());
            }

            display.setData(data);
         }

         public void onFailure(Throwable caught) {

            Window.alert("Error fetching address details");
         }
      });

   }
}
