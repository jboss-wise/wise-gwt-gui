package org.jboss.wise.gwt.client.event;

import com.google.gwt.event.shared.EventHandler;

/**
 * User: rsearls
 * Date: 7/21/15
 */
public interface LoginEventHandler extends EventHandler {
   void onLogin(LoginEvent event);
}