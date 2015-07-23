package org.jboss.wise.gwt.shared;

import java.io.Serializable;

/**
 * User: rsearls
 * Date: 6/23/15
 */
public class WiseWebServiceException extends Exception implements Serializable {
   private String message;

   public WiseWebServiceException() {

   }

   public WiseWebServiceException(String message) {
      this.message = message;
   }
}
