package org.jboss.wise.gwt.client.widget;

import com.google.gwt.user.client.ui.Label;

/**
 * User: rsearls
 * Date: 7/31/15
 */
public class StepLabel extends Label {

    public StepLabel(String title) {
        super(title);
        addStyleName("wiseStepLabel");
    }
}
