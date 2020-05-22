package de.dhbw.WoPeDText2Process.petrinet;

import de.dhbw.WoPeDText2Process.petrinet.processors.PetrinetElementBuilder;

public abstract class  PetrinetGateway {

    protected PetrinetElementBuilder elementBuilder;

    public PetrinetGateway(PetrinetElementBuilder elementbuilder){
        this.elementBuilder=elementbuilder;
    }
}
