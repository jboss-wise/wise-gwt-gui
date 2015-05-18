package org.jboss.wise.gwt.shared.tree.element;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

/**
 * User: rsearls
 * Date: 3/19/15
 */
public abstract class TreeElement implements Serializable {

   private static final long serialVersionUID = 1L;

   public static final String SIMPLE = "simple";
   public static final String BYTE_ARRAY = "byteArray";
   public static final String COMPLEX = "complex";
   public static final String DURATION = "Duration";
   public static final String ENUMERATION = "Enumeration";
   public static final String GROUP = "group";
   public static final String LAZY = "lazy";
   public static final String PARAMETERIZED = "Parameterized";
   public static final String QNAME = "qname";
   public static final String ROOT = "root";
   public static final String XML_GREGORIAN_CAL = "XMLGregorianCalendar";

   protected String id; // hashcode of *WiseTreeElement mapped to
   protected String name;
   protected String kind;
   protected String classType;
   protected List<TreeElement> children = new LinkedList<TreeElement>();
   protected boolean nil; //whether this elements has the attribute xsi:nil set to "true"


   public String getId() {

      return id;
   }

   public void setId(String id) {

      this.id = id;
   }

   public String getKind() {

      return this.kind;
   }

   public void setKind(String kind) {

      this.kind = kind;
   }

   public String getName() {

      return name;
   }

   public void setName(String name) {

      this.name = name;
   }

   public String getClassType() {

      return classType;
   }

   public void setClassType(String classType) {

      this.classType = classType;
   }

   public void addChild(TreeElement child) {

      children.add(child);
   }

   public List<TreeElement> getChildren() {

      return children;
   }

   public boolean isNil() {
      return nil;
   }

   public void setNil(boolean nil) {
      this.nil = nil;
   }

   public String getCleanClassName(String src) {
      String tmpStr = src;
      int index = src.trim().lastIndexOf(" ");
      if (index > -1) {
         tmpStr = src.substring(index + 1);
      }
      return tmpStr;
   }

   public abstract TreeElement clone();
}

