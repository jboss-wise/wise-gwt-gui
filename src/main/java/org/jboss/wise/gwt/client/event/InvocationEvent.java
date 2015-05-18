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
package org.jboss.wise.gwt.client.event;

import com.google.gwt.event.shared.GwtEvent;
import org.jboss.wise.gwt.shared.tree.element.TreeElement;
import org.jboss.wise.gwt.shared.WsdlInfo;

/**
 * User: rsearls
 * Date: 3/26/15
 */
public class InvocationEvent extends GwtEvent<InvocationEventHandler> {
   public static Type<InvocationEventHandler> TYPE = new Type<InvocationEventHandler>();
   private final TreeElement treeElement;
   private final WsdlInfo wsdlInfo;

   public InvocationEvent(TreeElement treeElement, WsdlInfo wsdlInfo) {

      this.treeElement = treeElement;
      this.wsdlInfo = wsdlInfo;
   }

   public TreeElement getId() {

      return treeElement;
   }

   public WsdlInfo getWsdlInfo() {
      return wsdlInfo;
   }

   @Override
   public Type<InvocationEventHandler> getAssociatedType() {

      return TYPE;
   }

   @Override
   protected void dispatch(InvocationEventHandler handler) {

      handler.onInvocation(this);
   }
}
