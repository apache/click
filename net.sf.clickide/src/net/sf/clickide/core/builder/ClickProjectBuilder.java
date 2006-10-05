package net.sf.clickide.core.builder;

import java.util.Map;

import net.sf.clickide.ClickUtils;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;

/**
 * The incremental builder for the Click project.
 * 
 * @author Naoki Takezoe
 * @see ClickProjectNature
 */
public class ClickProjectBuilder extends IncrementalProjectBuilder {

	/** The id of this builder. */
	public static final String BUILDER_ID = "net.sf.clickide.ClickProjectBuilder";
	
	private ClickXMLValidator validator = new ClickXMLValidator();

	protected IProject[] build(int kind, Map args, IProgressMonitor monitor) throws CoreException {
		if (kind == FULL_BUILD) {
			fullBuild(monitor);
		} else {
			IResourceDelta delta = getDelta(getProject());
			if (delta == null) {
				fullBuild(monitor);
			} else {
				incrementalBuild(delta, monitor);
			}
		}
		return null;
	}
	
	protected void fullBuild(final IProgressMonitor monitor) throws CoreException {
		IFile file = ClickUtils.getClickConfigFile(getProject());
		validator.validate(file, monitor);
	}

	protected void incrementalBuild(IResourceDelta delta, 
			IProgressMonitor monitor) throws CoreException {
		delta.accept(new ClickDeltaVisitor(monitor));
	}
	
	private class ClickDeltaVisitor implements IResourceDeltaVisitor {
		
		private IProgressMonitor monitor;
		
		public ClickDeltaVisitor(IProgressMonitor monitor){
			this.monitor = monitor;
		}
		
		public boolean visit(IResourceDelta delta) throws CoreException {
			IResource resource = delta.getResource();
			if(resource instanceof IFile){
				if(resource.getName().endsWith(".htm")){
					fullBuild(monitor);
					return false;
				}
				if(resource.getName().endsWith(".java")){
					fullBuild(monitor);
					return false;
				}
				if(resource.getName().endsWith(".class")){
					fullBuild(monitor);
					return false;
				}
				if(resource.getName().endsWith(".jar")){
					fullBuild(monitor);
					return false;
				}
				if(resource.getName().equals("click.xml")){
					fullBuild(monitor);
					return false;
				}
			}
			return true;
		}
	}


}
