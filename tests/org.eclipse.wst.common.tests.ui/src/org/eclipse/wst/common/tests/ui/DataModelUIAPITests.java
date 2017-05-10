/*******************************************************************************
 * Copyright (c) 2003, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.common.tests.ui;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.eclipse.wst.common.tests.SimpleTestSuite;
import org.eclipse.wst.common.tests.ui.wizard.TestWizardTestcase;

/**
 * @author jsholl
 * 
 */
public class DataModelUIAPITests extends TestSuite {

	public static Test suite() {
		return new DataModelUIAPITests();
	}

	public DataModelUIAPITests() {
		super();
		addTest(new SimpleTestSuite(DataModelUIFactoryTest.class));
		addTest(new SimpleTestSuite(TestWizardTestcase.class));
	}
}
