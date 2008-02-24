package org.springframework.samples.petclinic.page;

import java.util.Collection;

public class Owners extends BasePage {
    
    public void setOwners(Collection owners) {
        addModel("owners", owners);
    }

}
