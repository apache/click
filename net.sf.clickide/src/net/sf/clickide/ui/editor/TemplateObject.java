package net.sf.clickide.ui.editor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import net.sf.clickide.ClickUtils;

import org.eclipse.jdt.core.Flags;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.ITypeHierarchy;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.Signature;

/**
 * Provides accessors which are easy to use for JDT class model.
 * This class would be used by the code completion in the Velocity editor.
 * 
 * @author Naoki Takezoe
 * @see TemplateContentAssistProcessor
 */
public class TemplateObject {
	
	private IType type;
	private String primitiveType;
	
	/**
	 * The constructor.
	 * 
	 * @param type the <code>IType</code> object
	 */
	public TemplateObject(IType type){
		this.type = type;
	}
	
	/**
	 * The constructor for primitive types.
	 * 
	 * @param primitiveType the primitive type name
	 */
	public TemplateObject(String primitiveType){
		this.primitiveType = primitiveType;
	}
	
	/**
	 * Returns the specified method.
	 * If this object doesn't have the specified method, returns <code>null</code>.
	 * 
	 * @param name the method name
	 * @return the specified method or <code>null</code>
	 */
	public TemplateObjectMethod getMethod(String name){
		if(this.type!=null){
			TemplateObjectMethod[] methods = getMethods();
			for(int i=0;i<methods.length;i++){
				if(methods[i].getName().equals(name)){
					return methods[i];
				}
			}
		}
		return null;
	}
	
	/**
	 * Returns the specified property.
	 * If this object doesn't have the specified property, returns <code>null</code>.
	 * 
	 * @param name the property name
	 * @return the specified property or <code>null</code>
	 */
	public TemplateObjectProperty getProperty(String name){
		if(this.type!=null){
			TemplateObjectProperty[] properties = getProperties();
			for(int i=0;i<properties.length;i++){
				if(properties[i].getName().equals(name)){
					return properties[i];
				}
			}
		}
		return null;
	}
	
	/**
	 * Returns available methods in this object.
	 * 
	 * @return the array of methods
	 */
	public TemplateObjectMethod[] getMethods(){
		if(this.type!=null){
			try {
				IMethod[] methods = getAllMethods(type);
				List result = new ArrayList();
				for(int i=0;i<methods.length;i++){
					if(Flags.isPublic(methods[i].getFlags()) && !methods[i].isConstructor()){
						result.add(new TemplateObjectMethod(methods[i]));
					}
				}
				return (TemplateObjectMethod[])result.toArray(new TemplateObjectMethod[result.size()]);
			} catch (JavaModelException e) {
			}
		}
		return new TemplateObjectMethod[0];
	}
	
	/**
	 * Returns available fields in this object.
	 * 
	 * @return the array of properties
	 */
	public TemplateObjectProperty[] getProperties(){
		if(this.type!=null){
			try {
				IMethod[] methods = getAllMethods(type);
				List result = new ArrayList();
				for(int i=0;i<methods.length;i++){
					if(Flags.isPublic(methods[i].getFlags()) && methods[i].getParameterTypes().length==0){
						String name = methods[i].getElementName();
						if((name.startsWith("get") && name.length() > 3) || 
								(name.startsWith("is") && name.length() > 2)){
							result.add(new TemplateObjectProperty(methods[i]));
						}
					}
				}
				return (TemplateObjectProperty[])result.toArray(new TemplateObjectProperty[result.size()]);
			} catch (JavaModelException e) {
			}
		}
		return new TemplateObjectProperty[0];
	}
	
	/**
	 * Returns available methods and fields in this object.
	 * 
	 * @return the array of both methods and properties
	 */
	public TemplateObjectElement[] getChildren(){
		List result = new ArrayList();
		if(this.type!=null){
			TemplateObjectMethod[] methods = getMethods();
			TemplateObjectProperty[] properties = getProperties();
			for(int i=0;i<methods.length;i++){
				result.add(methods[i]);
			}
			for(int i=0;i<properties.length;i++){
				result.add(properties[i]);
			}
			Collections.sort(result, new Comparator(){
				public int compare(Object arg0, Object arg1) {
					return arg0.toString().compareTo(arg1.toString());
				}
			});
		}
		return (TemplateObjectElement[])result.toArray(new TemplateObjectElement[result.size()]);
	}
	
	/**
	 * Tests whether this object is the primitive type.
	 * 
	 * @return <code>true</code> if this is the primitive type; 
	 *   <code>false</code> otherwise
	 */
	public boolean isPrimitive(){
		return this.type == null;
	}
	
	/**
	 * Returns the <code>IType</code> instance of this object.
	 * 
	 * @return the <code>IType</code> instance.
	 *   If this object is the primitive type, returns null.
	 */
	public IType getType(){
		return this.type;
	}
	
	/**
	 * Returns the type name of this object.
	 * 
	 * @return the full qualified class name or the primitive type name
	 */
	public String getTypeName(){
		if(this.type!=null){
			return this.type.getFullyQualifiedName();
		} else {
			return this.primitiveType;
		}
	}
	
	///////////////////////////////////////////////////////////////////////////////////////
	//
	// Inner Classes
	//
	///////////////////////////////////////////////////////////////////////////////////////
	
	public static class TemplateObjectProperty implements TemplateObjectElement {
		
		private IMethod method;
		
		public TemplateObjectProperty(IMethod method){
			this.method = method;
		}
		
		public String getName(){
			String name = this.method.getElementName();
			if(name.startsWith("get")){
				name = name.substring(3);
			} else if(name.startsWith("is")){
				name = name.substring(2);
			}
			return name.substring(0, 1).toLowerCase() + name.substring(1);
		}
		
		public String getDisplayName(){
			StringBuffer sb = new StringBuffer();
			sb.append(getName());
			sb.append(" ");
			try {
				sb.append(getSimpleName(Signature.toString(this.method.getReturnType())));
			} catch(Exception ex){
			}
			sb.append(" - ");
			sb.append(this.method.getDeclaringType().getElementName());
			return sb.toString();
		}
		
		public TemplateObject toTemplateObject(){
			try {
				String className = ClickUtils.removeTypeParameter(Signature.toString(this.method.getReturnType()));
				if(ClickUtils.isPrimitive(className)){
					return null;
				}
				className = ClickUtils.resolveClassName(method.getDeclaringType(), className);
				IJavaProject javaProject = method.getDeclaringType().getJavaProject();
				IType type = javaProject.findType(className);
				if(type!=null && type.exists()){
					return new TemplateObject(type);
				}
			} catch(Exception ex){
			}
			return null;
		}
		
		public String toString(){
			return getDisplayName();
		}
	}
	
	public static class TemplateObjectMethod implements TemplateObjectElement {
		
		private IMethod method;
		
		public TemplateObjectMethod(IMethod method){
			this.method = method;
		}
		
		public String getName(){
			return this.method.getElementName();
		}
		
//		public int getArgumentCount(){
//			return this.method.getParameterTypes().length;
//		}
		
		public String getDisplayName(){
			StringBuffer sb = new StringBuffer();
			sb.append(getName());
			sb.append("(");
			String[] types = this.method.getParameterTypes();
			String[] names = null;
			try {
				names = this.method.getParameterNames();
			} catch(Exception ex){
				names = new String[types.length];
				for(int i=0;i<names.length;i++){
					names[i] = "arg" + i;
				}
			}
			for(int i=0;i<types.length;i++){
				if(i != 0){
					sb.append(", ");
				}
				sb.append(getSimpleName(Signature.toString(types[i])));
				sb.append(" ");
				sb.append(names[i]);
			}
			sb.append(") ");
			try {
				sb.append(getSimpleName(Signature.toString(method.getReturnType())));
			} catch(Exception ex){
			}
			sb.append(" - ");
			sb.append(method.getDeclaringType().getElementName());
			return sb.toString();
		}
		
		public TemplateObject toTemplateObject(){
			// TODO This implementation is same to TemplateObjectProperty.
			try {
				String className = ClickUtils.removeTypeParameter(Signature.toString(this.method.getReturnType()));
				if(ClickUtils.isPrimitive(className)){
					return null;
				}
				className = ClickUtils.resolveClassName(method.getDeclaringType(), className);
				IJavaProject javaProject = method.getDeclaringType().getJavaProject();
				IType type = javaProject.findType(className);
				if(type!=null && type.exists()){
					return new TemplateObject(type);
				}
			} catch(Exception ex){
			}
			return null;
		}
		
		public String toString(){
			return getDisplayName();
		}
	}
	
	public static interface TemplateObjectElement {
		public String getName();
		public String getDisplayName();
		public TemplateObject toTemplateObject();
	}
	
	///////////////////////////////////////////////////////////////////////////////////////
	//
	// Utility methods
	//
	///////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Get the simple classname from the full qualified classname.
	 * 
	 * @param name the full qualified classname
	 * @return the simple classname
	 */
	private static String getSimpleName(String name){
		String simpleName = ClickUtils.removeTypeParameter(name);
		if(simpleName.indexOf('.')>=0){
			simpleName = simpleName.substring(simpleName.lastIndexOf('.') + 1);
		}
		return simpleName;
	}
	
	private static IMethod[] getAllMethods(IType type) throws JavaModelException {
		ArrayList list = new ArrayList();
		IMethod[] methods = type.getMethods();
		for(int i=0;i<methods.length;i++){
			if(!methods[i].isConstructor() && !methods[i].isMainMethod() && Flags.isPublic(methods[i].getFlags())){
				list.add(methods[i]);
			}
		}
		// search super class
		ITypeHierarchy hierarchy = type.newSupertypeHierarchy(null);
		IType[] superClass = hierarchy.getAllSuperclasses(type);
		for(int i=0;i<superClass.length;i++){
			IMethod[] superMethods = superClass[i].getMethods();
			for(int j=0;j<superMethods.length;j++){
				if(!superMethods[j].isConstructor() && !superMethods[j].isMainMethod() && Flags.isPublic(superMethods[j].getFlags())){
					list.add(superMethods[j]);
				}
			}
		}
		return (IMethod[])list.toArray(new IMethod[list.size()]);
	}
	
}
