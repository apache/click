package org.springframework.samples.petclinic.page;

import java.util.Collection;

import net.sf.click.control.Form;
import net.sf.click.control.Submit;
import net.sf.click.control.TextField;

import org.springframework.samples.petclinic.control.CustomForm;
import org.springframework.samples.petclinic.model.Owner;

public class FindOwners extends BasePage {
    
    private TextField nameField = new TextField("lastName", "Last Name:", 30);

    public FindOwners() {
        Form form = new CustomForm("form");
                
        nameField.setMaxLength(30);
        form.add(nameField);
        
        form.add(new Submit("findOwners", this, "onFindOwners"));
        
        addControl(form);
    }
    
    public boolean onFindOwners() {
        String lastName = nameField.getValue();
        
        Collection results = getClinic().findOwners(lastName);
        
        if (results.size() < 1) {
            nameField.setError("Last name not found");
            return true;
        }

        if (results.size() > 1) {
            Owners ownersPage = (Owners) getContext().createPage(Owners.class);
            ownersPage.setOwners(results);
            setForward(ownersPage);
            return false;
        }

        // 1 owner found
        Owner owner = (Owner) results.iterator().next();
        String url = getContext().getPagePath(OwnerDetails.class);
        setRedirect(url + "?owner.id=" + owner.getId());
        return false;
    }

}
