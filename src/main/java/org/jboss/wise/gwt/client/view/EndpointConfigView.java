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
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;

import com.google.gwt.user.client.ui.DoubleBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.IntegerBox;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import org.jboss.wise.gwt.client.presenter.EndpointConfigPresenter;
import org.jboss.wise.gwt.shared.tree.element.ComplexTreeElement;
import org.jboss.wise.gwt.shared.tree.element.EnumerationTreeElement;
import org.jboss.wise.gwt.shared.tree.element.GroupTreeElement;
import org.jboss.wise.gwt.shared.tree.element.ParameterizedTreeElement;
import org.jboss.wise.gwt.shared.tree.element.RequestResponse;
import org.jboss.wise.gwt.shared.tree.element.SimpleTreeElement;
import org.jboss.wise.gwt.shared.tree.element.TreeElement;
import org.jboss.wise.gwt.shared.WsdlInfo;

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
   private StringBuilder debugStrBld; // debug only

   private LinkedHashMap<Widget, TreeElement> newparamWidgetTable =
      new LinkedHashMap<Widget, TreeElement>();
   private LinkedHashMap<TreeItem, GroupTreeElement> newgroupTreeWidgetMap =
      new LinkedHashMap<TreeItem, GroupTreeElement>();
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
         TreeItem parentItem = generateDisplayObject(new TreeItem(), child);
         parentItem.setState(true);
         treeRoot.addItem(parentItem.getChild(0));
      }


      baseVerticalPanel.insert(createFullnamePanel(),
         baseVerticalPanel.getWidgetCount() - widgetCountOffset);
      baseVerticalPanel.insert(treeRoot,
         baseVerticalPanel.getWidgetCount() - widgetCountOffset);

   }

   protected TreeItem generateDisplayObject(TreeItem parentItem,
                                            TreeElement parentTreeElement) {

      if (TreeElement.SIMPLE.equals(parentTreeElement.getKind())) {
         TreeItem treeItem = new TreeItem();
         HorizontalPanel hPanel = new HorizontalPanel();
         treeItem.setWidget(hPanel);
         treeItem.setState(true);

         Label label = new Label(getBaseType(parentTreeElement.getClassType()) + " : "
            + parentTreeElement.getName());
         Widget widget = getWidget(parentTreeElement);
         hPanel.add(label);
         hPanel.add(widget);

         parentItem.addItem(treeItem);
         newparamWidgetTable.put(widget, parentTreeElement);

      } else if (parentTreeElement instanceof ComplexTreeElement) {
         TreeItem treeItem = new TreeItem();
         HorizontalPanel hPanel = new HorizontalPanel();
         treeItem.addItem(hPanel);
         treeItem.setState(true);

         treeItem.setText(getBaseType(parentTreeElement.getClassType())
            + " : " + parentTreeElement.getName());

         for (TreeElement child : parentTreeElement.getChildren()) {
            generateDisplayObject(treeItem, child);
         }

         parentItem.addItem(treeItem);
         lazyLoadMap.put(parentTreeElement.getClassType(), parentTreeElement);

      } else if (parentTreeElement instanceof ParameterizedTreeElement) {
         TreeItem treeItem = new TreeItem();
         HorizontalPanel hPanel = new HorizontalPanel();
         treeItem.addItem(hPanel);
         treeItem.setState(true);

         treeItem.setText(getBaseType(parentTreeElement.getClassType())
            + " : " + parentTreeElement.getName());

         for (TreeElement child : parentTreeElement.getChildren()) {
            generateDisplayObject(treeItem, child);
         }

         parentItem.addItem(treeItem);

      } else if (parentTreeElement instanceof GroupTreeElement) {

         TreeItem treeItem = new TreeItem();
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
         newgroupTreeWidgetMap.put(treeItem, (GroupTreeElement) parentTreeElement);

         if (!TreeElement.LAZY.equals(gChild.getKind())) {
            lazyLoadMap.put(gChild.getClassType(), gChild);
         }

      } else if (parentTreeElement instanceof EnumerationTreeElement) {
         TreeItem treeItem = new TreeItem();
         HorizontalPanel hPanel = createEnumerationPanel((EnumerationTreeElement) parentTreeElement);
         treeItem.addItem(hPanel);
         treeItem.setState(true);

         parentItem.addItem(treeItem);

      } else {
         TreeItem treeItem = new TreeItem();
         HorizontalPanel hPanel = new HorizontalPanel();
         treeItem.addItem(hPanel);
         treeItem.setState(true);

         treeItem.setText("UNKNOWN: " + getBaseType(parentTreeElement.getClassType()) + " : "
            + parentTreeElement.getName());
         parentItem.addItem(treeItem);
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
      newparamWidgetTable.put(lBox, eNode);

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

      if ("java.lang.String".endsWith(pNode.getClassType())) {
         return new TextBox();
      } else if ("java.lang.Integer".equals(pNode.getClassType())) {
         return new IntegerBox();
      } else if ("java.lang.Double".equals(pNode.getClassType())) {
         return new DoubleBox();
      } else if ("long".equals(pNode.getClassType())) {
         return new DoubleBox();
      } else if ("int".equals(pNode.getClassType())) {
         return new IntegerBox();
      } else if ("float".equals(pNode.getClassType())) {
         return new DoubleBox();
      } else if ("double".equals(pNode.getClassType())) {
         return new DoubleBox();
      } else if ("char".equals(pNode.getClassType())) {
         return new TextBox();
      } else if ("java.lang.Object".equals(pNode.getClassType())) {
         return new TextBox();
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

   /**
    * @return
    */
   public TreeElement getParamsConfig() {

      for (Map.Entry<Widget, TreeElement> entry : newparamWidgetTable.entrySet()) {
         Widget widget = entry.getKey();
         TreeElement pNode = entry.getValue();

         if (pNode instanceof EnumerationTreeElement) {
            ListBox lBox = (ListBox) widget;
            EnumerationTreeElement eNode = (EnumerationTreeElement) pNode;

            int index = lBox.getSelectedIndex();
            eNode.setValue(lBox.getItemText(index));

         } else {
            getWidgetValue(widget, (SimpleTreeElement) pNode);
         }
      }

      /** debugging
       debugStrBld = new StringBuilder();
       for(TreeElement tElement : rootParamNode.getChildren()) {
       dumpTree(tElement);
       }
       Window.alert(debugStrBld.toString());
       **/
      return rootParamNode;
   }


   private TreeElement getWidgetValue(Widget widget, SimpleTreeElement pNode) {

      if (widget instanceof TextBox) {
         String s = ((TextBox) widget).getValue();
         if (s != null && !s.isEmpty()) {
            pNode.setValue(s);
         }

      } else if (widget instanceof IntegerBox) {
         Integer i = ((IntegerBox) widget).getValue();
         if (i != null) {
            pNode.setValue(i.toString());
         }

      } else if (widget instanceof DoubleBox) {
         Double d = ((DoubleBox) widget).getValue();
         if (d != null) {
            pNode.setValue(d.toString());
         }

      }
      return pNode;
   }

   public class AddParamerterizeBlockClickHandler implements ClickHandler {
      private EndpointConfigView endpointConfigView;
      private TreeItem treeItem;
      private GroupTreeElement parentTreeElement;

      public AddParamerterizeBlockClickHandler(EndpointConfigView endpointConfigView,
                                               TreeItem treeItem,
                                               GroupTreeElement parentTreeElement) {

         this.endpointConfigView = endpointConfigView;
         this.treeItem = treeItem;
         this.parentTreeElement = parentTreeElement;
      }

      public void onClick(ClickEvent event) {

         debugStrBld = new StringBuilder();

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

            TreeItem grpTreeItem = new TreeItem();
            treeItem.addItem(grpTreeItem);
            treeItem.setState(true);

            int modulo = treeItem.getChildCount() % 2;
            if (modulo == 0) {
               grpTreeItem.addStyleName("evenBlk");
            } else {
               grpTreeItem.addStyleName("oddBlk");
            }

            HorizontalPanel hPanel = new HorizontalPanel();
            grpTreeItem.setWidget(hPanel);

            hPanel.add(new Label(getBaseType(parentTreeElement.getClassType()) + " : "
               + parentTreeElement.getName()));

            Button rmButton = new Button("remove");
            hPanel.add(rmButton);

            parentTreeElement.addValue(cloneChild);
            rmButton.addClickHandler(new RemoveParamerterizeBlockClickHandler(
               grpTreeItem, parentTreeElement, cloneChild));

            endpointConfigView.generateDisplayObject(grpTreeItem, cloneChild);
            grpTreeItem.setState(true);
         }
         //Window.alert(debugStrBld.toString());
      }
   }

   public class RemoveParamerterizeBlockClickHandler implements ClickHandler {
      private GroupTreeElement child;
      private TreeElement gChild;
      private TreeItem treeItem;

      public RemoveParamerterizeBlockClickHandler(TreeItem treeItem,
                                                  GroupTreeElement child,
                                                  TreeElement gChild) {

         this.treeItem = treeItem;
         this.child = child;
         this.gChild = gChild;
      }

      public void onClick(ClickEvent event) {

         // remove generated object
         child.getValueList().remove(gChild);

         // remove display widgets
         scrubTable(treeItem);
         TreeItem parent = treeItem.getParentItem();
         parent.removeItem(treeItem);
      }

      private void scrubTable(TreeItem parentItem) {

         int cnt = parentItem.getChildCount();
         if (cnt == 0) {
            parentItem.getParentItem().removeItem(parentItem);
         } else {
            for (--cnt; cnt > -1; cnt--) {
               scrubTable(parentItem.getChild(cnt));
            }
         }
      }
   }

   /**
    * Debuggin only
    *
    * @param tElement
    */
   private void dumpTree(TreeElement tElement) {

      if (TreeElement.SIMPLE.equals(tElement.getKind())) {
         debugStrBld.append("kind: " + tElement.getKind()
            + "  name: " + tElement.getName()
            + "  value: " + ((SimpleTreeElement) tElement).getValue() + "\n");

      } else if (TreeElement.COMPLEX.equals(tElement.getKind())) {
         debugStrBld.append("kind: " + tElement.getKind()
            + "  name: " + tElement.getName()
            + "  value: " + ((SimpleTreeElement) tElement).getValue() + "\n");
         for (TreeElement tChild : tElement.getChildren()) {
            dumpTree(tChild);
         }

      } else if (TreeElement.PARAMETERIZED.equals(tElement.getKind())) {
         debugStrBld.append("kind: " + tElement.getKind()
            + "  name: " + tElement.getName()
            + "  value: " + ((SimpleTreeElement) tElement).getValue() + "\n");
         for (TreeElement tChild : tElement.getChildren()) {
            dumpTree(tChild);
         }

      } else if (TreeElement.GROUP.equals(tElement.getKind())) {
         debugStrBld.append("kind: " + tElement.getKind()
            + "  name: " + tElement.getName() + "\n");

         for (TreeElement tChild : ((GroupTreeElement) tElement).getValueList()) {
            dumpTree(tChild);
         }

      } else if (TreeElement.ENUMERATION.equals(tElement.getKind())) {
         debugStrBld.append("kind: " + tElement.getKind()
            + "  name: " + tElement.getName()
            + "  value: " + ((EnumerationTreeElement) tElement).getValue() + "\n");
      }

   }

}
