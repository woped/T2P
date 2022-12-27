package de.dhbw.WoPeDText2Process.UTWorldModelToPetriNet;

import de.dhbw.text2process.processors.worldmodel.WorldModelBuilder;
import de.dhbw.text2process.processors.petrinet.PetrinetBuilder;
import de.dhbw.text2process.exceptions.PetrinetGenerationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class PetrinetBuilderTest {

    static Logger logger = LoggerFactory.getLogger(PetrinetBuilderTest.class);

    /*
    For module testing during implementation phase
    */
    public static void main(String [] args) throws PetrinetGenerationException, IOException {

        //The Manager finishes the documents. if he likes it, he signs it and sends it away. If he doesnt like it, he throws it away.

        /**** Mock WorldModel ****//*
        WorldModelBuilder WMBuilder = new WorldModelBuilder("The manager finishes the document. If he likes it, he sends it to the office. Otherwise he throws it in the bin.");
        PetrinetBuilder PNBuilder = new PetrinetBuilder(WMBuilder.buildWorldModel(true));*/

        /**** Build WorldModel ****/
        WorldModelBuilder WMBuilder = new WorldModelBuilder("The manager finishes the document. If he likes it, he sends it to the office. Otherwise he throws it in the bin.");
        PetrinetBuilder PNBuilder = new PetrinetBuilder(WMBuilder.buildWorldModel(true));

        String processPNML=PNBuilder.buildPNML();
        logger.info("Build Results:");
        logger.info(processPNML);
    }
}
