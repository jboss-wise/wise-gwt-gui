package org.jboss.wise.gwt.shared.tree.element;

import java.util.ArrayList;
import java.util.List;

/**
 * User: rsearls
 * Date: 3/23/15
 */
public class EnumerationTreeElement extends SimpleTreeElement {

   private List<String> enumValues = new ArrayList<String>();

   public EnumerationTreeElement () {
      kind = TreeElement.ENUMERATION;
   }

   public List<String> getEnumValues() {

      return enumValues;
   }

   public void addEnumValue(String value) {

      enumValues.add(value);
   }

   @Override
   public TreeElement clone(){
      EnumerationTreeElement clone = new EnumerationTreeElement();
      clone.setKind(getKind());
      clone.setName(getName());
      clone.setClassType(getClassType());
      clone.setValue(getValue());
      clone.getEnumValues().addAll(enumValues);
      return  clone;
   }
}
