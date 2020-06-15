package de.dhbw.WoPeDText2Process.processors.worldmodel;

import de.dhbw.WoPeDText2Process.processors.worldmodel.transform.TextAnalyzer;
import de.dhbw.WoPeDText2Process.wrapper.FrameNetInitializer;
import de.dhbw.WoPeDText2Process.wrapper.StanfordParserFunctionality;
import de.dhbw.WoPeDText2Process.wrapper.StanfordParserInitializer;
import de.dhbw.WoPeDText2Process.wrapper.WordNetInitializer;
import de.dhbw.WoPeDText2Process.models.worldModel.Text;
import de.dhbw.WoPeDText2Process.models.worldModel.WorldModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WorldModelBuilder {

    Logger logger = LoggerFactory.getLogger(WorldModelBuilder.class);

    private String processText;
    private Text parsedText;
    private TextAnalyzer textAnalyzer = new TextAnalyzer();
    private StanfordParserFunctionality stanford = StanfordParserFunctionality.getInstance();

    public WorldModelBuilder(String processText){
        logger.debug("Constructor called with the processText = " + processText);
        this.processText = processText;
    }

    public WorldModel buildWorldModel(boolean mockBuild){
        logger.debug("Entered buildWorldModel method");
        logger.debug("Check whether mockBuild is true or false");
        if(mockBuild){
            logger.debug("\ttrue - for test purpose only");
            return buildWorldModelMock();
        } else {
            logger.debug("\tfalse - Starting to build the WorldModel");
            return buildWorldModel();
        }
    }

    public static synchronized void resetNLPTools(){
        StanfordParserInitializer.resetInstance();
        WordNetInitializer.resetInstance();
        StanfordParserInitializer.resetInstance();
        FrameNetInitializer.resetInstance();
    }

    public TextStatistics getTextStatistics() {
        TextStatistics textStatistics = new TextStatistics();
        textStatistics.setNumberOfSentences(parsedText.getSize());
        textStatistics.setAvgSentenceLength(parsedText.getAvgSentenceLength());
        textStatistics.setNumOfReferences(textAnalyzer.getNumberOfReferences());
        textStatistics.setNumOfLinks(textAnalyzer.getNumberOfLinks());
        return textStatistics;
    }

    private WorldModel buildWorldModel(){
        logger.debug("Entered buildWorldModel method");
        logger.debug("Parsing the Text through the Stanford CoreNLP ...");
        parsedText = stanford.createText(processText);
        logger.debug("Clear the analyzer ...");
        textAnalyzer.clear();
        logger.debug("Analyzing the parsed text to get a worldModel ...");
        textAnalyzer.analyze(parsedText);
        logger.debug("WorldModel created based on the given input text");
        WorldModel worldModel= textAnalyzer.getWorld();
        return worldModel;
    }

    private WorldModel buildWorldModelMock(){
        return new MockWorldModel().getMockWM();
    }
}
