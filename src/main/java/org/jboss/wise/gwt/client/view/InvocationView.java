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

import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DisclosurePanel;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.wise.gwt.client.presenter.InvocationPresenter;
import org.jboss.wise.gwt.client.util.TreeImageResource;
import org.jboss.wise.gwt.client.widget.MessageDisplayPanel;
import org.jboss.wise.gwt.client.widget.StepLabel;
import org.jboss.wise.gwt.shared.tree.element.ComplexTreeElement;
import org.jboss.wise.gwt.shared.tree.element.EnumerationTreeElement;
import org.jboss.wise.gwt.shared.tree.element.GroupTreeElement;
import org.jboss.wise.gwt.shared.tree.element.ParameterizedTreeElement;
import org.jboss.wise.gwt.shared.tree.element.RequestResponse;
import org.jboss.wise.gwt.shared.tree.element.SimpleTreeElement;
import org.jboss.wise.gwt.shared.tree.element.TreeElement;


/**
 * User: rsearls
 * Date: 3/26/15
 */
public class InvocationView extends Composite implements InvocationPresenter.Display {
   private final Button backButton;
   private final Button cancelButton;

   @UiField(provided=true)
   private Tree rootNode = null;
   private String responseMessage;
   private MessageDisplayPanel previewMessageDisplayPanel = new MessageDisplayPanel();

   public InvocationView() {

      SimplePanel contentDetailsDecorator = new SimplePanel();
      contentDetailsDecorator.setWidth("100%");
      contentDetailsDecorator.setWidth("640px");
      initWidget(contentDetailsDecorator);

      VerticalPanel contentDetailsPanel = new VerticalPanel();
      contentDetailsPanel.setWidth("100%");

      StepLabel stepTitle = new StepLabel("Step 3 of 3: Result Data");
      contentDetailsPanel.add(stepTitle);

      Tree.Resources resources = new TreeImageResource();
      rootNode = new Tree(resources);
      rootNode.addItem(new TreeItem(SafeHtmlUtils.fromString("")));
      contentDetailsPanel.add(rootNode);

      // result msg display area
      previewMessageDisplayPanel.setHeaderTitle("View Message");
      previewMessageDisplayPanel.setDisplayRefreshButton(false);
      contentDetailsPanel.add(previewMessageDisplayPanel);

      HorizontalPanel menuPanel = new HorizontalPanel();
      menuPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
      backButton = new Button("Back");
      cancelButton = new Button("Cancel");
      menuPanel.add(backButton);
      menuPanel.add(cancelButton);
      contentDetailsPanel.add(menuPanel);
      contentDetailsDecorator.add(contentDetailsPanel);
   }

   public Tree getData() {

      return rootNode;
   }

   public HasClickHandlers getBackButton() {

      return backButton;
   }

   public HasClickHandlers getCancelButton() {

      return cancelButton;
   }

   public Widget asWidget() {

      return this;
   }

   public String getResponseMessage() {

      return responseMessage;
   }

   public void setData(RequestResponse result) {

      rootNode.addItem(new TreeItem(new Label(result.getOperationFullName())));

      if (result.getErrorMessage() != null) {
         TreeItem tItem = new TreeItem(new Label(result.getErrorMessage()));
         tItem.addStyleName("soapFault");

         rootNode.addItem(tItem);
      }

      responseMessage = result.getResponseMessage();
      TreeElement rootParamNode = result.getTreeElement();
      if (rootParamNode != null) {
         for (TreeElement child : rootParamNode.getChildren()) {
            TreeItem parentItem = generateDisplayObject(new TreeItem(), child);
            parentItem.setState(true);
            rootNode.addItem(parentItem.getChild(0));
         }
      }
   }


   protected TreeItem generateDisplayObject(TreeItem parentItem, TreeElement parentTreeElement) {

      if (TreeElement.SIMPLE.equals(parentTreeElement.getKind())) {
         TreeItem treeItem = new TreeItem();
         HorizontalPanel hPanel = new HorizontalPanel();
         treeItem.setWidget(hPanel);
         treeItem.setState(true);

         Label label = new Label(getClassType(parentTreeElement) + parentTreeElement.getName() + " = "
            + ((SimpleTreeElement)parentTreeElement).getValue());
         hPanel.add(label);
         parentItem.addItem(treeItem);

      } else if (parentTreeElement instanceof ComplexTreeElement) {
         TreeItem treeItem = new TreeItem();
         HorizontalPanel hPanel = new HorizontalPanel();
         treeItem.setWidget(hPanel);

         hPanel.add(new Label(getClassType(parentTreeElement) + parentTreeElement.getName()));

         for (TreeElement child : parentTreeElement.getChildren()) {
            generateDisplayObject(treeItem, child);
         }

         treeItem.setState(true);
         parentItem.addItem(treeItem);

      } else if (parentTreeElement instanceof ParameterizedTreeElement) {
         TreeItem treeItem = new TreeItem();
         HorizontalPanel hPanel = new HorizontalPanel();
         treeItem.setWidget(hPanel);
         hPanel.add(new Label(parentTreeElement.getClassType() + " : " + parentTreeElement.getName()));

         for (TreeElement child : parentTreeElement.getChildren()) {
            generateDisplayObject(treeItem, child);
         }

         treeItem.setState(true);
         parentItem.addItem(treeItem);

      } else if (parentTreeElement instanceof GroupTreeElement) {

         TreeItem treeItem = new TreeItem();
         HorizontalPanel gPanel = new HorizontalPanel();

         String typeName = "";
         if (((GroupTreeElement) parentTreeElement).getProtoType() == null) {
            typeName = EndpointConfigView.getBaseType(((GroupTreeElement) parentTreeElement).getRawType());
         } else {
            typeName = getClassType(((GroupTreeElement) parentTreeElement).getProtoType());
         }
         gPanel.add(new Label(typeName
            + "[" + ((GroupTreeElement) parentTreeElement).getValueList().size() + "]"));
         treeItem.setWidget(gPanel);

         for (TreeElement child : ((GroupTreeElement) parentTreeElement).getValueList()) {
            generateDisplayObject(treeItem, child);
         }

         parentItem.addItem(treeItem);
         treeItem.setState(true);

      } else if (parentTreeElement instanceof EnumerationTreeElement) {
         TreeItem treeItem = new TreeItem();
         HorizontalPanel hPanel = new HorizontalPanel();
         treeItem.setWidget(hPanel);
         treeItem.setState(true);

         Label label = new Label(getClassType(parentTreeElement) + parentTreeElement.getName() + " = "
             + ((SimpleTreeElement)parentTreeElement).getValue());
         hPanel.add(label);

         parentItem.addItem(treeItem);

      } else {
         TreeItem treeItem = new TreeItem();
         HorizontalPanel hPanel = new HorizontalPanel();
         treeItem.addItem(hPanel);
         treeItem.setState(true);

         treeItem.setText("UNKNOWN: " + getClassType(parentTreeElement) + parentTreeElement.getName());
         parentItem.addItem(treeItem);
      }

      return parentItem;
   }

   private String getClassType(TreeElement parentTreeElement) {
      String classTypeStr = "";
      if (parentTreeElement != null && parentTreeElement.getClassType() != null) {
         classTypeStr = EndpointConfigView.getBaseType(parentTreeElement.getClassType())
            + " : ";
      }
      return classTypeStr;
   }


   public DisclosurePanel getMessageDisclosurePanel() {
      return previewMessageDisplayPanel.getDisclosurePanel();
   }

   public void showResultMessage(String msg) {
      previewMessageDisplayPanel.showMessage(msg);
   }

   public void clearResultMessage() {
      previewMessageDisplayPanel.clearMessage();
   }
}
