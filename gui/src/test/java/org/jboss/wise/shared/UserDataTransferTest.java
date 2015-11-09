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

import java.util.List;
import java.util.Map;
import org.jboss.wise.gui.model.TreeNodeImpl;
import org.jboss.wise.gui.treeElement.EnumerationWiseTreeElement;
import org.jboss.wise.gui.treeElement.SimpleWiseTreeElement;
import org.jboss.wise.gwt.shared.tree.element.EnumerationTreeElement;
import org.jboss.wise.gwt.shared.tree.element.SimpleTreeElement;
import org.jboss.wise.gwt.shared.tree.element.TreeElement;
import org.jboss.wise.shared.GWTClientConversationBean;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * * GWT does not emulate java.lang.reflect.*.  The following is not testable
 *    - ParameterizedTreeElement requires input arg is java.lang.reflect.ParameterizedType.
 *    - ComplexWiseTreeElement requires input arg is java.lang.reflect.Type
 *    - GroupWiseTreeElement  requires input arg is java.lang.reflect.Type
 * User: rsearls
 * Date: 5/12/15
 */
public class UserDataTransferTest {

   @Test
   public void testSimpleTreeElement() {

      GWTClientConversationBean gwtClientConversation = new GWTClientConversationBean();

      SimpleTreeElement src = new SimpleTreeElement();
      src.setValue("tValue");
      SimpleWiseTreeElement dest = new SimpleWiseTreeElement();
      gwtClientConversation.testItUserDataTransfer(src, dest);

      assertEquals(src.getValue(), dest.getValue());

   }

   @Test
   public void testEnumerationTreeElement() {

      GWTClientConversationBean gwtClientConversation = new GWTClientConversationBean();

      EnumerationTreeElement src = new EnumerationTreeElement();
      src.addEnumValue(UserStatusEnum.INACTIVE.value());
      EnumerationWiseTreeElement dest = new EnumerationWiseTreeElement(UserStatusEnum.class, null, null);

      gwtClientConversation.testItUserDataTransfer(src, dest);

      Map<String, String> enumMap = dest.getValidValue();
      String result = enumMap.get(src.getEnumValues().get(0));
      assertNotNull(result);
   }
}
