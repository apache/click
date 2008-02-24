package org.springframework.samples.petclinic.page;

public class Vets extends BasePage {
    
    /**
     * Add list of Vets to the page model under the name "vets".
     * <p/>
     * Lits is added in <tt>onInit()</tt> method rather than in the constructor
     * to enable Clinic dependency to be injected.
     */
    public void onInit() {
        addModel("vets", getClinic().getVets());
    }

}
