package org.apache.click.eclipse.core.config;

import org.eclipse.core.resources.IProject;
import org.eclipse.jdt.core.IType;

/**
 * Provides framework configurations.
 * 
 * @author Naoki Takezoe
 */
public interface IClickConfigurationProvider {
	
	/**
	 * Returns the charset.
	 * 
	 * @param project the project
	 * @return the charset
	 */
	public String getCharset(IProject project);
	
	/**
	 * Returns the format class.
	 * 
	 * @param project the project
	 * @return the format class
	 */
	public IType getFormat(IProject project);
	
	public boolean getAutoMapping(IProject project);
	
	/**
	 * Returns the root package name which contains page classes.
	 * 
	 * @param project the project
	 * @return the root package name of page classes
	 */
	public String getPagePackageName(IProject project);
	
	/**
	 * Returns the HTML template path from the page classname.
	 * 
	 * @param project the project
	 * @param className the page classname
	 * @return the HTML template path
	 */
	public String getHTMLfromClass(IProject project, String className);
	
	/**
	 * Returns the page classname from the HTML template path.
	 * 
	 * @param project the project
	 * @param htmlName the HTML template path
	 * @return the page classname
	 */
	public String getClassfromHTML(IProject project, String htmlName);
	
	public boolean isSupportedProject(IProject project);
	
}
