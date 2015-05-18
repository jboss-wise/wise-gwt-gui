package org.jboss.wise.gwt.shared.tree.element;

import org.jboss.wise.gui.treeElement.WiseTreeElement;

/**
 * User: rsearls
 * Date: 3/23/15
 */
public class TreeElementFactory {

   public static TreeElement create (WiseTreeElement wte) {
      TreeElement treeElement;

      if (WiseTreeElement.COMPLEX.equals(wte.getKind())) {
         treeElement = new org.jboss.wise.gwt.shared.tree.element.ComplexTreeElement();

      } else if (WiseTreeElement.GROUP.equals(wte.getKind())) {
         treeElement = new org.jboss.wise.gwt.shared.tree.element.GroupTreeElement();

      } else if (WiseTreeElement.ENUMERATION.equals(wte.getKind())) {
         treeElement = new org.jboss.wise.gwt.shared.tree.element.EnumerationTreeElement();

      } else if (WiseTreeElement.PARAMETERIZED.equals(wte.getKind())) {
         treeElement = new org.jboss.wise.gwt.shared.tree.element.ParameterizedTreeElement();

      } else {
         treeElement = new org.jboss.wise.gwt.shared.tree.element.SimpleTreeElement();
      }
      return treeElement;
   }
}
