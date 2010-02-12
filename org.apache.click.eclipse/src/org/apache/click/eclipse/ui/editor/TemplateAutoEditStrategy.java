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


import org.apache.click.eclipse.ClickPlugin;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.DocumentCommand;
import org.eclipse.jface.text.IAutoEditStrategy;
import org.eclipse.jface.text.IDocument;

/**
 * {@link IAutoEditStrategy} implementation for the {@link TemplateEditor}.
 * 
 * @see TemplateEditor
 * @see TemplateEditorConfiguration
 * 
 * @author Naoki Takezoe
 */
public class TemplateAutoEditStrategy implements IAutoEditStrategy {

	public void customizeDocumentCommand(IDocument document, DocumentCommand command) {
		try {
			// ${}
			if(command.text.equals("{") && command.offset > 1 
					&& document.get(command.offset - 1, 1).equals("$")){
				command.text = command.text + "}";
				command.caretOffset = command.offset + 1;
				command.shiftsCaret = false;
				command.doit = false;
			}
		} catch(BadLocationException ex){
			ClickPlugin.log(ex);
		}
	}

}
