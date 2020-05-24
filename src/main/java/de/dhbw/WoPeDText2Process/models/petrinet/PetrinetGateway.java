package de.dhbw.WoPeDText2Process.models.petrinet;

import de.dhbw.WoPeDText2Process.processors.petrinet.PetrinetElementBuilder;

public abstract class  PetrinetGateway {

    protected PetrinetElementBuilder elementBuilder;

    public PetrinetGateway(PetrinetElementBuilder elementbuilder){
        this.elementBuilder=elementbuilder;
    }
}
