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
package org.eclipse.wst.common.frameworks.datamodel.provisional;

import org.eclipse.wst.common.frameworks.internal.datamodel.DataModelFactoryImpl;

public interface DataModelFactory {

	public static final DataModelFactory INSTANCE = new DataModelFactoryImpl();

	public IDataModel createDataModel(String dataModelProviderID);

	public IDataModel createDataModel(Class dataModelProviderClass);

	public IDataModel createDataModel(IDataModelProvider dataModelProviderClass);

}