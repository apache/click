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

import org.eclipse.jface.text.hyperlink.IHyperlinkDetector;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.wst.xml.ui.StructuredTextViewerConfigurationXML;

/**
 * Extended StructuredTextViewerConfigurationXML
 * which provides the hyperlink support for click.xml.
 * 
 * @author Naoki Takezoe
 * @see org.apache.click.eclipse.ui.editor.ClickXMLHyperlinkDetector
 */
public class ClickXMLTextViewerConfiguration extends StructuredTextViewerConfigurationXML {
	
	public IHyperlinkDetector[] getHyperlinkDetectors(ISourceViewer sourceViewer) {
		List<IHyperlinkDetector> result = new ArrayList<IHyperlinkDetector>(0);
		result.add(new ClickXMLHyperlinkDetector());
		
		IHyperlinkDetector[] superDetectors = super.getHyperlinkDetectors(sourceViewer);
		for (int m = 0; m < superDetectors.length; m++) {
			IHyperlinkDetector detector = superDetectors[m];
			if (!result.contains(detector)) {
				result.add(detector);
			}
		}
		
		return result.toArray(new IHyperlinkDetector[0]);
	}
	
}
