package org.jboss.wise.gwt.shared.tree.element;

import java.io.Serializable;

/**
 * User: rsearls
 * Date: 3/27/15
 */
public class SimpleTreeElement extends TreeElement implements Serializable {
   protected String value;

   public SimpleTreeElement () {
      kind = TreeElement.SIMPLE;
   }

   public String getValue() {

      return value;
   }

   public void setValue(String value) {

      if (value == null || value.isEmpty()) {
         setNil(true);
      } else {
         setNil(false);
      }
      this.value = value;
   }

   @Override
   public TreeElement clone(){
      SimpleTreeElement clone = new SimpleTreeElement();
      clone.setKind(getKind());
      clone.setName(getName());
      clone.setClassType(getClassType());
      clone.setValue(getValue());

      for (TreeElement child : getChildren()) {
         clone.addChild(child.clone());
      }

      return  clone;
   }
}
