/*******************************************************************************
 * Copyright (c) 2003, 2015 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.common.tests;

import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IPluginDescriptor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Plugin;
import org.osgi.framework.BundleContext;

public class CommonTestsPlugin extends Plugin {
	public static String PLUGIN_ID = "org.eclipse.wst.common.tests";
	public static CommonTestsPlugin instance = null;
	public IExtensionPoint dataModelVerifierExt = null;
	
	/**
	 * @param descriptor
	 */
	public CommonTestsPlugin(IPluginDescriptor descriptor) {
		super(descriptor);
		instance = this;
		dataModelVerifierExt = descriptor.getExtensionPoint("DataModelVerifier");
	}

	// default constructor for use of start() and stop()
	public CommonTestsPlugin()
	{
	}

	public void start(BundleContext context) throws Exception {
		super.start(context);
		instance = this;
		Platform.getExtensionRegistry().getExtensionPoint(PLUGIN_ID, "DataModelVerifier");
	}

	public void stop(BundleContext context) throws Exception {
		instance = null;
		super.stop(context);
	}
}