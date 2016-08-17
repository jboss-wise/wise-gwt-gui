package org.jboss.wise.gwt.client.widget;

import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;

/**
 * Consolidate standard navigation buttons as a single manageable unit
 * <p>
 * User: rsearls
 * Date: 8/18/15
 */
public class MenuPanel extends HorizontalPanel {
    private final Button backButton = new Button("Back");
    private final Button nextButton = new Button("Next");

    public MenuPanel() {

        addStyleName("wise-menuPanel");
        setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);

        backButton.addStyleName("wise-gwt-Button");
        nextButton.addStyleName("wise-gwt-Button");

        backButton.addStyleName("wise-gwt-Button-back");
        nextButton.addStyleName("wise-gwt-Button-next");

        add(backButton);
        add(nextButton);
    }

    public Button getNextButton() {
        return nextButton;
    }

    public Button getBackButton() {
        return backButton;
    }
}
