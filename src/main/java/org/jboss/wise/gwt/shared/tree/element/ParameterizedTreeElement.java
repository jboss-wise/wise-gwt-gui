package org.jboss.wise.gwt.shared.tree.element;

/**
 * User: rsearls
 * Date: 4/27/15
 */
public class ParameterizedTreeElement extends SimpleTreeElement {

   public ParameterizedTreeElement () {
      kind = TreeElement.PARAMETERIZED;
   }

   @Override
   public TreeElement clone() {

      ParameterizedTreeElement clone = new ParameterizedTreeElement();
      clone.setKind(getKind());
      clone.setName(getName());
      clone.setClassType(getClassType());

      for (TreeElement child : getChildren()) {
         clone.addChild(child.clone());
      }

      return clone;

   }
}
