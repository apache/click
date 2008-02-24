package org.springframework.samples.petclinic.service.impl;

import java.util.Collection;
import java.util.Collections;

import org.objectstyle.cayenne.query.SelectQuery;
import org.springframework.dao.DataAccessException;
import org.springframework.orm.cayenne.CayenneTemplate;
import org.springframework.samples.petclinic.model.Owner;
import org.springframework.samples.petclinic.model.Pet;
import org.springframework.samples.petclinic.model.PetType;
import org.springframework.samples.petclinic.model.Vet;
import org.springframework.samples.petclinic.model.Visit;
import org.springframework.samples.petclinic.service.ClinicService;

/**
 * Clinic implementation using Cayenne.
 * 
 * @author Andrei Adamchik
 */
public class ClinicServiceImpl extends CayenneTemplate implements ClinicService {

    public Collection getVets() throws DataAccessException {
        return performQuery(new SelectQuery(Vet.class));
    }

    public Collection getPetTypes() throws DataAccessException {
        return performQuery(new SelectQuery(PetType.class));
    }

    public Collection findOwners(String lastName) throws DataAccessException {
        String pattern = "%" + lastName + "%";
        return performQuery("OwnerSearch", Collections.singletonMap("name",
                pattern), true);
    }
    
    public void deleteOwner(Integer id) throws DataAccessException {
        Owner owner = loadOwner(id);
        threadDataContext().deleteObject(owner);
        commitChanges();
    }
    
    public void deleteOwner(String id) throws DataAccessException {
        deleteOwner(Integer.valueOf(id));
    }

    public Owner loadOwner(Integer id) throws DataAccessException {
        // this does lazy loading of the owner
        // (i.e. if it is cached, no query is performed).
        return (Owner) objectForPK(Owner.class, id);
    }
    
    public Owner loadOwner(String id) throws DataAccessException {
        // this does lazy loading of the owner
        // (i.e. if it is cached, no query is performed).
        return (Owner) objectForPK(Owner.class, Integer.valueOf(id));
    }
    
    public Pet loadPet(Integer id) throws DataAccessException {
        return (Pet) objectForPK(Pet.class, id);
    }
    
    public Pet loadPet(String id) throws DataAccessException {
        return (Pet) objectForPK(Pet.class, Integer.valueOf(id));
    }
    
    public void storeOwner(Owner owner) throws DataAccessException {
        if (owner.getDataContext() == null) {
            threadDataContext().registerNewObject(owner);
        }
        commitChanges();
    }

    public void storePet(Pet pet) throws DataAccessException {
        commitChanges();
    }

    public void storeVisit(Visit visit) throws DataAccessException {
        commitChanges();
    }
}