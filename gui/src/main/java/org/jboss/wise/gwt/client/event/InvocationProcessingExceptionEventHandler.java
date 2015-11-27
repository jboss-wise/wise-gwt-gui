package org.jboss.wise.gwt.client.event;

import com.google.gwt.event.shared.EventHandler;

/**
 * User: rsearls
 * Date: 8/16/15
 */
public interface InvocationProcessingExceptionEventHandler extends EventHandler {
   void onProcessingException(InvocationProcessingExceptionEvent event);
}