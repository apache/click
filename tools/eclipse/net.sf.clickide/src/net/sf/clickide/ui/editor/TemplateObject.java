package net.sf.clickide.ui.editor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.eclipse.jdt.core.Flags;
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
	
	public TemplateObject(IType type){
		this.type = type;
	}
	
	/**
	 * Returns available methods in this object.
	 */
	public TemplateObjectMethod[] getMethods(){
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
		return new TemplateObjectMethod[0];
	}
	
	/**
	 * Returns available fields in this object.
	 */
	public TemplateObjectProperty[] getProperties(){
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
		return new TemplateObjectProperty[0];
	}
	
	/**
	 * Returns available methods and fields in this object.
	 */
	public TemplateObjectElement[] getChildren(){
		TemplateObjectMethod[] methods = getMethods();
		TemplateObjectProperty[] properties = getProperties();
		List result = new ArrayList();
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
		return (TemplateObjectElement[])result.toArray(new TemplateObjectElement[result.size()]);
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
		
		public String toString(){
			return getDisplayName();
		}
	}
	
	public static interface TemplateObjectElement {
		public String getName();
		public String getDisplayName();
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
		String simpleName = name;
		if(simpleName.indexOf('<')>=0){
			simpleName = simpleName.substring(0, simpleName.lastIndexOf('<'));
		}
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

	
//	/**
//	 * Creates a qualified class name from a class name which doesn't contain package name.
//	 * 
//	 * @param parent a full qualified class name of the class which uses this variable
//	 * @param type a class name which doesn't contain package name
//	 * @return full a created qualified class name
//	 */
//	private static String getFullQName(IType parent,String type){
//		if(type.indexOf('.') >= 0){
//			return type;
//		}
//		if(isPrimitive(type)){
//			return type;
//		}
//		IJavaProject project = parent.getJavaProject();
//		try {
//			IType javaType = project.findType("java.lang." + type);
//			if(javaType!=null && javaType.exists()){
//				return javaType.getFullyQualifiedName();
//			}
//		} catch(Exception ex){
//			ex.printStackTrace();
//		}
//		try {
//			IType javaType = project.findType(parent.getPackageFragment().getElementName() + "." + type);
//			if(javaType!=null && javaType.exists()){
//				return javaType.getFullyQualifiedName();
//			}
//		} catch(Exception ex){
//			ex.printStackTrace();
//		}
//		try {
//			IImportDeclaration[] imports = parent.getCompilationUnit().getImports();
//			for(int i=0;i<imports.length;i++){
//				String importName = imports[i].getElementName();
//				if(importName.endsWith("." + type)){
//					return importName;
//				}
//				if(importName.endsWith(".*")){
//					try {
//						IType javaType = project.findType(importName.replaceFirst("\\*$",type));
//						if(javaType!=null && javaType.exists()){
//							return javaType.getFullyQualifiedName();
//						}
//					} catch(Exception ex){
//					}
//				}
//			}
//		} catch(Exception ex){
//			ex.printStackTrace();
//		}
//		return type;
//	}
//	
//	/**
//	 * This method judges whether the type is a primitive type. 
//	 * 
//	 * @param type type (classname or primitive type)
//	 * @return 
//	 * <ul>
//	 *   <li>true - primitive type</li>
//	 *   <li>false - not primitive type</li>
//	 * </ul>
//	 */
//	private static boolean isPrimitive(String type){
//		if(type.equals("int") || type.equals("long") || type.equals("double") || type.equals("float") || 
//				type.equals("char") || type.equals("boolean") || type.equals("byte")){
//			return true;
//		}
//		return false;
//	}
	
}
