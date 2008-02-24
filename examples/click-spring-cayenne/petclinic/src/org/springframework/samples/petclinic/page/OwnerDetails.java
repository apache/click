package org.springframework.samples.petclinic.page;

import org.springframework.samples.petclinic.control.ActionButton;
import org.springframework.samples.petclinic.model.Owner;

public class OwnerDetails extends BasePage {
    
    private ActionButton editOwnerButton;
    private ActionButton deleteOwnerButton;
    
    public void onInit() {
        editOwnerButton = 
            new ActionButton("editOwnerButton", this, "onEditOwnerClick");
        addControl(editOwnerButton);

        deleteOwnerButton = 
            new ActionButton("deleteOwnerButton", this, "onDeleteOwnerClick");
        addControl(deleteOwnerButton);
        
        String id = getContext().getRequestParameter("owner.id");
        if (id != null) {
            editOwnerButton.setValue(id);
            deleteOwnerButton.setValue(id);
            
            Owner owner = getClinic().loadOwner(id);
            addModel("owner", owner);
        }
    }
    
    public boolean onEditOwnerClick() {
        EditOwner editOwner = 
            (EditOwner) getContext().createPage(EditOwner.class);       
        Owner owner = getClinic().loadOwner(editOwnerButton.getValue());       
        editOwner.setOwner(owner);   
        setForward(editOwner);
        return false;
    }
    
    public boolean onDeleteOwnerClick() {  
        getClinic().deleteOwner(deleteOwnerButton.getValue());
        setRedirect(getContext().getPagePath(FindOwners.class));
        return false;
    }

}
