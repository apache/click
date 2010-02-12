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
package org.apache.click.eclipse.ui.editor;

import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IEditorActionBarContributor;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.part.MultiPageEditorActionBarContributor;
import org.eclipse.wst.sse.ui.StructuredTextEditor;
import org.eclipse.wst.sse.ui.internal.ISourceViewerActionBarContributor;
import org.eclipse.wst.xml.ui.internal.tabletree.SourcePageActionContributor;

/**
 * 
 * @author Naoki Takezoe
 */
public class ClickActionBarContributor extends MultiPageEditorActionBarContributor {
	
	private IEditorActionBarContributor sourceViewerActionContributor = null;
	private ClickEditor clickEditor = null;
	
	public ClickActionBarContributor(){
		super();
		sourceViewerActionContributor = new SourcePageActionContributor();
	}
	
	public void init(IActionBars actionBars) {
		super.init(actionBars);
		if (actionBars != null) {
			initSourceViewerActionContributor(actionBars);
		}
	}
	
	private void initSourceViewerActionContributor(IActionBars actionBars) {
		if (sourceViewerActionContributor != null){
			sourceViewerActionContributor.init(actionBars, getPage());
		}
	}
	
	public void dispose() {
		super.dispose();

		if (sourceViewerActionContributor != null){
			sourceViewerActionContributor.dispose();
		}
	}
	
	public void setActiveEditor(IEditorPart targetEditor) {
		if (targetEditor instanceof ClickEditor){
			clickEditor = (ClickEditor) targetEditor;
		}
		super.setActiveEditor(targetEditor);
	}
	
	public void setActivePage(IEditorPart activeEditor) {
		// This contributor is designed for StructuredTextMultiPageEditorPart.
		// To safe-guard this from problems caused by unexpected usage by
		// other editors, the following
		// check is added.
		if (clickEditor != null) {
			if (activeEditor != null && activeEditor instanceof StructuredTextEditor){
				activateSourcePage(activeEditor);
			} else {
				activateDesignPage(activeEditor);
			}
		}
		IActionBars actionBars = getActionBars();
		if (actionBars != null) {
			// update menu bar and tool bar
			actionBars.updateActionBars();
		}
	}
	
	private void activateDesignPage(IEditorPart activeEditor) {
		if (sourceViewerActionContributor != null && sourceViewerActionContributor instanceof ISourceViewerActionBarContributor) {
			// if design page is not really an IEditorPart, activeEditor ==
			// null, so pass in multiPageEditor instead (d282414)
			if (activeEditor == null) {
				sourceViewerActionContributor.setActiveEditor(clickEditor);
			} else {
				sourceViewerActionContributor.setActiveEditor(activeEditor);
			}
			((ISourceViewerActionBarContributor) sourceViewerActionContributor).setViewerSpecificContributionsEnabled(false);
		}
	}

	private void activateSourcePage(IEditorPart activeEditor) {
		if (sourceViewerActionContributor != null && sourceViewerActionContributor instanceof ISourceViewerActionBarContributor) {
			sourceViewerActionContributor.setActiveEditor(activeEditor);
			((ISourceViewerActionBarContributor) sourceViewerActionContributor).setViewerSpecificContributionsEnabled(true);
		}
	}

}
