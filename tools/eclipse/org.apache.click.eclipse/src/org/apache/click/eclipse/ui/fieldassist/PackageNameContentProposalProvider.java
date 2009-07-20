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
package org.apache.click.eclipse.ui.fieldassist;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.internal.core.DefaultWorkingCopyOwner;
import org.eclipse.jdt.internal.ui.text.java.JavaCompletionProposal;
import org.eclipse.jdt.internal.ui.text.java.LazyJavaTypeCompletionProposal;
import org.eclipse.jdt.ui.text.java.CompletionProposalCollector;
import org.eclipse.jdt.ui.text.java.IJavaCompletionProposal;
import org.eclipse.jface.fieldassist.IContentProposal;
import org.eclipse.jface.fieldassist.IContentProposalProvider;

/**
 * 
 * @author Naoki Takezoe
 */
public class PackageNameContentProposalProvider implements IContentProposalProvider {
	
	private IJavaProject project;
	
	/**
	 * Constructor.
	 * 
	 * @param project the Java project
	 */
	public PackageNameContentProposalProvider(IJavaProject project){
		this.project = project;
	}
	
	public void setJavaProject(IJavaProject project){
		this.project = project;
	}
	
	public IContentProposal[] getProposals(String contents, int position) {
		if(this.project==null){
			return null;
		}
		try {
			CompletionProposalCollector collector = new CompletionProposalCollector(project);
			ICompilationUnit unit = FieldAssistUtils.getTemporaryCompilationUnit(project);
			contents = contents.substring(0, position);
			String source = "package " + contents;
			FieldAssistUtils.setContentsToCU(unit, source);
			unit.codeComplete(source.length(), collector, DefaultWorkingCopyOwner.PRIMARY);
			IJavaCompletionProposal[] proposals = collector.getJavaCompletionProposals();
			List<IContentProposal> result = new ArrayList<IContentProposal>();
			
			for(int j=0;j<proposals.length;j++){
				if(proposals[j].getImage()!=null){
					String replaceString = null;
					if(proposals[j] instanceof LazyJavaTypeCompletionProposal){
						LazyJavaTypeCompletionProposal p = (LazyJavaTypeCompletionProposal)proposals[j];
						replaceString = p.getReplacementString();
					} else if(proposals[j] instanceof JavaCompletionProposal){
						JavaCompletionProposal p = (JavaCompletionProposal)proposals[j];
						replaceString = p.getReplacementString();
					}
					if(replaceString!=null && replaceString.startsWith(contents)){
						result.add(new FieldAssistUtils.ContentProposalImpl(replaceString, position));
					}
				}
			}
			
			return result.toArray(new IContentProposal[result.size()]);
			
		} catch(Exception ex){
			ex.printStackTrace();
		}
		
		return null;
	}
	
}
