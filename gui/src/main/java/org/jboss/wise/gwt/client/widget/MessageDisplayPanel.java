package org.jboss.wise.gwt.client.widget;

import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.user.client.ui.*;
import org.jboss.wise.gwt.client.util.Images;

/**
 * A standard layout for display of SOAP messages.
 * User: rsearls
 * Date: 7/31/15
 */
public class MessageDisplayPanel extends HorizontalPanel {

    private Label msgPreviewLabel = new Label();
    private DisclosurePanel previewlMsgDisclosure = null;
    private Button refreshPreviewMsgButton = new Button();

    public MessageDisplayPanel(String title) {
        super();
        previewlMsgDisclosure = new DisclosurePanel(Images.IMAGE_RESOURCE.treeOpen(), Images.IMAGE_RESOURCE.treeClosed(),
                title);
        add(previewlMsgDisclosure);
        msgPreviewLabel.getElement().getStyle().setProperty("whiteSpace", "pre-wrap");

        VerticalPanel previewMsgVerticalPanel = new VerticalPanel();
        refreshPreviewMsgButton.setText("refresh");
        refreshPreviewMsgButton.addStyleName("wise-gwt-Button");
        refreshPreviewMsgButton.addStyleName("wise-gwt-Button-refresh");
        previewMsgVerticalPanel.add(refreshPreviewMsgButton);
        ScrollPanel previewScrollPanel = new ScrollPanel(msgPreviewLabel);
        previewMsgVerticalPanel.add(previewScrollPanel);
        previewMsgVerticalPanel.addStyleName("wise-message-content");
        previewlMsgDisclosure.setContent(previewMsgVerticalPanel);

        previewlMsgDisclosure.addStyleName("wise-msg-preview.DisclosurePanel");
        msgPreviewLabel.addStyleName("wise-msg-preview.DisclosurePanel.content");
    }

    /**
     * set usage of refresh button as needed.
     *
     * @param flag
     */
    public void setDisplayRefreshButton(boolean flag) {
        refreshPreviewMsgButton.setVisible(flag);
    }

    public DisclosurePanel getDisclosurePanel() {
        return previewlMsgDisclosure;
    }

    public HasClickHandlers getRefreshButton() {
        return refreshPreviewMsgButton;
    }

    public void showMessage(String msg) {

        if (msg != null) {
            msgPreviewLabel.setText(msg);
        } else {
            msgPreviewLabel.setText("Message was NULL");
        }

        if (!previewlMsgDisclosure.isOpen()) {
            previewlMsgDisclosure.setOpen(true);
        }
    }

    public void clearMessage() {
        msgPreviewLabel.setText("");
    }
}
