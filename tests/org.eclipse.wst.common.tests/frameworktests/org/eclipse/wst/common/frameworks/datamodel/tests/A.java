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
package org.eclipse.wst.common.frameworks.datamodel.tests;

import java.util.Set;

import org.eclipse.wst.common.frameworks.datamodel.AbstractDataModelProvider;

public class A extends AbstractDataModelProvider {
	public static final String P = "A.P";

	public Set getPropertyNames() {
		Set propertyNames = super.getPropertyNames();
		propertyNames.add(P);
		return propertyNames;
	}

	public String getID() {
		return null;
	}
}
