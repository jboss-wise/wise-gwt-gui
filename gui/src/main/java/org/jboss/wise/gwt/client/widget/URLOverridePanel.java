/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2015, Red Hat, Inc., and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.jboss.wise.gwt.client.widget;

import com.google.gwt.user.client.ui.DisclosurePanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import org.jboss.wise.gwt.client.handlers.URLFieldValidator;
import org.jboss.wise.gwt.client.util.Images;

/**
 * User: rsearls
 * Date: 8/3/15
 */
public class URLOverridePanel extends HorizontalPanel {

    private TextBox address;
    private Label errorLabel;
    private URLFieldValidator urlFieldValidor;

    public URLOverridePanel() {

        DisclosurePanel urlOverrideDisclosure = new DisclosurePanel(Images.IMAGE_RESOURCE.treeOpen(),
                Images.IMAGE_RESOURCE.treeClosed(), "Run the service endpoint on another server");
        add(urlOverrideDisclosure);

        HorizontalPanel hPanel = new HorizontalPanel();
        Label label = new Label("URL: ");
        address = new TextBox();
        address.setWidth("28em");
        hPanel.add(label);
        hPanel.add(address);

        errorLabel = new Label("Invalid URL");
        hPanel.add(errorLabel);
        errorLabel.setVisible(false);
        errorLabel.addStyleName("urlValidationError");

        urlFieldValidor = new URLFieldValidator(address, errorLabel);
        address.addKeyPressHandler(urlFieldValidor);

        urlOverrideDisclosure.setContent(hPanel);
    }

    public String getAddress() {
        return address.getValue();
    }

    public boolean urlFieldValidation() {
        return urlFieldValidor.urlFieldValidation();
    }
}
