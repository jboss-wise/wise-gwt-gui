package org.jboss.wise.gui.model;

import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

//import com.google.gwt.user.client.ui.TreeItem;

/**
 * Copied from richfaces
 * <p/>
 * User: rsearls
 * Date: 3/2/15
 */
public class TreeNodeImpl implements TreeNode {
   private static final long serialVersionUID = 1L;

   private List<Object> keysList = null;
   private Map<Object, TreeNode> children = null;
   private boolean leaf;
   //private TreeItem treeItem;

   public TreeNodeImpl() {

      this(false);
   }

   public TreeNodeImpl(boolean leaf) {

      super();

      this.leaf = leaf;

      if (!leaf) {
         keysList = Lists.newArrayList();
         children = Maps.newHashMap();
      }
   }

   public void addChild(Object key, TreeNode child) {

      if (isLeaf()) {
         throw new IllegalStateException("Cannot add children to leaf");
      }

      keysList.add(key);
      children.put(key, child);
      //treeItem = new TreeItem();
   }

   public void insertChild(int idx, Object key, TreeNode child) {

      if (isLeaf()) {
         throw new IllegalStateException("Cannot add children to leaf");
      }

      keysList.add(idx, key);
      children.put(key, child);
   }

   public void removeChild(Object key) {

      if (isLeaf()) {
         return;
      }

      children.remove(key);
      keysList.remove(key);
   }

   public TreeNode getChild(Object key) {

      if (isLeaf()) {
         return null;
      }

      return children.get(key);
   }

   public Iterator<Object> getChildrenKeysIterator() {

      if (isLeaf()) {
         return Iterators.emptyIterator();
      }

      return Iterators.unmodifiableIterator(keysList.iterator());
   }

   public int indexOf(Object key) {

      if (isLeaf()) {
         return -1;
      }

      return keysList.indexOf(key);
   }

   public boolean isLeaf() {

      return leaf;
   }
   /**
    public TreeItem getTreeItem(){
    return treeItem;
    }
    **/
}
