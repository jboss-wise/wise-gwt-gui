package org.jboss.wise.gwt.client.event;

import com.google.gwt.event.shared.GwtEvent;

/**
 * User: rsearls
 * Date: 7/21/15
 */
public class LoginCancelEvent extends GwtEvent<LoginCancelEventHandler> {
    public static Type<LoginCancelEventHandler> TYPE = new Type<LoginCancelEventHandler>();

    @Override public Type<LoginCancelEventHandler> getAssociatedType() {

        return TYPE;
    }

    @Override protected void dispatch(LoginCancelEventHandler handler) {

        handler.onLoginCancel(this);
    }
}
