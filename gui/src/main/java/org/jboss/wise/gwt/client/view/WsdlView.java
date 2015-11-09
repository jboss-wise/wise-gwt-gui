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
package org.jboss.wise.gwt.client.view;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTMLTable;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import java.util.List;
import org.jboss.wise.gwt.client.handlers.URLFieldValidator;
import org.jboss.wise.gwt.client.presenter.WsdlPresenter;

/**
 * User: rsearls
 * Date: 3/5/15
 */
public class WsdlView extends Composite implements WsdlPresenter.Display {
   private FlexTable contactsTable;
   private FlexTable contentTable;

   private TextBox wsdlAddress;
   private Label errorLabel;
   private URLFieldValidator urlFieldValidor;
   private Button sendButton;
   private Button updateWsdlListButton;
   private FlexTable detailsTable;

   public WsdlView() {

      SimplePanel contentTableDecorator = new SimplePanel();
      initWidget(contentTableDecorator);
      contentTableDecorator.setWidth("100%");

      VerticalPanel contentDetailsPanel = new VerticalPanel();
      contentDetailsPanel.setWidth("100%");

      // display sections
      contentDetailsPanel.add(createInputDetails());
      contentDetailsPanel.add(createMenuPanel());
      contentDetailsPanel.add(createWsdlList());

      contentTableDecorator.add(contentDetailsPanel);
   }

   public HasClickHandlers getSendButton() {

      return sendButton;
   }

   public HasClickHandlers getWsdlListButton() {
      return updateWsdlListButton;
   }

   private HorizontalPanel createInputDetails() {

      HorizontalPanel contentDetailsPanel = new HorizontalPanel();
      contentDetailsPanel.add(new Label("URL: "));
      wsdlAddress = new TextBox();
      wsdlAddress.setVisibleLength(56);
      wsdlAddress.addStyleName("wsdl.input.box");
      contentDetailsPanel.add(wsdlAddress);
      errorLabel = new Label("Invalid URL");
      contentDetailsPanel.add(errorLabel);
      errorLabel.setVisible(false);
      errorLabel.addStyleName("urlValidationError");

      wsdlAddress.setFocus(true);

      urlFieldValidor = new URLFieldValidator(wsdlAddress, errorLabel);
      wsdlAddress.addKeyPressHandler(urlFieldValidor);

      return contentDetailsPanel;
   }

   private HorizontalPanel createMenuPanel() {

      HorizontalPanel menuPanel = new HorizontalPanel();
      sendButton = new Button("Read WSDL");
      sendButton.addStyleName("readWsdlButton");
      menuPanel.add(sendButton);
      return menuPanel;
   }

   private FlexTable createWsdlList() {

      contentTable = new FlexTable();
      contentTable.setWidth("100%");
      contentTable.getCellFormatter().setWidth(0, 0, "100%");
      contentTable.getFlexCellFormatter().setVerticalAlignment(0, 0, DockPanel.ALIGN_TOP);

      // Create the list
      contactsTable = new FlexTable();
      contactsTable.setCellSpacing(0);
      contactsTable.setCellPadding(0);
      contactsTable.setWidth("100%");
      contentTable.setWidget(1, 0, contactsTable);

      updateWsdlListButton = new Button("Update list");
      updateWsdlListButton.addStyleName("wsdlListButton");
      contentTable.setWidget(2, 0, updateWsdlListButton);
      return contentTable;
   }

   public HasClickHandlers getList() {

      return contactsTable;
   }

   public void setData(List<String> data) {

      contactsTable.removeAllRows();

      for (int i = 0; i < data.size(); ++i) {
         contactsTable.setText(i, 1, data.get(i));
      }
   }

   public int getClickedRow(ClickEvent event) {

      int selectedRow = -1;
      HTMLTable.Cell cell = contactsTable.getCellForEvent(event);

      if (cell != null) {
         if (cell.getCellIndex() > 0) {
            selectedRow = cell.getRowIndex();
         }
      }

      return selectedRow;
   }

   public Widget asWidget() {

      return this;
   }

   public HasValue<String> getWsdlAddress() {

      return wsdlAddress;
   }

   public void setWsdlAddress(String wsdlAddress) {

      this.wsdlAddress.setValue(wsdlAddress);
   }

   public boolean urlFieldValidation() {
      return urlFieldValidor.urlFieldValidation();
   }

}
