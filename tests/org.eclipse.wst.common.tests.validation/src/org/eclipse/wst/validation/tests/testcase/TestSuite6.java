package org.eclipse.wst.validation.tests.testcase;

import java.io.UnsupportedEncodingException;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.wst.validation.internal.Tracing;
import org.eclipse.wst.validation.tests.T6A;

/** Test what happens when a dependent resource is deleted. */
public class TestSuite6 extends TestCase {
	
	private TestEnvironment _env;
	private IProject		_project;
	private IFile			_sample;
	private	IFile			_master;
	
	public static Test suite() {
		return new TestSuite(TestSuite6.class);
	} 
	
	public TestSuite6(String name){
		super(name);
	}
	

	protected void setUp() throws Exception {
		super.setUp();
		_env = new TestEnvironment();
		_env.turnoffAutoBuild();
		_project = _env.createProject("TestSuite6");
		
		IPath first = _env.addFolder(_project.getFullPath(), "first");
		_sample = _env.addFile(first, "sample.t6a",	"include master.t6a");		
		_master = _env.addFile(first, "master.t6a", "# a dummy file");
		
		TestEnvironment.enableOnlyThisValidator("org.eclipse.wst.validation.tests.T6A");
	}
	
	protected void tearDown() throws Exception {
		_project.delete(true, null);
		_env.dispose();
		super.tearDown();
	}
	
	/**
	 * Ensure that the validator gets called when a dependent resource is deleted.
	 */
	public void testDelete() throws CoreException, UnsupportedEncodingException, InterruptedException {
		Tracing.log("TestSuite6-01: testDelete starting");
		IProgressMonitor monitor = new NullProgressMonitor();	
		T6A.resetCounters();
		
		_env.incrementalBuildAndWait(monitor);
		assertEquals("We do not expect any errors by this point", 0, _env.getErrors(_sample));
		
		_master.delete(true, monitor);
		_env.incrementalBuildAndWait(monitor);
		assertEquals("The missing depenency should have been reported.", 1, _env.getErrors(_sample));
		assertEquals("The simple version of the validate method should never be called", 0, T6A.getCountSimple());
		assertTrue("The complex method must be called at least once", T6A.getCountComplex() > 0);
					
		Tracing.log("TestSuite6-02:testDelete finished");
	}
	


}
