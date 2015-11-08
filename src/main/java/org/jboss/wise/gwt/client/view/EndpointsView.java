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
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.jboss.wise.gwt.client.presenter.EndpointsPresenter;
import org.jboss.wise.gwt.client.util.TreeImageResource;
import org.jboss.wise.gwt.client.widget.MenuPanel;
import org.jboss.wise.gwt.client.widget.StepLabel;
import org.jboss.wise.gwt.shared.Operation;
import org.jboss.wise.gwt.shared.Port;
import org.jboss.wise.gwt.shared.Service;

/**
 * User: rsearls
 * Date: 3/6/15
 */
public class EndpointsView extends Composite implements EndpointsPresenter.Display {

   MenuPanel menuPanel = new MenuPanel();

   private Map<TreeItem, Operation> endpointsMap = new HashMap<TreeItem, Operation>();

   @UiField(provided=true)
   private Tree rootNode;

   public EndpointsView() {

      SimplePanel contentDetailsDecorator = new SimplePanel();
      contentDetailsDecorator.setWidth("100%");
      contentDetailsDecorator.setWidth("640px");
      initWidget(contentDetailsDecorator);

      VerticalPanel contentDetailsPanel = new VerticalPanel();
      contentDetailsPanel.setWidth("100%");

      StepLabel stepTitle = new StepLabel("Step 1 of 3: Select an Endpoint");
      contentDetailsPanel.add(stepTitle);

      Tree.Resources resources = new TreeImageResource();
      rootNode = new Tree(resources);
      rootNode.addItem(new TreeItem(SafeHtmlUtils.fromString("")));
      contentDetailsPanel.add(rootNode);

      menuPanel.getNextButton().setEnabled(false); // wait for user to select endpoint treeItem
      contentDetailsPanel.add(menuPanel);
      contentDetailsDecorator.add(contentDetailsPanel);
   }

   public Tree getData() {

      return rootNode;
   }

   public String getId(TreeItem treeItem) {

      if (treeItem != null) {
         Operation o = endpointsMap.get(treeItem);
         if (o != null) {
            return o.getCurrentOperation();
         }
      }
      return null;
   }

   public HasClickHandlers getBackButton() {
      return menuPanel.getBackButton();
   }

   public Button getNextButton() {
      return menuPanel.getNextButton();
   }

   public Widget asWidget() {

      return this;
   }

   public void setData(List<Service> data) {

      endpointsMap.clear();
      rootNode.removeItems();
      if (data != null) {
         for (Service s : data) {
            TreeItem serviceItem = new TreeItem(SafeHtmlUtils.fromString(s.getName()));
            rootNode.addItem(serviceItem);

            for (Port p : s.getPorts()) {
               TreeItem portItem = new TreeItem(SafeHtmlUtils.fromString(p.getName()));
               serviceItem.addItem(portItem);
               serviceItem.setState(true);

               for (Operation o : p.getOperations()) {
                  TreeItem tItem = new TreeItem(SafeHtmlUtils.fromString(o.getFullName()));
                  portItem.addItem(tItem);
                  portItem.setState(true);
                  tItem.addStyleName("endpoint");
                  endpointsMap.put(tItem, o);
               }
            }
         }
      }
   }
}
