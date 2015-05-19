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
package org.jboss.wise.gwt.client.view;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTMLTable;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Label;
import org.jboss.wise.gwt.client.presenter.WsdlPresenter;

import java.util.List;

/**
 * User: rsearls
 * Date: 3/5/15
 */
public class WsdlView extends Composite implements WsdlPresenter.Display {
   private FlexTable contactsTable;
   private FlexTable contentTable;

   private TextBox user;
   private PasswordTextBox password;
   private TextBox wsdlAddress;
   private Button sendButton;
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

   private VerticalPanel createInputDetails() {

      VerticalPanel contentDetailsPanel = new VerticalPanel();
      contentDetailsPanel.setWidth("100%");
      detailsTable = new FlexTable();
      detailsTable.setCellSpacing(0);
      detailsTable.setWidth("100%");
      wsdlAddress = new TextBox();
      wsdlAddress.setWidth("28em");
      user = new TextBox();
      password = new PasswordTextBox();

      //initDetailsTable();
      detailsTable.setWidget(0, 0, new Label("URL"));
      detailsTable.setWidget(0, 1, wsdlAddress);
      detailsTable.setWidget(1, 0, new Label("User"));
      detailsTable.setWidget(1, 1, user);
      detailsTable.setWidget(2, 0, new Label("Password"));
      detailsTable.setWidget(2, 1, password);
      wsdlAddress.setFocus(true);

      contentDetailsPanel.add(detailsTable);
      return contentDetailsPanel;
   }

   private HorizontalPanel createMenuPanel() {

      HorizontalPanel menuPanel = new HorizontalPanel();
      sendButton = new Button("Read WSDL");
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
  /**
   public List<Integer> getSelectedRows() {

      List<Integer> selectedRows = new ArrayList<Integer>();

      for (int i = 0; i < contactsTable.getRowCount(); ++i) {
         CheckBox checkBox = (CheckBox) contactsTable.getWidget(i, 0);
         if (checkBox.getValue()) {
            selectedRows.add(i);
         }
      }

      return selectedRows;
   }
  **/
   public Widget asWidget() {

      return this;
   }

   public HasValue<String> getWsdlAddress() {

      return wsdlAddress;
   }

   public void setWsdlAddress(String wsdlAddress) {

      this.wsdlAddress.setValue(wsdlAddress);
   }

   public HasValue<String> getUser() {

      return user;
   }

   public HasValue<String> getPassword() {

      return password;
   }
}
