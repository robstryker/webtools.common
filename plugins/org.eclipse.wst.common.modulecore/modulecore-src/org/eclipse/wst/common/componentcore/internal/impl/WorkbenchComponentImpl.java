/**
 * <copyright>
 * </copyright>
 *
 * $Id: WorkbenchComponentImpl.java,v 1.1 2005/04/04 07:04:59 cbridgha Exp $
 */
package org.eclipse.wst.common.componentcore.internal.impl;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.EObjectImpl;
import org.eclipse.emf.ecore.util.EObjectContainmentWithInverseEList;
import org.eclipse.emf.ecore.util.EObjectResolvingEList;
import org.eclipse.emf.ecore.util.InternalEList;
import org.eclipse.jem.util.emf.workbench.ProjectUtilities;
import org.eclipse.wst.common.componentcore.UnresolveableURIException;
import org.eclipse.wst.common.componentcore.internal.ComponentResource;
import org.eclipse.wst.common.componentcore.internal.ComponentType;
import org.eclipse.wst.common.componentcore.internal.ComponentcorePackage;
import org.eclipse.wst.common.componentcore.internal.ReferencedComponent;
import org.eclipse.wst.common.componentcore.internal.WorkbenchComponent;

/**
 * <!-- begin-user-doc --> An implementation of the model object '<em><b>Workbench Module</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.eclipse.wst.common.componentcore.internal.impl.WorkbenchComponentImpl#getName <em>Name</em>}</li>
 *   <li>{@link org.eclipse.wst.common.componentcore.internal.impl.WorkbenchComponentImpl#getResources <em>Resources</em>}</li>
 *   <li>{@link org.eclipse.wst.common.componentcore.internal.impl.WorkbenchComponentImpl#getComponentType <em>Component Type</em>}</li>
 *   <li>{@link org.eclipse.wst.common.componentcore.internal.impl.WorkbenchComponentImpl#getReferencedComponents <em>Referenced Components</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class WorkbenchComponentImpl extends EObjectImpl implements WorkbenchComponent {
	/**
	 * The default value of the '{@link #getName() <em>Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getName()
	 * @generated
	 * @ordered
	 */
	protected static final String NAME_EDEFAULT = "";

	/**
	 * The cached value of the '{@link #getName() <em>Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getName()
	 * @generated
	 * @ordered
	 */
	protected String name = NAME_EDEFAULT;

	/**
	 * The cached value of the '{@link #getResources() <em>Resources</em>}' containment reference list.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @see #getResources()
	 * @generated
	 * @ordered
	 */
	protected EList resources = null;

	/**
	 * The cached value of the '{@link #getComponentType() <em>Component Type</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getComponentType()
	 * @generated
	 * @ordered
	 */
	protected ComponentType componentType = null;

	/**
	 * The cached value of the '{@link #getReferencedComponents() <em>Referenced Components</em>}' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getReferencedComponents()
	 * @generated
	 * @ordered
	 */
	protected EList referencedComponents = null;

	private final Map resourceIndexByDeployPath = new HashMap();
	private final Map resourceIndexBySourcePath = new HashMap();

	private boolean isIndexedByDeployPath;

	private boolean isIndexedBySourcePath;

	private static final ComponentResource[] NO_MODULE_RESOURCES = new ComponentResource[0];

	private URI handle;

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	protected WorkbenchComponentImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	protected EClass eStaticClass() {
		return ComponentcorePackage.eINSTANCE.getWorkbenchComponent();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getName() {
		return name;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setNameGen(String newName) {
		String oldName = name;
		name = newName;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, ComponentcorePackage.WORKBENCH_COMPONENT__NAME, oldName, name));
	}

	public void setName(String newDeployedName) {
		setNameGen(newDeployedName);
		// TODO A more advanced adapter should be applied to keep the handle up to date.
		if (eResource() != null) {
			URI resourceURI = eResource().getURI();
			String safeDeployedName = getName() != null ? getName() : ""; //$NON-NLS-1$
			if (resourceURI != null && resourceURI.segmentCount() >= 2)
				setHandle(computeHandle());
		}
	}
	
	protected void setHandle(URI aHandle) {
		handle = aHandle;
	}
	
	public URI getHandle() {
		if(handle == null)
			handle = computeHandle();
		return handle;
	}

	private URI computeHandle() {
		return URI.createURI(PlatformURLModuleConnection.MODULE_PROTOCOL + IPath.SEPARATOR + PlatformURLModuleConnection.RESOURCE_MODULE + IPath.SEPARATOR + computeProjectName() + IPath.SEPARATOR + getName());
		
	}

	private String computeProjectName() {
		IProject project = ProjectUtilities.getProject(this);
		return (project!=null)?project.getName():"UNCONTAINED"; //$NON-NLS-1$
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public EList getResources() {
		if (resources == null) {
			resources = new EObjectContainmentWithInverseEList(ComponentResource.class, this, ComponentcorePackage.WORKBENCH_COMPONENT__RESOURCES, ComponentcorePackage.COMPONENT_RESOURCE__COMPONENT);
		}
		return resources;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public ComponentType getComponentType() {
		if (componentType != null && componentType.eIsProxy()) {
			ComponentType oldComponentType = componentType;
			componentType = (ComponentType)eResolveProxy((InternalEObject)componentType);
			if (componentType != oldComponentType) {
				if (eNotificationRequired())
					eNotify(new ENotificationImpl(this, Notification.RESOLVE, ComponentcorePackage.WORKBENCH_COMPONENT__COMPONENT_TYPE, oldComponentType, componentType));
			}
		}
		return componentType;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public ComponentType basicGetComponentType() {
		return componentType;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setComponentType(ComponentType newComponentType) {
		ComponentType oldComponentType = componentType;
		componentType = newComponentType;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, ComponentcorePackage.WORKBENCH_COMPONENT__COMPONENT_TYPE, oldComponentType, componentType));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList getReferencedComponents() {
		if (referencedComponents == null) {
			referencedComponents = new EObjectResolvingEList(ReferencedComponent.class, this, ComponentcorePackage.WORKBENCH_COMPONENT__REFERENCED_COMPONENTS);
		}
		return referencedComponents;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain eInverseAdd(InternalEObject otherEnd, int featureID, Class baseClass, NotificationChain msgs) {
		if (featureID >= 0) {
			switch (eDerivedStructuralFeatureID(featureID, baseClass)) {
				case ComponentcorePackage.WORKBENCH_COMPONENT__RESOURCES:
					return ((InternalEList)getResources()).basicAdd(otherEnd, msgs);
				default:
					return eDynamicInverseAdd(otherEnd, featureID, baseClass, msgs);
			}
		}
		if (eContainer != null)
			msgs = eBasicRemoveFromContainer(msgs);
		return eBasicSetContainer(otherEnd, featureID, msgs);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, Class baseClass, NotificationChain msgs) {
		if (featureID >= 0) {
			switch (eDerivedStructuralFeatureID(featureID, baseClass)) {
				case ComponentcorePackage.WORKBENCH_COMPONENT__RESOURCES:
					return ((InternalEList)getResources()).basicRemove(otherEnd, msgs);
				default:
					return eDynamicInverseRemove(otherEnd, featureID, baseClass, msgs);
			}
		}
		return eBasicSetContainer(null, featureID, msgs);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public Object eGet(EStructuralFeature eFeature, boolean resolve) {
		switch (eDerivedStructuralFeatureID(eFeature)) {
			case ComponentcorePackage.WORKBENCH_COMPONENT__NAME:
				return getName();
			case ComponentcorePackage.WORKBENCH_COMPONENT__RESOURCES:
				return getResources();
			case ComponentcorePackage.WORKBENCH_COMPONENT__COMPONENT_TYPE:
				if (resolve) return getComponentType();
				return basicGetComponentType();
			case ComponentcorePackage.WORKBENCH_COMPONENT__REFERENCED_COMPONENTS:
				return getReferencedComponents();
		}
		return eDynamicGet(eFeature, resolve);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public void eSet(EStructuralFeature eFeature, Object newValue) {
		switch (eDerivedStructuralFeatureID(eFeature)) {
			case ComponentcorePackage.WORKBENCH_COMPONENT__NAME:
				setName((String)newValue);
				return;
			case ComponentcorePackage.WORKBENCH_COMPONENT__RESOURCES:
				getResources().clear();
				getResources().addAll((Collection)newValue);
				return;
			case ComponentcorePackage.WORKBENCH_COMPONENT__COMPONENT_TYPE:
				setComponentType((ComponentType)newValue);
				return;
			case ComponentcorePackage.WORKBENCH_COMPONENT__REFERENCED_COMPONENTS:
				getReferencedComponents().clear();
				getReferencedComponents().addAll((Collection)newValue);
				return;
		}
		eDynamicSet(eFeature, newValue);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public void eUnset(EStructuralFeature eFeature) {
		switch (eDerivedStructuralFeatureID(eFeature)) {
			case ComponentcorePackage.WORKBENCH_COMPONENT__NAME:
				setName(NAME_EDEFAULT);
				return;
			case ComponentcorePackage.WORKBENCH_COMPONENT__RESOURCES:
				getResources().clear();
				return;
			case ComponentcorePackage.WORKBENCH_COMPONENT__COMPONENT_TYPE:
				setComponentType((ComponentType)null);
				return;
			case ComponentcorePackage.WORKBENCH_COMPONENT__REFERENCED_COMPONENTS:
				getReferencedComponents().clear();
				return;
		}
		eDynamicUnset(eFeature);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public boolean eIsSet(EStructuralFeature eFeature) {
		switch (eDerivedStructuralFeatureID(eFeature)) {
			case ComponentcorePackage.WORKBENCH_COMPONENT__NAME:
				return NAME_EDEFAULT == null ? name != null : !NAME_EDEFAULT.equals(name);
			case ComponentcorePackage.WORKBENCH_COMPONENT__RESOURCES:
				return resources != null && !resources.isEmpty();
			case ComponentcorePackage.WORKBENCH_COMPONENT__COMPONENT_TYPE:
				return componentType != null;
			case ComponentcorePackage.WORKBENCH_COMPONENT__REFERENCED_COMPONENTS:
				return referencedComponents != null && !referencedComponents.isEmpty();
		}
		return eDynamicIsSet(eFeature);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public String toString() {
		if (eIsProxy()) return super.toString();

		StringBuffer result = new StringBuffer(super.toString());
		result.append(" (name: ");
		result.append(name);
		result.append(')');
		return result.toString();
	}

	public ComponentResource[] findWorkbenchModuleResourceByDeployPath(URI aDeployPath) {
		// if (!isIndexedByDeployPath)
		// indexResourcesByDeployPath();
		// return (ComponentResource) resourceIndexByDeployPath.get(aDeployPath);
		IPath resourcePath = new Path(aDeployPath.path());
		ResourceTreeRoot resourceTreeRoot = ResourceTreeRoot.getDeployResourceTreeRoot(this);
		return resourceTreeRoot.findModuleResources(resourcePath, false); 
	}

	public ComponentResource[] findWorkbenchModuleResourceBySourcePath(URI aSourcePath) {
		// if(!isIndexedBySourcePath)
		// indexResourcesBySourcePath();
		try {
			if (ModuleURIUtil.ensureValidFullyQualifiedPlatformURI(aSourcePath, false)) {
				IPath resourcePath = new Path(aSourcePath.path()).removeFirstSegments(1);
				ResourceTreeRoot resourceTreeRoot = ResourceTreeRoot.getSourceResourceTreeRoot(this);
				return resourceTreeRoot.findModuleResources(resourcePath, false);
			}
		} catch (UnresolveableURIException e) {
			e.printStackTrace();
		}
		return NO_MODULE_RESOURCES;
	}
  

} // WorkbenchComponentImpl