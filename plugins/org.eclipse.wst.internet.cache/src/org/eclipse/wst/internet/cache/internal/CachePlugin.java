/*******************************************************************************
 * Copyright (c) 2001, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

package org.eclipse.wst.internet.cache.internal;

import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Preferences;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipse.wst.internet.cache.internal.preferences.PreferenceConstants;
import org.osgi.framework.BundleContext;

/**
 * The main plugin class to be used in the desktop.
 */
public class CachePlugin extends AbstractUIPlugin 
{
  /**
   * The ID of this plugin.
   */
  public static final String PLUGIN_ID = "org.eclipse.wst.internet.cache";

  /**
   * The shared instance.
   */
  private static CachePlugin plugin;

  /**
   * The constructor.
   */
  public CachePlugin() 
  {
	super();
	plugin = this;
  }

  /**
   * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
   */
  public void start(BundleContext context) throws Exception 
  {
	super.start(context);
	ToCacheRegistryReader.getInstance().readRegistry();
	Cache.open(Platform.getStateLocation(getBundle()));
	if (getPluginPreferences().contains(PreferenceConstants.CACHE_ENABLED)) 
	{
	  setCacheEnabled(getPluginPreferences().getBoolean(PreferenceConstants.CACHE_ENABLED));
	} 
	else 
	{
	  // The cache is disabled by default.
	  setCacheEnabled(false);
	}
	
	// Restore license preferences
	Preferences prefs = getPluginPreferences();
	LicenseRegistry registry = LicenseRegistry.getInstance();
	String[] licenses = registry.getLicenses();
	int numLicenses = licenses.length;
	for(int i = 0; i < numLicenses; i++)
	{
	  int state = prefs.getInt(licenses[i]);
	  if(state == LicenseRegistry.LICENSE_AGREE.intValue())
	  {
		registry.agreeLicense(licenses[i]);
	  }
	  else if(state == LicenseRegistry.LICENSE_DISAGREE.intValue())
	  {
		registry.disagreeLicense(licenses[i]);
	  }
	}
  }

  /**
   * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
   */
  public void stop(BundleContext context) throws Exception 
  {
	// Save the license state information.
	Preferences prefs = getPluginPreferences();
	LicenseRegistry registry = LicenseRegistry.getInstance();
	String[] licenses = registry.getLicenses();
	int numLicenses = licenses.length;
	for(int i = 0; i < numLicenses; i++)
	{
	  Integer state = registry.getLicenseState(licenses[i]);
      // For states that have been disagreed to this session store
	  // them as disagree.
	  if(state == LicenseRegistry.LICENSE_DISAGREE_THIS_SESSION)
	  {
		state = LicenseRegistry.LICENSE_DISAGREE;
	  }
	  prefs.setValue(licenses[i], state.intValue());
	}
	
	Cache.getInstance().close();
	CacheJob.stopJob();
	super.stop(context);
	plugin = null;
  }

  /**
   * Returns the shared instance.
   * 
   * @return The shared instance.
   */
  public static CachePlugin getDefault() 
  {
	return plugin;
  }

  /**
   * Set whether or not the cache is enabled.
   * 
   * @param enabled If true the cache is enabled, if false it is not enabled.
   */
  public void setCacheEnabled(boolean enabled) 
  {
	getPluginPreferences().setValue(PreferenceConstants.CACHE_ENABLED, enabled);
	CacheJob.stopJob();
  }

  /**
   * Returns true if the cache is enabled, false otherwise.
   * 
   * @return True if the cache is enabled, false otherwise.
   */
  public boolean isCacheEnabled() 
  {
	if (getPluginPreferences().contains(PreferenceConstants.CACHE_ENABLED))
	  return getPluginPreferences().getBoolean(PreferenceConstants.CACHE_ENABLED);
	return true;
  }
  
  
  /**
   * Set whether or not the cache job should ignore a no-cache header
   * 
   * @param enabled If true the cache will ignore no-cache header, false to respect no-cache header
   */
  public void setIgnoreNoCacheHeader(boolean ignore) 
  {
	getPluginPreferences().setValue(PreferenceConstants.IGNORE_NO_CACHE_HEADER, ignore);
  }

  /**
   * Returns true if the cache is ignoring no-cache header, false otherwise.
   * 
   * @return True if the cache is ignoring no-cache header, false otherwise.
   */
  public boolean isIgnoreNoCacheHeader() 
  {
	if (getPluginPreferences().contains(PreferenceConstants.IGNORE_NO_CACHE_HEADER))
	  return getPluginPreferences().getBoolean(PreferenceConstants.IGNORE_NO_CACHE_HEADER);
	return false;
  }

  
  
  /**
   * Set the timeout for caching
   * 
   * @param timeout a timeout in ms
   */
  public void setCacheTimeout(long timeout) 
  {
	getPluginPreferences().setValue(PreferenceConstants.CACHE_DURATION, timeout);
  }

  /**
   * Returns the number of ms to cache a resource, or 86400000 (1 day) as default
   * 
   * @return a number of ms to cache a resource. The default is 86400000 (1 day)
   */
  public long getCacheTimeout() 
  {
	if (getPluginPreferences().contains(PreferenceConstants.CACHE_DURATION)) {
		long l = getPluginPreferences().getLong(PreferenceConstants.CACHE_DURATION); 
		if( l > 0 )
			return l;
	}
	return 86400000; // 1 day
  }

  
  static boolean isRunning() {
	  return plugin != null;
  }
  
  /**
   * Set whether or not the user should be prompted for licenses to which they 
   * have previously disagreed.
   * 
   * @param prompt If true the the user should be prompted, if false the user should not be prompted.
   */
  public void setPromptDisagreedLicenses(boolean prompt) 
  {
	getPluginPreferences().setValue(PreferenceConstants.PROMPT_DISAGREED_LICENSES, prompt);
  }

  /**
   * Returns true if the the user should be prompted for licenses to which they
   * have previously disagreed, false otherwise.
   * 
   * @return True if the user should be prompted, false otherwise.
   */
  public boolean shouldPrompt() 
  {
	if (getPluginPreferences().contains(PreferenceConstants.PROMPT_DISAGREED_LICENSES))
	  return getPluginPreferences().getBoolean(PreferenceConstants.PROMPT_DISAGREED_LICENSES);
	return true;
  }
}
