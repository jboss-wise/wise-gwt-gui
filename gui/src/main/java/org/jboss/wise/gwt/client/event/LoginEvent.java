package org.jboss.wise.gwt.client.event;

import com.google.gwt.event.shared.GwtEvent;

/**
 * User: rsearls
 * Date: 7/21/15
 */
public class LoginEvent extends GwtEvent<LoginEventHandler> {
    public static Type<LoginEventHandler> TYPE = new Type<LoginEventHandler>();
    private final String username;
    private final String password;

    public LoginEvent(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    @Override public Type<LoginEventHandler> getAssociatedType() {

        return TYPE;
    }

    @Override protected void dispatch(LoginEventHandler handler) {

        handler.onLogin(this);
    }
}
