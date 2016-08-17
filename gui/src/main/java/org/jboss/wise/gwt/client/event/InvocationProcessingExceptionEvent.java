package org.jboss.wise.gwt.client.event;

import com.google.gwt.event.shared.GwtEvent;

/**
 * User: rsearls
 * Date: 8/16/15
 */
public class InvocationProcessingExceptionEvent extends GwtEvent<InvocationProcessingExceptionEventHandler> {
    public static Type<InvocationProcessingExceptionEventHandler> TYPE = new Type<InvocationProcessingExceptionEventHandler>();
    private final String message;

    public InvocationProcessingExceptionEvent(String message) {

        this.message = message;
    }

    public String getMessage() {

        return message;
    }

    @Override public Type<InvocationProcessingExceptionEventHandler> getAssociatedType() {

        return TYPE;
    }

    @Override protected void dispatch(InvocationProcessingExceptionEventHandler handler) {

        handler.onProcessingException(this);
    }
}
