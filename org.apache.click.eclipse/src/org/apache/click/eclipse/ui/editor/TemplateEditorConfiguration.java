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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.text.IAutoEditStrategy;
import org.eclipse.jface.text.contentassist.IContentAssistProcessor;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.SourceViewerConfiguration;
import org.eclipse.wst.html.core.text.IHTMLPartitions;
import org.eclipse.wst.html.ui.StructuredTextViewerConfigurationHTML;
import org.eclipse.wst.sse.ui.internal.provisional.style.LineStyleProvider;

/**
 * {@link SourceViewerConfiguration} for the Velocity Template Editor.
 * Provides the extended code completion for Velocity.
 * 
 * @author Naoki Takezoe
 * @see TemplateContentAssistProcessor
 */
public class TemplateEditorConfiguration extends StructuredTextViewerConfigurationHTML {
	
	private TemplateContentAssistProcessor processor = null;
	
	/**
	 * @see TemplateContentAssistProcessor
	 */
	protected IContentAssistProcessor[] getContentAssistProcessors(ISourceViewer viewer, String partitionType) {
		if(this.processor == null){
			this.processor = new TemplateContentAssistProcessor();
		}
		return new IContentAssistProcessor[]{this.processor};
	}

	/**
	 * @see TemplateAutoEditStrategy
	 */
	public IAutoEditStrategy[] getAutoEditStrategies(ISourceViewer sourceViewer, String contentType) {
		List<IAutoEditStrategy> allStrategies = new ArrayList<IAutoEditStrategy>(0);
		
		IAutoEditStrategy[] superStrategies = super.getAutoEditStrategies(sourceViewer, contentType);
		for (int i = 0; i < superStrategies.length; i++) {
			allStrategies.add(superStrategies[i]);
		}
		
		allStrategies.add(new TemplateAutoEditStrategy());

		return allStrategies.toArray(new IAutoEditStrategy[allStrategies.size()]);
	}
	
	/**
	 * @see LineStyleProviderForVelocity
	 */
	public LineStyleProvider[] getLineStyleProviders(ISourceViewer sourceViewer, String partitionType) {
		LineStyleProvider[] providers = null;

		if (partitionType == IHTMLPartitions.HTML_DEFAULT || partitionType == IHTMLPartitions.HTML_COMMENT || partitionType == IHTMLPartitions.HTML_DECLARATION) {
			providers = new LineStyleProvider[]{new LineStyleProviderForVelocity()};
		} else {
			providers = super.getLineStyleProviders(sourceViewer, partitionType);
		}
//		else if (partitionType == IHTMLPartitions.SCRIPT) {
//			providers = new LineStyleProvider[]{getLineStyleProviderForJavascript()};
//		}
//		else if (partitionType == ICSSPartitions.STYLE) {
//			providers = new LineStyleProvider[]{getLineStyleProviderForEmbeddedCSS()};
//		}

		return providers;
	}
	
	
	
}
