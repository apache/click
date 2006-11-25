package net.sf.clickide.ui.editor.attrs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.IBuffer;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.core.DefaultWorkingCopyOwner;
import org.eclipse.jdt.internal.ui.text.java.JavaCompletionProposal;
import org.eclipse.jdt.internal.ui.text.java.LazyJavaTypeCompletionProposal;
import org.eclipse.jdt.ui.text.java.CompletionProposalCollector;
import org.eclipse.jdt.ui.text.java.IJavaCompletionProposal;
import org.eclipse.jface.fieldassist.IContentProposal;
import org.eclipse.jface.fieldassist.IContentProposalProvider;

/**
 * Provides type name completion in the Java project.
 * 
 * @author Naoki Takezoe
 */
public class TypeNameContentProposalProvider implements IContentProposalProvider {
	
	private static HashMap unitMap = new HashMap();
	
	private IJavaProject project;
	
	/**
	 * Constructor.
	 * 
	 * @param project the Java project
	 */
	public TypeNameContentProposalProvider(IJavaProject project){
		this.project = project;
	}
	
	public IContentProposal[] getProposals(String contents, int position) {
		try {
			CompletionProposalCollector collector = new CompletionProposalCollector(project);
			ICompilationUnit unit = getTemporaryCompilationUnit(project);
			contents = contents.substring(0, position);
			String source = "public class _xxx { public static void hoge(){ " + contents + "}}";
			setContentsToCU(unit, source);
			unit.codeComplete(source.length() - 2, collector, DefaultWorkingCopyOwner.PRIMARY);
			IJavaCompletionProposal[] proposals = collector.getJavaCompletionProposals();
			List result = new ArrayList();
			
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
						result.add(new ContentProposalImpl(replaceString, position));
					}
				}
			}
			
			return (IContentProposal[])result.toArray(new IContentProposal[result.size()]);
			
		} catch(Exception ex){
			ex.printStackTrace();
		}
		
		return null;
	}
	
	private class ContentProposalImpl implements IContentProposal {
		
		private String content;
		private int position;
		
		public ContentProposalImpl(String content, int position){
			this.content = content;
			this.position = position;
		}
		
		public String getContent() {
			return content.substring(position);
		}

		public int getCursorPosition() {
			return content.length() - position;
		}

		public String getDescription() {
			return null;
		}

		public String getLabel() {
			return content;
		}
	}
	
	/**
	 * Set contents of the compilation unit to the translated jsp text.
	 *
	 * @param unit the ICompilationUnit on which to set the buffer contents
	 * @param value Java source code
	 */	
	private static void setContentsToCU(ICompilationUnit unit, String value){
		if (unit == null)
			return;

		synchronized (unit) {
			IBuffer buffer;
			try {

				buffer = unit.getBuffer();
			}
			catch (JavaModelException e) {
				e.printStackTrace();
				buffer = null;
			}

			if (buffer != null)
				buffer.setContents(value);
		}
	}
	
	/**
	 * Creates the <code>ICompilationUnit</code> to use temporary.
	 * 
	 * @param project the java project
	 * @return the temporary <code>ICompilationUnit</code>
	 * @throws JavaModelException
	 */
	public synchronized static ICompilationUnit getTemporaryCompilationUnit(
			IJavaProject project) throws JavaModelException {
		
		if(unitMap.get(project) != null){
			return (ICompilationUnit)unitMap.get(project);
		}
		
		IPackageFragment root = project.getPackageFragments()[0];
		ICompilationUnit unit = root.getCompilationUnit("_xxx.java").getWorkingCopy(
				new NullProgressMonitor());
		
		unitMap.put(project, unit);
		
		return unit;
	}

}
