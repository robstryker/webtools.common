package org.eclipse.wst.validation.tests.testcase;

import java.io.ByteArrayInputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Iterator;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceDescription;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.wst.validation.IMutableValidator;
import org.eclipse.wst.validation.MutableWorkspaceSettings;
import org.eclipse.wst.validation.ValidationFramework;
import org.eclipse.wst.validation.internal.ValConstants;
import org.eclipse.wst.validation.internal.operations.ValidatorManager;

public class TestEnvironment {
	
	public static final boolean DEBUG = true;
	
	private IWorkspace	_workspace;
	private HashMap<String, IProject> _projects = new HashMap<String, IProject>(20);
	
	public TestEnvironment() throws CoreException {
		_workspace = ResourcesPlugin.getWorkspace();
		if (DEBUG){
			_workspace.getRoot().delete(true, true, null);
		}
	}
	
	public IPath addFolder(IPath root, String folderName) throws CoreException {
		IPath path = root.append(folderName);
		createFolder(path);
		return path;
	}
	
	public void incrementalBuild() throws CoreException{
		getWorkspace().build(IncrementalProjectBuilder.INCREMENTAL_BUILD, null);
	}
	
	/**
	 * Run an incremental build and wait for it to finish.
	 * @param monitor
	 * @throws CoreException
	 */
	public void incrementalBuildAndWait(IProgressMonitor monitor) throws CoreException, InterruptedException {
		getWorkspace().build(IncrementalProjectBuilder.INCREMENTAL_BUILD, monitor);
		Thread.sleep(2000);
		ValidationFramework.getDefault().join(monitor);
	}
	
	/**
	 * Start a full build.
	 */
	public void fullBuild2(IProgressMonitor monitor) throws CoreException{
		getWorkspace().build(IncrementalProjectBuilder.FULL_BUILD, monitor);
	}
	
	/**
	 * Do a full build, and wait until all the validation has finished.
	 * @param monitor
	 */
	public void fullBuild(IProgressMonitor monitor) throws CoreException, InterruptedException {
		fullBuild2(monitor);
		Thread.sleep(1000);
		ValidationFramework.getDefault().join(monitor);
		Thread.sleep(2000);  // we need to sleep here to give the "finished" job a chance to run.		
	}
	
	/**
	 * Do a clean build, and wait until all the validation has finished.
	 * @param monitor
	 */
	public void cleanBuild(IProgressMonitor monitor) throws CoreException, InterruptedException {
		getWorkspace().build(IncrementalProjectBuilder.CLEAN_BUILD, monitor);
		Thread.sleep(1000);
		ValidationFramework.getDefault().join(monitor);
	}
	
	private IFolder createFolder(IPath path) throws CoreException {
		if (path.segmentCount() <= 1)return null;
		
		IFolder folder = _workspace.getRoot().getFolder(path);
		if (!folder.exists()){
			folder.create(true, true, null);
		}
		return folder;
	}

	public IProject createProject(String name) throws CoreException {
		final IProject project = _workspace.getRoot().getProject(name);
		IWorkspaceRunnable create = new IWorkspaceRunnable() {

			public void run(IProgressMonitor monitor) throws CoreException {
				project.create(monitor);
				project.open(monitor);	
				ValidatorManager.addProjectBuildValidationSupport(project);
			}		
		};
		
		_workspace.run(create, null);
		_projects.put(name, project);
		
		return project;
	}
	
	public void dispose() throws CoreException {
		if (DEBUG)return;
		for (Iterator<IProject> it=_projects.values().iterator(); it.hasNext();){
			IProject project = it.next();
			project.delete(true, null);
		}
	}

	public IFile addFile(IPath folder, String fileName, String contents) throws CoreException, UnsupportedEncodingException {
		IPath filePath = folder.append(fileName);
		return createFile(filePath, contents.getBytes("UTF8"));
	}

	private IFile createFile(IPath filePath, byte[] contents) throws CoreException {
		IFile file = _workspace.getRoot().getFile(filePath);
		ByteArrayInputStream in = new ByteArrayInputStream(contents);
		if (file.exists())file.setContents(in, true, false, null);
		else file.create(in, true, null);
		return file;
	}
	
	public IWorkspace getWorkspace(){
		return _workspace;
	}

	public IProject findProject(String name) {
		IProject project = _workspace.getRoot().getProject(name);
		if (project.exists())return project;
		return null;
	}
	
	/**
	 * Since other plug-ins can add and remove validators, turn off all the ones that are not part of
	 * these tests.
	 * 
	 * @param validatorPrefix The start of the validator class name, but without the package name. For example "T5".
	 */
	public static void enableOnlyTheseValidators(String validatorPrefix) throws InvocationTargetException {
		ValidationFramework vf = ValidationFramework.getDefault();
		String name = "org.eclipse.wst.validation.tests." + validatorPrefix;
		MutableWorkspaceSettings ws = vf.getWorkspaceSettings();
		for (IMutableValidator v : ws.getValidators()){
			boolean enable = v.getValidatorClassname().startsWith(name);
			v.setBuildValidation(enable);
			v.setManualValidation(enable);
		}
		vf.applyChanges(ws, true);
	}

	/**
	 * Since other plug-ins can add and remove validators, turn off all the validators except this one.
	 * 
	 * @param name Fully qualified class name of the validator to turn on. For example
	 * org.eclipse.wst.validation.tests.TestValidator
	 */
	public static void enableOnlyThisValidator(String name) throws InvocationTargetException {
		ValidationFramework vf = ValidationFramework.getDefault();
		MutableWorkspaceSettings ws = vf.getWorkspaceSettings();
		for (IMutableValidator v : ws.getValidators()){
			boolean enable = v.getValidatorClassname().equals(name);
			v.setBuildValidation(enable);
			v.setManualValidation(enable);
		}
		vf.applyChanges(ws, true);
	}
	
	public void turnoffAutoBuild() throws CoreException {
		IWorkspaceDescription wd = _workspace.getDescription();
		if (wd.isAutoBuilding()){
			wd.setAutoBuilding(false);
			_workspace.setDescription(wd);
		}
		
	}
	
	public void turnOnAutoBuild() throws CoreException {
		IWorkspaceDescription wd = _workspace.getDescription();
		if (!wd.isAutoBuilding()){
			wd.setAutoBuilding(true);
			_workspace.setDescription(wd);
		}		
	}

	/**
	 * Answer the number of validation errors on this resource.
	 * 
	 * @param resource
	 *            The resource being checked.
	 * @return the number of problem markers that have the error severity.
	 */
	public int getErrors(IResource resource) {
		int errors = 0;
		try {
			IMarker[] markers = resource.findMarkers(ValConstants.ProblemMarker, false, IResource.DEPTH_ZERO);
			for (IMarker marker : markers){
				int severity = marker.getAttribute(IMarker.SEVERITY, -1);
				if (severity == IMarker.SEVERITY_ERROR)errors++;
			}
		}
		catch (CoreException e){
			//eat it
		}
		return errors;
	}
}
