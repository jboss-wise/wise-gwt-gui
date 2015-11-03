package org.jboss.wise.gwt.client.event;

import com.google.gwt.event.shared.GwtEvent;

/**
 * User: rsearls
 * Date: 5/28/15
 */
public class PopupOpenEvent extends GwtEvent<PopupOpenEventHandler> {
   public static Type<PopupOpenEventHandler> TYPE = new Type<PopupOpenEventHandler>();

   @Override
   public Type<PopupOpenEventHandler> getAssociatedType() {

      return TYPE;
   }

   @Override
   protected void dispatch(PopupOpenEventHandler handler) {

      handler.onOpen(this);
   }
}
