package org.springframework.samples.petclinic.page;

import net.sf.click.Context;
import net.sf.click.Page;
import net.sf.click.control.Submit;
import net.sf.click.control.TextArea;
import net.sf.click.extras.control.DateField;

import org.springframework.samples.petclinic.control.CustomForm;
import org.springframework.samples.petclinic.control.ObjectBinder;
import org.springframework.samples.petclinic.model.Pet;
import org.springframework.samples.petclinic.model.Visit;

public class AddVisit extends BasePage implements ObjectBinder {
    
    private CustomForm form = new CustomForm("form");
    private Visit visit;

    public AddVisit() {
        form.add(new DateField("date", "Date:", true));
        form.add(new TextArea("description", "Description:", true));
        form.add(new Submit("save", "Save", this, "onSubmitClick")); 
        form.add(new Submit("cancel", " Cancel ", this, "onCancelClick"));
        form.setObjectBinder(this);
        
        addControl(form);
    }
    
    /**
     * @see Page#onInit()
     */
    public void onInit() {
        visit = (Visit) form.getBoundObject();
        addModel("pet", visit.getPet());
    }
    
    /**
     * @see ObjectBinder#getObject(Context)
     */
    public Object getObject(Context context) {
        String id = context.getRequestParameter("pet.id");
        Pet pet = getClinic().loadPet(id);
        Visit visit = new Visit();
        pet.addToVisits(visit);
        return visit;
    }
    
    public boolean onSubmitClick() {
        if (form.isValid()) {
            getClinic().storeVisit(visit);
            
            form.removeBoundObject();
            
            String url = getContext().getPagePath(OwnerDetails.class);
            setRedirect(url + "?owner.id=" + visit.getPet().getOwner().getId());
            return false;
        }
        return true;
    }

    public boolean onCancelClick() {  
        Pet pet = visit.getPet();
        pet.removeFromVisits(visit);
        form.removeBoundObject();
        
        String url = getContext().getPagePath(OwnerDetails.class);
        setRedirect(url + "?owner.id=" + pet.getOwner().getId());
        return false;
    }
}
