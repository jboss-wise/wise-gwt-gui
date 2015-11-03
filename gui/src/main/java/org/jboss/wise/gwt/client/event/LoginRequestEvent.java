package org.jboss.wise.gwt.client.event;

import com.google.gwt.event.shared.GwtEvent;

/**
 * User: rsearls
 * Date: 7/21/15
 */
public class LoginRequestEvent extends GwtEvent<LoginRequestEventHandler> {
   public static Type<LoginRequestEventHandler> TYPE = new Type<LoginRequestEventHandler>();

   @Override
   public Type<LoginRequestEventHandler> getAssociatedType() {

      return TYPE;
   }

   @Override
   protected void dispatch(LoginRequestEventHandler handler) {

      handler.onRequestLogin(this);
   }
}

