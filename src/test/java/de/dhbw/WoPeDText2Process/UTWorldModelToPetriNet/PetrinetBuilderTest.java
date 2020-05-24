package de.dhbw.WoPeDText2Process.UTWorldModelToPetriNet;

import de.dhbw.WoPeDText2Process.processors.worldmodel.WorldModelBuilder;
import de.dhbw.WoPeDText2Process.processors.petrinet.PetrinetBuilder;
import de.dhbw.WoPeDText2Process.exceptions.PetrinetGenerationException;

public class PetrinetBuilderTest {
    /*
    For module testing during implementation phase
    */
    public static void main(String [] args) throws PetrinetGenerationException {

        //The Manager finishes the documents. if he likes it, he signs it and sends it away. If he doesnt like it, he throws it away.

        /**** Mock WorldModel ****//*
        WorldModelBuilder WMBuilder = new WorldModelBuilder("The manager finishes the document. If he likes it, he sends it to the office. Otherwise he throws it in the bin.");
        PetrinetBuilder PNBuilder = new PetrinetBuilder(WMBuilder.buildWorldModel(true));*/

        /**** Build WorldModel ****/
        WorldModelBuilder WMBuilder = new WorldModelBuilder("The manager finishes the document. If he likes it, he sends it to the office. Otherwise he throws it in the bin.");
        PetrinetBuilder PNBuilder = new PetrinetBuilder(WMBuilder.buildWorldModel(true));

        String processPNML=PNBuilder.buildPNML();
        System.out.println("Build Results:");
        System.out.println(processPNML);
    }
}
