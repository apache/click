package org.springframework.samples.petclinic.page;

import net.sf.click.Context;
import net.sf.click.control.Submit;
import net.sf.click.control.TextField;
import net.sf.click.extras.control.LongField;

import org.springframework.samples.petclinic.control.CustomForm;
import org.springframework.samples.petclinic.control.ObjectBinder;
import org.springframework.samples.petclinic.model.Owner;

public class EditOwner extends BasePage implements ObjectBinder {
    
    private CustomForm form = new CustomForm("form");

    public EditOwner() {       
        form.add(new TextField("firstName", "First Name:", true));
        form.add(new TextField("lastName", "Last Name:", true));
        form.add(new TextField("address", "Address:", true));
        form.add(new TextField("city", "City:", true));
        form.add(new LongField("telephone", "Telephone:", true)); 
        form.add(new Submit("save", "Save", this, "onSubmitClick"));
        form.add(new Submit("cancel", " Cancel ", this, "onCancelClick")); 
        form.setObjectBinder(this);
        
        addControl(form);         
    }
    
    /**
     * @see ObjectBinder#getObject(Context)
     */
    public Object getObject(Context context) {
        return new Owner();
    }
    
    public void setOwner(Owner owner) {
        form.setBoundObject(owner);
    }
    
    public boolean onSubmitClick() {
        if (form.isValid()) {
            Owner owner = (Owner) form.getBoundObject();
            
            getClinic().storeOwner(owner);
            
            form.removeBoundObject();
            
            String url = getContext().getPagePath(OwnerDetails.class);
            setRedirect(url + "?owner.id=" + owner.getId());
            return false;
        }
        return true;
    }
      
    public boolean onCancelClick() {  
        Owner owner = (Owner) form.getBoundObject();
        form.removeBoundObject();
        
        String url = getContext().getPagePath(OwnerDetails.class);
        setRedirect(url + "?owner.id=" + owner.getId());
        return false;
    }

}
