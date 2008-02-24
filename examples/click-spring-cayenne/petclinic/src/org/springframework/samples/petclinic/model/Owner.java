package org.springframework.samples.petclinic.model;

import java.util.Iterator;

import org.springframework.samples.petclinic.model.auto._Owner;

public class Owner extends _Owner {

    /**
     * Return the Pet with the given name, or null if none found for this Owner.
     * 
     * @param name
     *            to test
     * @return true if pet name is already in use
     */
    public Pet getPet(String name) {
        return getPet(name, false);
    }

    /**
     * Return the Pet with the given name, or null if none found for this Owner.
     * 
     * @param name
     *            to test
     * @return true if pet name is already in use
     */
    public Pet getPet(String name, boolean ignoreNew) {
        name = name.toLowerCase();
        for (Iterator it = getPets().iterator(); it.hasNext();) {
            Pet pet = (Pet) it.next();
            if (!ignoreNew || !pet.isNew()) {
                String compName = pet.getName();
                compName = compName.toLowerCase();
                if (compName.equals(name)) {
                    return pet;
                }
            }
        }
        return null;
    }
}

