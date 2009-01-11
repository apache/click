package org.springframework.samples.petclinic.page;

import org.apache.click.Context;
import org.apache.click.Page;
import org.apache.click.control.Select;
import org.apache.click.control.Submit;
import org.apache.click.control.TextField;
import org.apache.click.extras.control.DateField;

import org.springframework.samples.petclinic.control.CustomForm;
import org.springframework.samples.petclinic.control.ObjectBinder;
import org.springframework.samples.petclinic.model.EntityUtils;
import org.springframework.samples.petclinic.model.Owner;
import org.springframework.samples.petclinic.model.Pet;
import org.springframework.samples.petclinic.model.PetType;

/**
 * Provides a Edit Pet page which can be used to edit exiting Pets or create
 * new Pets.
 * 
 * @author Malcolm Edgar
 */
public class EditPet extends BasePage implements ObjectBinder {

    private CustomForm form = new CustomForm("form");
    private Select typeSelect = new Select("type.id", "Type:");

    /**
     * Create a new Edit Pet page.
     */
    public EditPet() {
        form.add(new TextField("name", "Name:", true));
        form.add(new DateField("birthDate", "Birth Date:", true));
        form.add(typeSelect);
        form.add(new Submit("save", "Save", this, "onSubmitClick"));
        form.add(new Submit("cancel", " Cancel ", this, "onCancelClick"));
        form.setObjectBinder(this);
        
        addControl(form);  
    }
    
    /**
     * Initialize the pet type select using the Clinic service, and add the Pet
     * to the page model.
     * <p/>
     * Note the PetType Select values cannot be loaded in the Page constructor 
     * as the Clinic service is not yet available.
     * 
     * @see Page#onInit()
     */
    public void onInit() {
        typeSelect.addAll(getClinic().getPetTypes(), "id", "name");
    }
    
    /**
     * @see ObjectBinder#getObject(Context)
     */
    public Object getObject(Context context) {
        // If an existing pet.id is specified in a request parameter
        // load the pet from the database
        String id = context.getRequestParameter("pet.id");
        if (id != null) {
            return getClinic().loadPet(id);
            
        } else {
            // Otherwise create new pet with the Owner specified in the
            // owner.id request parameter
            Pet pet = new Pet();
            id = context.getRequestParameter("owner.id");
            Owner owner = getClinic().loadOwner(id);
            owner.addToPets(pet);
            return pet;
        }
    }
    
    /**
     * The Save submit button action handler
     * 
     * @return true to continue processing or false otherwise
     */
    public boolean onSubmitClick() {

        int typeId = Integer.parseInt(typeSelect.getValue());
        Pet pet = getPet();
        pet = (Pet) form.getBoundObject();
        pet.setType((PetType) EntityUtils.getById(getClinic().getPetTypes(), 
                                                  PetType.class, 
                                                  typeId));

        if (form.isValid()) {            
            getClinic().storePet(pet);
            
            form.removeBoundObject();
            
            String url = getContext().getPagePath(OwnerDetails.class);
            setRedirect(url + "?owner.id=" + pet.getOwner().getId());
            return false;
        }
        
        return true;
    }
    
    public boolean onCancelClick() {  
        Pet pet = getPet();
        form.removeBoundObject();
        
        String url = getContext().getPagePath(OwnerDetails.class);
        setRedirect(url + "?owner.id=" + pet.getOwner().getId());
        return false;
    }

    public void onRender() {
        Pet pet = getPet();
        addModel("pet", pet);
    }

    public Pet getPet() {
        return (Pet) form.getBoundObject();
    }
}
