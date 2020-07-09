package de.dhbw.text2process.models.petrinet;

import de.dhbw.text2process.processors.petrinet.PetrinetElementBuilder;

public abstract class  PetrinetGateway {

    protected PetrinetElementBuilder elementBuilder;

    public PetrinetGateway(PetrinetElementBuilder elementbuilder){
        this.elementBuilder=elementbuilder;
    }
}
