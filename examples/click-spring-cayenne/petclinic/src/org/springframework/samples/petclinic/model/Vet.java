package org.springframework.samples.petclinic.model;

import org.springframework.samples.petclinic.model.auto._Vet;

public class Vet extends _Vet {
    public int getNrOfSpecialties() {
        return getSpecialties().size();
    }
}

