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
package org.jboss.wise.gwt.client.event;

import com.google.gwt.event.shared.GwtEvent;

/**   ProcessingExceptionEventHandler
 * User: rsearls
 * Date: 8/11/15
 */
public class ProcessingExceptionEvent extends GwtEvent<ProcessingExceptionEventHandler> {
   public static Type<ProcessingExceptionEventHandler> TYPE = new Type<ProcessingExceptionEventHandler>();
   private final String message;

   public ProcessingExceptionEvent(String message) {

      this.message = message;
   }

   public String getMessage() {

      return message;
   }

   @Override
   public Type<ProcessingExceptionEventHandler> getAssociatedType() {

      return TYPE;
   }

   @Override
   protected void dispatch(ProcessingExceptionEventHandler handler) {

      handler.onProcessingException(this);
   }
}
