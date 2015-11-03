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
package org.jboss.wise.shared;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.*;

import java.util.List;
import org.jboss.wise.gui.model.TreeNodeImpl;
import org.jboss.wise.gui.treeElement.ComplexWiseTreeElement;
import org.jboss.wise.gui.treeElement.EnumerationWiseTreeElement;
import org.jboss.wise.gui.treeElement.ParameterizedWiseTreeElement;
import org.jboss.wise.gui.treeElement.SimpleWiseTreeElement;
import org.jboss.wise.gwt.shared.tree.element.EnumerationTreeElement;
import org.jboss.wise.gwt.shared.tree.element.ParameterizedTreeElement;
import org.jboss.wise.gwt.shared.tree.element.SimpleTreeElement;
import org.jboss.wise.gwt.shared.tree.element.TreeElement;
import org.jboss.wise.shared.GWTClientConversationBean;
import org.junit.Test;


/**
 * GWT does not emulate java.lang.reflect.*.  The following is not testable
 *    - ParameterizedTreeElement requires input arg is java.lang.reflect.ParameterizedType.
 *    - ComplexWiseTreeElement requires input arg is java.lang.reflect.Type
 *    - GroupWiseTreeElement  requires input arg is java.lang.reflect.Type
 */
public class WiseDataPostProcessTest {

   @Test
   public void testSimpleTreeElement() {

      GWTClientConversationBean gwtClientConversation = new GWTClientConversationBean();

      TreeNodeImpl rootNode = new TreeNodeImpl();
      SimpleWiseTreeElement child = new SimpleWiseTreeElement();
      child.setValue("tValue");
      child.setName("nameValue");
      rootNode.addChild(child.getId(), child);
      TreeElement tElement = gwtClientConversation.testItWiseDataPostProcess(rootNode);

      assertEquals(1, tElement.getChildren().size());

      TreeElement resultElement = tElement.getChildren().get(0);
      assertEquals(resultElement.getName(), child.getName());
      assertTrue(resultElement instanceof SimpleTreeElement);
      assertEquals(((SimpleTreeElement)resultElement).getValue(), child.getValue());

   }

   @Test
   public void testEnumerationTreeElement() {

      GWTClientConversationBean gwtClientConversation = new GWTClientConversationBean();

      TreeNodeImpl rootNode = new TreeNodeImpl();
      EnumerationWiseTreeElement child = new EnumerationWiseTreeElement(UserStatusEnum.class,
         "eName", "eValue");
      rootNode.addChild(child.getId(), child);
      TreeElement tElement = gwtClientConversation.testItWiseDataPostProcess(rootNode);

      assertEquals(1, tElement.getChildren().size());

      TreeElement resultElement = tElement.getChildren().get(0);
      assertEquals(resultElement.getName(), child.getName());
      assertTrue(resultElement instanceof EnumerationTreeElement);
      assertEquals(((SimpleTreeElement) resultElement).getValue(), child.getValue());

      List<String> eValueList = ((EnumerationTreeElement)resultElement).getEnumValues();
      assertEquals(UserStatusEnum.values().length, eValueList.size());
   }

}

