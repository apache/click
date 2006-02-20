package net.sf.clickide.ui.wizard;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.internal.ui.wizards.NewClassCreationWizard;
import org.eclipse.jdt.ui.wizards.NewClassWizardPage;

public class NewClassWizard extends NewClassCreationWizard {

	private String superClass;
	private String className;
	private List implement;
	
	public NewClassWizard() {
		super();
	}
	
	public void setSuperClass(String superClass){
		this.superClass = superClass;
	}
	
	public void setClassName(String className){
		this.className = className;
	}
	
	public void addInterface(String implement){
		if(this.implement==null){
			this.implement = new ArrayList();
		}
		this.implement.add(implement);
	}
	
	public void addPages() {
		super.addPages();
		NewClassWizardPage fPage = (NewClassWizardPage)getPages()[0];
		if(superClass!=null){
			fPage.setSuperClass(superClass, true);
		}
		if(implement!=null){
			fPage.setSuperInterfaces(implement, true);
		}
		if(className!=null){
			int index = className.lastIndexOf('.');
			if(index!=-1){
				String packageName = className.substring(0,index);
				className = className.substring(index+1,className.length());
				IPackageFragment pack = fPage.getPackageFragmentRoot().getPackageFragment(packageName);
				fPage.setPackageFragment(pack,true);
			}
			fPage.setTypeName(className,true);
		}
	}
}
