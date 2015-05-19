package org.jboss.wise.gwt.shared.tree.element;

import java.io.Serializable;

/**
 * User: rsearls
 * Date: 3/26/15
 */
public class RequestResponse implements Serializable {
   private String operationFullName;
   private TreeElement treeElement;
   private String responseMessage;
   private String errorMessage;

   public String getOperationFullName() {

      return operationFullName;
   }

   public void setOperationFullName(String operationFullName) {

      this.operationFullName = operationFullName;
   }

   public TreeElement getTreeElement() {

      return treeElement;
   }

   public void setTreeElement(TreeElement treeElement) {

      this.treeElement = treeElement;
   }

   public String getResponseMessage() {

      return responseMessage;
   }

   public void setResponseMessage(String responseMessage) {

      this.responseMessage = responseMessage;
   }

   public String getErrorMessage() {

      return errorMessage;
   }

   public void setErrorMessage(String errorMessage) {

      this.errorMessage = errorMessage;
   }
}
