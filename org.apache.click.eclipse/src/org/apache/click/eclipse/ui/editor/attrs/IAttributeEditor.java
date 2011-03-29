/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.click.eclipse.ui.editor.attrs;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMElement;

/**
 * The interface of element editors in the {@link net.sf.clickide.ui.editor.ClickTreeEditor}.
 * <p>
 * Subclasses would provide the form editor to edit the element attributes.
 * 
 * @author Naoki Takezoe
 * @see net.sf.clickide.ui.editor.ClickTreeEditor
 */
public interface IAttributeEditor {
	
	/**
	 * Creates the form for the element editing.
	 * 
	 * @param toolkit FormToolkit
	 * @param parent the parent composite
	 * @param element the target element
	 * @return the form composite
	 */
	public Composite createForm(FormToolkit toolkit, Composite parent, IDOMElement element);
	
}
