package org.jboss.wise.gwt.client.util;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

/**
 * User: rsearls
 * Date: 9/24/15
 */
public interface Resources extends ClientBundle
{
   // I added this line below
   public static final Resources INSTANCE =  GWT.create(Resources.class);

   @Source("../imageresource/arrow_closed_down.gif")
   ImageResource openImage();

   @Source("../imageresource/arrow_open_up.gif")
   ImageResource closeImage();
}
