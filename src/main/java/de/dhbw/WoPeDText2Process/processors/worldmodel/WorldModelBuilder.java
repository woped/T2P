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
    private TextAnalyzer analyzer = new TextAnalyzer();
    private StanfordParserFunctionality stanford = StanfordParserFunctionality.getInstance();

    public WorldModelBuilder(String processText){
        logger.debug("Constructor called with the processText = " + processText);
        this.processText = processText;
    }

    public WorldModel buildWorldModel(boolean mockBuild){
        logger.debug("Entered buildWorldModel method");
        logger.debug("Instantiating a new WorldModel object");
        WorldModel processWM;
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
        TextStatistics _result = new TextStatistics();
        _result.setNumberOfSentences(parsedText.getSize());
        _result.setAvgSentenceLength(parsedText.getAvgSentenceLength());
        _result.setNumOfReferences(analyzer.getNumberOfReferences());
        _result.setNumOfLinks(analyzer.getNumberOfLinks());
        return _result;
    }

    private WorldModel buildWorldModel(){
        logger.debug("Entered buildWorldModel method");
        logger.debug("Parsing the Text through the Stanford CoreNLP ...");
        parsedText = stanford.createText(processText);
        logger.debug("Clear the analyzer ...");
        analyzer.clear();
        logger.debug("Analyzing the parsed text to get a worldModel ...");
        analyzer.analyze(parsedText);
        logger.debug("WorldModel created based on the given input text");
        WorldModel processWM= analyzer.getWorld();
        return processWM;
    }

    private WorldModel buildWorldModelMock(){
        return new MockWorldModel().getMockWM();
    }
}
