package org.jboss.wise.gwt.client.util;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ClientBundleWithLookup;
import com.google.gwt.resources.client.ImageResource;

/**
 * User: rsearls
 * Date: 8/6/15
 */
public interface Images extends ClientBundle, ClientBundleWithLookup {
    Images IMAGE_RESOURCE = GWT.create(Images.class);

    @Source("../imageresource/chevron-right-small.png") ImageResource treeClosed();

    @Source("../imageresource/chevron-bottom-small.png") ImageResource treeLeaf();

    @Source("../imageresource/chevron-bottom-small.png") ImageResource treeOpen();
}
