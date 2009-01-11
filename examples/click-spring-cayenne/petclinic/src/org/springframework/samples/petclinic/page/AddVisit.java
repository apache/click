package org.springframework.samples.petclinic.page;

import org.apache.click.Context;
import org.apache.click.control.Submit;
import org.apache.click.control.TextArea;
import org.apache.click.extras.control.DateField;

import org.springframework.samples.petclinic.control.CustomForm;
import org.springframework.samples.petclinic.control.ObjectBinder;
import org.springframework.samples.petclinic.model.Pet;
import org.springframework.samples.petclinic.model.Visit;

public class AddVisit extends BasePage implements ObjectBinder {
    
    private CustomForm form = new CustomForm("form");

    public AddVisit() {
        form.add(new DateField("date", "Date:", true));
        form.add(new TextArea("description", "Description:", true));
        form.add(new Submit("save", "Save", this, "onSubmitClick")); 
        form.add(new Submit("cancel", " Cancel ", this, "onCancelClick"));
        form.setObjectBinder(this);
        
        addControl(form);
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
            Visit visit = getVisit();
            getClinic().storeVisit(visit);
            
            form.removeBoundObject();
            
            String url = getContext().getPagePath(OwnerDetails.class);
            setRedirect(url + "?owner.id=" + visit.getPet().getOwner().getId());
            return false;
        }
        return true;
    }

    public boolean onCancelClick() {
        Visit visit = getVisit();
        Pet pet = visit.getPet();
        pet.removeFromVisits(visit);
        form.removeBoundObject();
        
        String url = getContext().getPagePath(OwnerDetails.class);
        setRedirect(url + "?owner.id=" + pet.getOwner().getId());
        return false;
    }

    public void onRender() {
        Visit visit = getVisit();
        addModel("pet", visit.getPet());
    }

    public Visit getVisit() {
        return (Visit) form.getBoundObject();
    }
}
