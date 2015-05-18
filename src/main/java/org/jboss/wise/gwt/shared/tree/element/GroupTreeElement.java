package org.jboss.wise.gwt.shared.tree.element;

import java.util.ArrayList;
import java.util.List;

/**
 * User: rsearls
 * Date: 3/21/15
 */
public class GroupTreeElement extends org.jboss.wise.gwt.shared.tree.element.TreeElement {

   private String rawType;
   private org.jboss.wise.gwt.shared.tree.element.TreeElement protoType;
   private List<org.jboss.wise.gwt.shared.tree.element.TreeElement> valueList = new ArrayList<org.jboss.wise.gwt.shared.tree.element.TreeElement>();

   public GroupTreeElement () {
      kind = org.jboss.wise.gwt.shared.tree.element.TreeElement.GROUP;
   }

   public List<org.jboss.wise.gwt.shared.tree.element.TreeElement> getValueList() {

      return valueList;
   }

   public void addValue(org.jboss.wise.gwt.shared.tree.element.TreeElement value) {

      valueList.add(value);
   }

   public String getRawType() {

      return rawType;
   }

   public void setRawType(String rawType) {

      this.rawType = rawType;
   }

   public org.jboss.wise.gwt.shared.tree.element.TreeElement getProtoType() {

      return protoType;
   }

   public void setProtoType(org.jboss.wise.gwt.shared.tree.element.TreeElement protoType) {

      this.protoType = protoType;
   }

   @Override
   public org.jboss.wise.gwt.shared.tree.element.TreeElement clone(){
      GroupTreeElement clone = new GroupTreeElement();
      clone.setKind(getKind());
      clone.setName(getName());
      clone.setClassType(getClassType());
      clone.setProtoType(protoType.clone());
      //clone.getValueList().addAll(getValueList());
      return  clone;
   }
}
