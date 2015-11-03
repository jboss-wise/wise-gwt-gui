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
package org.jboss.wise.gui.model;

import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Copied from richfaces
 * <p/>
 * User: rsearls
 * Date: 3/2/15
 */
public class TreeNodeImpl implements TreeNode {

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
         return Collections.emptyIterator();
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
