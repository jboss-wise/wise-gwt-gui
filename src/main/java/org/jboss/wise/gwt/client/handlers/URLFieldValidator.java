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
package org.jboss.wise.gwt.client.handlers;

import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.regexp.shared.RegExp;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;

/**
 * User: rsearls
 * Date: 8/11/15
 */
public class URLFieldValidator implements KeyPressHandler {

   private static RegExp urlValidator;
   private static RegExp urlPlusTldValidator;

   private TextBox textbox;
   private Label errorLabel;

   public URLFieldValidator(TextBox textbox, Label errorLabel) {
      this.textbox = textbox;
      this.errorLabel = errorLabel;

      if (urlValidator == null || urlPlusTldValidator == null) {
         urlValidator = RegExp.compile("^((ftp|http|https)://[\\w@.\\-\\_]+(:\\d{1,5})?(/[\\w#!:.?+=&%@!\\_\\-/]+)*){1}$");
         urlPlusTldValidator = RegExp.compile("^((ftp|http|https)://[\\w@.\\-\\_]+\\.[a-zA-Z]{2,}(:\\d{1,5})?(/[\\w#!:.?+=&%@!\\_\\-/]+)*){1}$");
      }
   }

   public void onKeyPress(KeyPressEvent event) {

      if (this.errorLabel.isVisible()) {
         TextBox eventSrc = (TextBox) event.getSource();

         if (event.getNativeEvent().getKeyCode() == 8 &&
            (eventSrc.getValue().length() - eventSrc.getSelectedText().length()) <= 1) {
            textbox.removeStyleName("urlValidationError");
            errorLabel.setVisible(false);
         } else if (isValidUrl(textbox.getValue())) {
            textbox.removeStyleName("urlValidationError");
            errorLabel.setVisible(false);
         }

      }
   }

   public boolean urlFieldValidation() {
      boolean isValid = isValidUrl(textbox.getValue());

      if(!isValid) {
         if(!errorLabel.isVisible()) {
            textbox.addStyleName("urlValidationError");
            errorLabel.setVisible(true);
         }
      }
      return isValid;
   }

   public boolean isValidUrl(String url) {
      return urlValidator.exec(url) != null;
   }

   public boolean isValidUrl(String url, boolean topLevelDomainRequired) {
      return (topLevelDomainRequired ? urlPlusTldValidator : urlValidator).exec(url) != null;
   }

}
