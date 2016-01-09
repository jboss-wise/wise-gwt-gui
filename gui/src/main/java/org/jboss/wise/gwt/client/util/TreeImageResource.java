package org.jboss.wise.gwt.client.util;

import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.Tree;

/**
 * User: rsearls
 * Date: 8/6/15
 */
public class TreeImageResource implements Tree.Resources
{

   @Override
   public ImageResource treeClosed()
   {
      return Images.IMAGE_RESOURCE.treeClosed();
   }

   @Override
   public ImageResource treeLeaf()
   {
      return Images.IMAGE_RESOURCE.treeLeaf();
   }

   @Override
   public ImageResource treeOpen()
   {
      return Images.IMAGE_RESOURCE.treeOpen();
   }

}
