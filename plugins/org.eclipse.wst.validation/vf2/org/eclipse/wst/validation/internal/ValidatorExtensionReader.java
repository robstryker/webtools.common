package org.eclipse.wst.validation.internal;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IContributor;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.osgi.util.NLS;
import org.eclipse.wst.validation.AbstractValidator;
import org.eclipse.wst.validation.Validator;
import org.eclipse.wst.validation.internal.model.FilterGroup;
import org.eclipse.wst.validation.internal.model.FilterRule;
import org.eclipse.wst.validation.internal.plugin.ValidationPlugin;

/**
 * Process the validator (version 2) extension point.
 * 
 * @author karasiuk
 *
 */
public class ValidatorExtensionReader {
	
	/**
	 * Read the extensions.
	 * 
	 * @param deep if true load all the configuration elements for each validator, if false
	 * do a shallow load, where only the validator class, id and name's are loaded.
	 */
	public static Validator[] process(boolean deep){
		ValidatorExtensionReader ver = new ValidatorExtensionReader();
		return ver.readRegistry(deep);
	}
	
	/**
	 * Determine if any of the validators need to be migrated, and if so answer a new
	 * Validator array.
	 * 
	 * @param validators the existing validators (from the preferences).
	 *  
	 * @return null if no validators needed to be migrated.
	 */
	public static Validator[] migrate(Validator[] validators){
		ValidatorExtensionReader ver = new ValidatorExtensionReader();
		return ver.migrate2(validators);
	}

	private ValidatorExtensionReader(){}
	
	private Validator[] readRegistry(boolean deep) {
		List<Validator> list = new LinkedList<Validator>();
		IExtensionRegistry registry = Platform.getExtensionRegistry();
		IExtensionPoint extensionPoint = registry.getExtensionPoint(ValidationPlugin.PLUGIN_ID, ExtensionConstants.validator);
		if (extensionPoint == null)return new Validator[0];
				
		for (IExtension ext : extensionPoint.getExtensions()){
			for (IConfigurationElement validator : ext.getConfigurationElements()){
				list.add(processValidator(validator, ext.getUniqueIdentifier(), ext.getLabel(), deep));
			}
		}
		Validator[] val = new Validator[list.size()];
		list.toArray(val);
		return val;
		
	}
	
	/**
	 * Process the validator element in a validator extension.
	 * 
	 * @param validator the validator element
	 * 
	 * @param deep if true load all the configuration elements for each validator, if false
	 * do a shallow load, where only the validator class, id and name's are loaded.
	 * 
	 * @return a configured validator
	 */
	private Validator processValidator(IConfigurationElement validator, String id, String label, boolean deep) {
		Validator.V2 v = null;
		try {
			AbstractValidator vb = (AbstractValidator)validator.createExecutableExtension(ExtensionConstants.AttribClass);
			v = Validator.create(vb).asV2Validator();
			v.setId(id);
			v.setName(label);
			v.setBuildValidation(getAttribute(validator, ExtensionConstants.build, true));
			v.setManualValidation(getAttribute(validator, ExtensionConstants.manual, true));
			v.setVersion(getAttribute(validator, ExtensionConstants.version, 1));
			if (deep){
				IConfigurationElement[] children = validator.getChildren();
				for (int i=0; i<children.length; i++)processValidatorChildren(v, children[i]);
			}
		}
		catch (Exception e){
			ValidationPlugin.getPlugin().handleException(e);
			IContributor contrib = validator.getContributor();
			String message = NLS.bind(ValMessages.ErrConfig, contrib.getName());
			ValidationPlugin.getPlugin().logMessage(IStatus.ERROR, message);
		}
		return v;
	}

	/** 
	 * Process the children of the validator tag, i.e. include and exclude groups.
	 * 
	 *  @param v the validator that we are building up
	 *  @param group the include and exclude elements
	 */
	private void processValidatorChildren(Validator.V2 v, IConfigurationElement group) {
		FilterGroup fg = FilterGroup.create(group.getName());
		if (fg == null)throw new IllegalStateException(ValMessages.ErrGroupName);			
		
		IConfigurationElement[] rules = group.getChildren(ExtensionConstants.rules);
		// there should only be one
		for (int i=0; i<rules.length; i++){
			IConfigurationElement[] r = rules[i].getChildren();
			for(int j=0; j<r.length; j++){
				processRule(fg, r[j]);
			}
		}
		v.add(fg);
	}

	/**
	 * Process a rule in one of the rule groups.
	 * 
	 * @param fg the filter group that we are building up 
	 * @param rule a rule in the group, like fileext.
	 */
	private void processRule(FilterGroup fg, IConfigurationElement rule) {
		FilterRule fr = FilterRule.create(rule.getName());
		if (fr == null)throw new IllegalStateException(ValMessages.ErrFilterRule);
		fr.setData(rule);
		fg.add(fr);
	}
	
	/**
	 * Determine if any of the validators need to be migrated, and if so answer a new
	 * Validator array.
	 * 
	 * @param validators the existing validators (from the preferences).
	 *  
	 * @return null if no validators needed to be migrated.
	 */
	private Validator[] migrate2(Validator[] validators) {
		int count = 0;
		Map<String, Validator> map = new HashMap<String, Validator>(validators.length);
		for (Validator v : validators)map.put(v.getId(), v);
		
		IExtensionRegistry registry = Platform.getExtensionRegistry();
		IExtensionPoint extensionPoint = registry.getExtensionPoint(ValidationPlugin.PLUGIN_ID, ExtensionConstants.validator);
		if (extensionPoint == null)return null;
				
		for (IExtension ext : extensionPoint.getExtensions()){
			for (IConfigurationElement validator : ext.getConfigurationElements()){
				Validator v = processValidator(validator, ext.getUniqueIdentifier(), ext.getLabel(), true);
				Validator old = map.get(v.getId());
				if (old == null || old.getVersion() < v.getVersion()){
					//TODO we may be replacing user preferences, at some point we may want to do a real migration.
					map.put(v.getId(), v);
					count++;
				}
			}
		}
		
		if (count > 0){
			Validator[] vals = new Validator[map.size()];
			map.values().toArray(vals);
			return vals;
		}
		return null;
	}
	
	private boolean getAttribute(IConfigurationElement element, String name, boolean dft){
		String v = element.getAttribute(name);
		if (v == null)return dft;
		if ("true".equalsIgnoreCase(v))return true; //$NON-NLS-1$
		if ("false".equalsIgnoreCase(v))return false; //$NON-NLS-1$
		return dft;
	}
	
	private int getAttribute(IConfigurationElement element, String name, int dft){
		String v = element.getAttribute(name);
		if (v == null)return dft;
		try {
			return Integer.parseInt(v);
		}
		catch (Exception e){
			// eat it.
		}
		return dft;
	}
}