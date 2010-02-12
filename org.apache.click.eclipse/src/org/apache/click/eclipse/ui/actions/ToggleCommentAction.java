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
package org.apache.click.eclipse.ui.actions;


import org.apache.click.eclipse.ClickPlugin;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.swt.SWT;
import org.eclipse.ui.texteditor.ITextEditor;

/**
 *
 * @author Naoki Takezoe
 */
public class ToggleCommentAction extends Action {

	private ITextEditor editor;

	public ToggleCommentAction(ITextEditor editor) {
		super(ClickPlugin.getString("action.toggleComment"));
		this.editor = editor;
		setId(ToggleCommentAction.class.getName());
		setActionDefinitionId(ToggleCommentAction.class.getName());
		setAccelerator(SWT.CTRL | '/');
	}

	public void run() {
		ITextSelection sel = (ITextSelection) editor.getSelectionProvider().getSelection();
		IDocument doc = editor.getDocumentProvider().getDocument(editor.getEditorInput());

		try {
			int offset = doc.getLineOffset(sel.getStartLine());
			String text = doc.get(offset, sel.getLength() + (sel.getOffset() - offset));

			if(text.startsWith("##")){
				text = text.replaceAll("(^|\r\n|\r|\n)##", "$1");
			} else {
				text = "##" + text.replaceAll("(\r\n|\r|\n)", "$1##");
				if(text.endsWith("##")){
					text = text.substring(0, text.length() - 2);
				}
			}

			doc.replace(offset, sel.getLength() + (sel.getOffset() - offset), text);

		} catch (BadLocationException e) {
			e.printStackTrace();
		}
	}

}
