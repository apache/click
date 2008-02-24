package org.springframework.samples.petclinic.service;

import java.util.Collection;

import org.springframework.samples.petclinic.model.Owner;
import org.springframework.samples.petclinic.model.Pet;
import org.springframework.samples.petclinic.model.Visit;

/**
 * The high-level Petclinic business interface.
 *
 * @author Ken Krebs
 * @author Juergen Hoeller
 */
public interface ClinicService {

	/**
	 * Retrieve all <code>Vet</code>s from the datastore.
     * 
	 * @return a <code>Collection</code> of <code>Vet</code>s.
	 */
	public Collection getVets();

	/**
	 * Retrieve all <code>PetType</code>s from the datastore.
     * 
	 * @return a <code>Collection</code> of <code>PetType</code>s.
	 */
	public Collection getPetTypes();

	/**
	 * Retrieve <code>Owner</code>s from the datastore by last name,
	 * returning all owners whose last name <i>starts</i> with the given name.
     * 
	 * @param lastName Value to search for.
	 * @return a <code>Collection</code> of matching <code>Owner</code>s.
	 */
	public Collection findOwners(String lastName);

    /**
     * Retrieve an <code>Owner</code> from the datastore by id.
     * 
     * @param id Value to search for.
     * @return the <code>Owner</code> if found.
     */
    public Owner loadOwner(Integer id);
 
    /**
     * Retrieve an <code>Owner</code> from the datastore by id.
     * 
     * @param id Value to search for.
     * @return the <code>Owner</code> if found.
     */
    public Owner loadOwner(String id);
    
    /**
     * Retrieve a <code>Pet</code> from the datastore by id.
     * 
     * @param id Value to search for.
     * @return the <code>Pet</code> if found.
     */
    public Pet loadPet(Integer id);
    
    /**
     * Retrieve a <code>Pet</code> from the datastore by id.
     * 
     * @param id Value to search for.
     * @return the <code>Pet</code> if found.
     */
    public Pet loadPet(String id);
    
    /**
     * Save a <code>Pet</code> to the datastore,
     * either inserting or updating it.
     * 
     * @param id the identifier of the owner to delete.
     * @see org.springframework.samples.petclinic.model.Entity#isNew()
     */
    public void deleteOwner(Integer id);
    
    /**
     * Save a <code>Pet</code> to the datastore,
     * either inserting or updating it.
     * 
     * @param id the identifier of the owner to delete.
     * @see org.springframework.samples.petclinic.model.Entity#isNew()
     */
    public void deleteOwner(String id);
    
	/**
	 * Save an <code>Owner</code> to the datastore,
	 * either inserting or updating it.
     * 
	 * @param owner to add.
	 * @see org.springframework.samples.petclinic.model.Entity#isNew()
	 */
	public void storeOwner(Owner owner);

	/**
	 * Save a <code>Pet</code> to the datastore,
	 * either inserting or updating it.
     * 
	 * @param pet to add.
	 * @see org.springframework.samples.petclinic.model.Entity#isNew()
	 */
	public void storePet(Pet pet);

	/**
	 * Save a <code>Visit</code> to the datastore,
	 * either inserting or updating it.
     * 
	 * @param visit to add.
	 * @see org.springframework.samples.petclinic.model.Entity#isNew()
	 */
	public void storeVisit(Visit visit);

}
