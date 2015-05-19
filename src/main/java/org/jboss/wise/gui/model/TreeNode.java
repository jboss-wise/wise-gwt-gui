package org.jboss.wise.gui.model;

import java.util.Iterator;
//import com.google.gwt.user.client.ui.TreeItem;

/**
 * copies from richFaces.
 *
 * User: rsearls
 * Date: 3/2/15
 */
public interface TreeNode {
    TreeNode getChild(Object key);

    int indexOf(Object key);

    Iterator<Object> getChildrenKeysIterator();

    boolean isLeaf();

    void addChild(Object key, TreeNode child);

    void insertChild(int idx, Object key, TreeNode child);

    void removeChild(Object key);

    //TreeItem getTreeItem();
}
