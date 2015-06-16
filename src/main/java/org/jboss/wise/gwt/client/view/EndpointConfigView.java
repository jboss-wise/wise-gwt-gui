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
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DoubleBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.IntegerBox;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.SimpleCheckBox;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import java.util.HashMap;
import org.jboss.wise.gwt.client.presenter.EndpointConfigPresenter;
import org.jboss.wise.gwt.client.ui.WiseTreeItem;
import org.jboss.wise.gwt.shared.WsdlInfo;
import org.jboss.wise.gwt.shared.tree.element.ComplexTreeElement;
import org.jboss.wise.gwt.shared.tree.element.EnumerationTreeElement;
import org.jboss.wise.gwt.shared.tree.element.GroupTreeElement;
import org.jboss.wise.gwt.shared.tree.element.ParameterizedTreeElement;
import org.jboss.wise.gwt.shared.tree.element.RequestResponse;
import org.jboss.wise.gwt.shared.tree.element.TreeElement;

/**
 * User: rsearls
 * Date: 3/9/15
 */
public class EndpointConfigView extends Composite implements EndpointConfigPresenter.Display {

   private final int COL_ONE = 0;
   private final int COL_TWO = 1;

   private final Button invokeButton;
   private final Button previewButton;
   private final Button cancelButton;
   private final Button backButton;

   private HashMap<String, TreeElement> lazyLoadMap = new HashMap<String, TreeElement>();

   private VerticalPanel baseVerticalPanel;
   private TreeElement rootParamNode = null;
   private RequestResponse msgInvocationResult;
   private int widgetCountOffset = 0;

   private TextBox wsdlAddress;
   private TextBox user;
   private PasswordTextBox password;
   private Tree treeRoot;

   public EndpointConfigView() {

      SimplePanel contentDetailsDecorator = new SimplePanel();
      contentDetailsDecorator.setWidth("100%");
      contentDetailsDecorator.setWidth("640px");
      initWidget(contentDetailsDecorator);

      baseVerticalPanel = new VerticalPanel();
      baseVerticalPanel.setWidth("100%");

      FlexTable fTable = createCredentialOverRidePanel();
      baseVerticalPanel.add(fTable);

      HorizontalPanel menuPanel = new HorizontalPanel();
      invokeButton = new Button("Invoke");
      cancelButton = new Button("Cancel");
      previewButton = new Button("Preview Message");
      backButton = new Button("Back");
      menuPanel.add(backButton);
      menuPanel.add(previewButton);
      menuPanel.add(invokeButton);
      menuPanel.add(cancelButton);
      baseVerticalPanel.add(menuPanel);

      contentDetailsDecorator.add(baseVerticalPanel);
      widgetCountOffset = 2;
   }

   public HasClickHandlers getInvokeButton() {

      return invokeButton;
   }

   public HasClickHandlers getPreviewButton() {

      return previewButton;
   }

   public HasClickHandlers getCancelButton() {

      return cancelButton;
   }

   public HasClickHandlers getBackButton() {

      return backButton;
   }

   public Widget asWidget() {

      return this;
   }

   public void setData(RequestResponse data) {

      msgInvocationResult = data;
      rootParamNode = data.getTreeElement();
      generateDataDisplay();
   }

   private void generateDataDisplay() {

      treeRoot = new Tree();

      for (TreeElement child : rootParamNode.getChildren()) {
         WiseTreeItem parentItem = generateDisplayObject(new WiseTreeItem(), child);
         parentItem.setState(true);
         treeRoot.addItem(parentItem.getChild(0));
      }


      baseVerticalPanel.insert(createFullnamePanel(),
         baseVerticalPanel.getWidgetCount() - widgetCountOffset);
      baseVerticalPanel.insert(treeRoot,
         baseVerticalPanel.getWidgetCount() - widgetCountOffset);

   }

   protected WiseTreeItem generateDisplayObject(WiseTreeItem parentItem,
                                                TreeElement parentTreeElement) {

      if (TreeElement.SIMPLE.equals(parentTreeElement.getKind())) {
         WiseTreeItem treeItem = new WiseTreeItem();
         HorizontalPanel hPanel = new HorizontalPanel();
         treeItem.setWidget(hPanel);
         treeItem.setState(true);

         Label label = new Label(getBaseType(parentTreeElement.getClassType()) + " : "
            + parentTreeElement.getName());
         Widget widget = getWidget(parentTreeElement);
         widget.addStyleName(WiseTreeItem.CSS_ENABLEBLK);
         hPanel.add(label);

         SimpleCheckBox checkBox = null;
         if (widget instanceof TextBox) {
            checkBox = new SimpleCheckBox();
            hPanel.add(checkBox);
            checkBox.addStyleName(WiseTreeItem.CSS_ENABLEBLK);
            ((TextBox) widget).addKeyUpHandler(new LeafKeyUpHandler(checkBox));
         }

         hPanel.add(widget);
         parentItem.addItem(treeItem);

         treeItem.setWTreeElement(parentTreeElement);
         treeItem.postCreateProcess();

      } else if (parentTreeElement instanceof ComplexTreeElement) {

         HorizontalPanel hPanel = new HorizontalPanel();
         hPanel.add(new Label(getBaseType(parentTreeElement.getClassType())
            + " : " + parentTreeElement.getName()));
         SimpleCheckBox checkBox = new SimpleCheckBox();
         checkBox.setValue(true);

         hPanel.add(checkBox);
         WiseTreeItem treeItem = new WiseTreeItem(hPanel);
         checkBox.addClickHandler(new CheckBoxClickHandler(treeItem));

         for (TreeElement child : parentTreeElement.getChildren()) {
            generateDisplayObject(treeItem, child);
         }

         treeItem.setState(true);
         parentItem.addItem(treeItem);
         lazyLoadMap.put(parentTreeElement.getClassType(), parentTreeElement);

         treeItem.setWTreeElement(parentTreeElement);
         treeItem.postCreateProcess();

      } else if (parentTreeElement instanceof ParameterizedTreeElement) {

         HorizontalPanel hPanel = new HorizontalPanel();
         WiseTreeItem treeItem = new WiseTreeItem();
         treeItem.setWidget(hPanel);

         hPanel.add(new Label(parentTreeElement.getClassType()
            + " : " + parentTreeElement.getName()));

         for (TreeElement child : parentTreeElement.getChildren()) {
            generateDisplayObject(treeItem, child);
         }

         treeItem.setState(true);
         parentItem.addItem(treeItem);

         treeItem.setWTreeElement(parentTreeElement);
         treeItem.postCreateProcess();

      } else if (parentTreeElement instanceof GroupTreeElement) {

         WiseTreeItem treeItem = new WiseTreeItem();
         TreeElement gChild = ((GroupTreeElement) parentTreeElement).getProtoType();

         HorizontalPanel gPanel = new HorizontalPanel();
         Button addButton = new Button("add");
         gPanel.add(new Label(getBaseType(parentTreeElement.getClassType())
            + "<" + getBaseType(gChild.getClassType()) + ">"
            + " : " + parentTreeElement.getName()));
         gPanel.add(addButton);
         treeItem.setWidget(gPanel);

         addButton.addClickHandler(new AddParamerterizeBlockClickHandler(this,
            treeItem, (GroupTreeElement) parentTreeElement));
         parentItem.addItem(treeItem);

         treeItem.setWTreeElement(parentTreeElement);
         treeItem.postCreateProcess();

         if (!TreeElement.LAZY.equals(gChild.getKind())) {
            lazyLoadMap.put(gChild.getClassType(), gChild);
         }

      } else if (parentTreeElement instanceof EnumerationTreeElement) {
         WiseTreeItem treeItem = new WiseTreeItem();
         HorizontalPanel hPanel = createEnumerationPanel((EnumerationTreeElement) parentTreeElement);
         treeItem.setWidget(hPanel);
         treeItem.setState(true);

         parentItem.addItem(treeItem);

         treeItem.setWTreeElement(parentTreeElement);
         treeItem.postCreateProcess();

      } else {
         WiseTreeItem treeItem = new WiseTreeItem();
         HorizontalPanel hPanel = new HorizontalPanel();
         treeItem.addItem(hPanel);
         treeItem.setState(true);

         treeItem.setText("UNKNOWN: " + getBaseType(parentTreeElement.getClassType()) + " : "
            + parentTreeElement.getName());
         parentItem.addItem(treeItem);

         treeItem.setWTreeElement(parentTreeElement);
         treeItem.postCreateProcess();
      }

      return parentItem;
   }


   private HorizontalPanel createEnumerationPanel(EnumerationTreeElement eNode) {

      HorizontalPanel hPanel = new HorizontalPanel();
      Label label = new Label(getBaseType(eNode.getClassType()));
      hPanel.add(label);
      ListBox lBox = new ListBox();
      lBox.setSelectedIndex(-1);
      hPanel.add(lBox);

      // put emun names in the list
      lBox.addItem("");
      for (String s : eNode.getEnumValues()) {
         lBox.addItem(s);
      }

      return hPanel;
   }

   private HorizontalPanel createFullnamePanel() {

      HorizontalPanel hPanel = new HorizontalPanel();
      hPanel.add(new Label(msgInvocationResult.getOperationFullName()));
      return hPanel;
   }


   private FlexTable createCredentialOverRidePanel() {

      FlexTable fTable = new FlexTable();
      fTable.setCellSpacing(2);
      fTable.setCellPadding(2);
      fTable.setBorderWidth(2);
      fTable.setWidth("100%");

      fTable.getColumnFormatter().setWidth(COL_ONE, "20%");
      fTable.getColumnFormatter().setWidth(COL_TWO, "40%");

      wsdlAddress = new TextBox();
      wsdlAddress.setWidth("28em");
      user = new TextBox();
      password = new PasswordTextBox();

      fTable.setWidget(0, COL_ONE, new Label("Override target address: "));
      fTable.setWidget(0, COL_TWO, wsdlAddress);

      fTable.setWidget(1, COL_ONE, new Label("User: "));
      fTable.setWidget(1, COL_TWO, user);

      fTable.setWidget(2, COL_ONE, new Label("Password: "));
      fTable.setWidget(2, COL_TWO, password);

      return fTable;
   }


   private Widget getWidget(TreeElement pNode) {

      if ("java.lang.String".endsWith(pNode.getClassType())
         || "char".equals(pNode.getClassType())
         || "java.lang.Object".equals(pNode.getClassType())) {
         return new TextBox();

      } else if ("java.lang.Integer".equals(pNode.getClassType())
         || "int".equals(pNode.getClassType())) {
         IntegerBox iBox = new IntegerBox();
         iBox.setValue(0);
         return iBox;

      } else if ("java.lang.Double".equals(pNode.getClassType())
         || "long".equals(pNode.getClassType())
         || "float".equals(pNode.getClassType())
         || "double".equals(pNode.getClassType())) {
         DoubleBox dBox = new DoubleBox();
         dBox.setValue(0.0);
         return dBox;
      }

      return new Label("UNKNOWN TYPE: " + pNode.getClassType());
   }


   public static String getBaseType(String src) {

      int indx = src.lastIndexOf(".");
      String t = src;
      if (indx > -1) {
         t = src.substring(indx + 1);
      }
      return t;
   }

   public WsdlInfo getWsdlInfo() {

      return new WsdlInfo(wsdlAddress.getValue(), user.getValue(), password.getValue());
   }

   public TreeElement getParamsConfig() {

      int cnt = treeRoot.getItemCount();
      for (int i = 0; i < cnt; i++) {
         ((WiseTreeItem) treeRoot.getItem(i)).postProcess();
      }

      return rootParamNode;
   }

   public class AddParamerterizeBlockClickHandler implements ClickHandler {
      private EndpointConfigView endpointConfigView;
      private WiseTreeItem treeItem;
      private GroupTreeElement parentTreeElement;

      public AddParamerterizeBlockClickHandler(EndpointConfigView endpointConfigView,
                                               WiseTreeItem treeItem,
                                               GroupTreeElement parentTreeElement) {

         this.endpointConfigView = endpointConfigView;
         this.treeItem = treeItem;
         this.parentTreeElement = parentTreeElement;
      }

      public void onClick(ClickEvent event) {

         // replace the lazyLoad reference object with the real object
         TreeElement cloneChild = null;
         if (TreeElement.LAZY.equals(parentTreeElement.getProtoType().getKind())) {
            TreeElement gChild = lazyLoadMap.get(parentTreeElement.getProtoType().getClassType());
            if (gChild != null) {
               cloneChild = gChild.clone();
            }

         } else {
            cloneChild = parentTreeElement.getProtoType().clone();
         }

         if (cloneChild != null) {

            parentTreeElement.addValue(cloneChild);
            endpointConfigView.generateDisplayObject(treeItem, cloneChild);

            Button rmButton = new Button("remove");
            int cnt = treeItem.getChildCount();
            WiseTreeItem childTreeItem = (WiseTreeItem) treeItem.getChild(cnt - 1);

            Widget childWidget = childTreeItem.getWidget();
            ((HorizontalPanel) childWidget).add(rmButton);

            rmButton.addClickHandler(new RemoveParamerterizeBlockClickHandler(
               childTreeItem, parentTreeElement, cloneChild));

            // strip the unit
            int modulo = treeItem.getChildCount() % 2;
            if (modulo == 0) {
               childTreeItem.addStyleName("evenBlk");
            } else {
               childTreeItem.addStyleName("oddBlk");
            }

            treeItem.setState(true);
         }

      }
   }

   public class RemoveParamerterizeBlockClickHandler implements ClickHandler {
      private GroupTreeElement child;
      private TreeElement gChild;
      private WiseTreeItem treeItem;

      public RemoveParamerterizeBlockClickHandler(WiseTreeItem treeItem,
                                                  GroupTreeElement child,
                                                  TreeElement gChild) {

         this.treeItem = treeItem;
         this.child = child;
         this.gChild = gChild;
      }

      public void onClick(ClickEvent event) {

         // remove generated object
         child.getValueList().remove(gChild);
         scrubTable(treeItem);
      }

      private void scrubTable(WiseTreeItem parentItem) {

         int cnt = parentItem.getChildCount();

         if (cnt == 0) {
            if (parentItem.getParentItem() != null) {
               parentItem.getParentItem().removeItem(parentItem);
            }
         } else {
            for (--cnt; cnt > -1; cnt--) {
               scrubTable((WiseTreeItem) parentItem.getChild(cnt));
            }

            if (parentItem.getParentItem() != null) {
               parentItem.getParentItem().removeItem(parentItem);
            }
         }
      }
   }


   public class CheckBoxClickHandler implements ClickHandler {

      private WiseTreeItem rootTreeItem;

      public CheckBoxClickHandler(WiseTreeItem rootTreeItem) {

         this.rootTreeItem = rootTreeItem;
      }

      public void onClick(ClickEvent event) {
         SimpleCheckBox checkBox = (SimpleCheckBox) event.getSource();
         boolean enable = checkBox.getValue();

         // skip disabling root element but set value to be passed
         if (rootTreeItem.getWTreeElement() != null) {
            rootTreeItem.getWTreeElement().setNil(!enable);
         }

         enableAllChildren(enable, rootTreeItem);
      }

      private void enableAllChildren(boolean enable, WiseTreeItem treeItem) {

         int cnt = treeItem.getChildCount();
         for (int i = 0; i < cnt; i++) {
            WiseTreeItem child = (WiseTreeItem) treeItem.getChild(i);

            // disabled children remain disabled no matter the parent setting.
            if (isChecked(child)) {
               enableAllChildren(enable, child);
            }

            child.setEnableTreeItem(enable);
         }
      }

      private boolean isChecked(WiseTreeItem child) {

         boolean isValue = true;
         if (child != null) {
            SimpleCheckBox checkBox = child.getCheckBox();
            if (checkBox != null) {
               return checkBox.getValue();
            }
         }
         return isValue;
      }
   }

   private class LeafKeyUpHandler implements KeyUpHandler {
      SimpleCheckBox checkBox;

      public LeafKeyUpHandler(SimpleCheckBox checkBox) {

         this.checkBox = checkBox;
      }

      @Override
      public void onKeyUp(KeyUpEvent event) {

         checkBox.setValue(true);
      }
   }
}
