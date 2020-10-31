package de.dhbw.t2ppreprocessor.Controller;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import de.dhbw.t2ppreprocessor.NLPToolWrapper.StanfordCoreNLPFunctionality;
import de.dhbw.t2ppreprocessor.models.SentencePattern;
import de.dhbw.t2ppreprocessor.models.T2PPreProSentence;
import de.dhbw.t2ppreprocessor.models.T2PPreProText;

import java.io.IOException;
import java.io.StringWriter;
import java.util.List;

public class T2PPreProcessorController {

    private String PreProcessableText;

    public T2PPreProcessorController(String PreProcessableText){
        this.PreProcessableText = PreProcessableText;
    }

    public String preprocessTextToText(){
        List<T2PPreProSentence> sentences = preprocessText();
        String analysisResult = getAnalysisResult(sentences,true);
        System.out.println(analysisResult);
        return adaptTextAccordingToMatchedPatterns(sentences);
    }

    public String preprocessTextToAnnotatedText() throws  IOException{
        return getAnalysisResultJSON(preprocessText(), true, true);
    }

    private String adaptTextAccordingToMatchedPatterns(List<T2PPreProSentence> sentences){
        return PreProcessableText;
    }

    private List<T2PPreProSentence> preprocessText(){
        T2PPreProText analyzedText = StanfordCoreNLPFunctionality.analyzeText(PreProcessableText);
        List<SentencePattern> patternList = SentencePatternInitializer.initializeSentencePatterns();
        List<T2PPreProSentence> sentences = analyzedText.getSentences();

        for(T2PPreProSentence sentence: sentences){
            analyzeSentence(sentence, patternList);
        }
        return sentences;
    }

    private void analyzeSentence(T2PPreProSentence sentence, List<SentencePattern> patternList){
        SentencePatternMatcher patternMatcher = new SentencePatternMatcher();
        for (SentencePattern pattern : patternList){
            if (patternMatcher.match(sentence, pattern))
                sentence.addMatchedPattern(pattern);
        }
    }

    private String getAnalysisResult(List<T2PPreProSentence> sentences, boolean JSONFormat){
        if(JSONFormat){
            try{
                 return getAnalysisResultJSON(sentences, true, true);
            }catch(IOException e){
                System.err.println("JSON Output could not be generated");
            }
        }
        String output = "Analysis of the processed Text:\n\n";

        for(T2PPreProSentence sentence: sentences){
            output += sentence.getIndex()+": "+ sentence.getText()+ "\n";

            output+= "The Sentence has BP Elements of the following Classes: ";
            for(String s :sentence.getMatchedSemanticClasses()){
             output+= s;
            }
            output+= "\n Matched Patterns: ";

            for(SentencePattern pattern :sentence.getMatchedPatterns()){
                output+= pattern.toString();
            }
            output+="\n\n";
        }

        return output;
    }

    private String getAnalysisResultJSON(List<T2PPreProSentence> sentences, boolean prettyPrint, boolean includeMatchedPatterns) throws IOException {
        JsonFactory factory = new JsonFactory();
        StringWriter jsonObjectWriter = new StringWriter();
        JsonGenerator generator = factory.createGenerator(jsonObjectWriter);
        if (prettyPrint)
            generator.useDefaultPrettyPrinter(); // pretty print JSON
        generator.writeStartObject();
        generator.writeFieldName("Sentences");
        generator.writeStartArray();
            for(T2PPreProSentence sentence: sentences){
                generator.writeStartObject();

                    generator.writeFieldName("Index");
                    generator.writeNumber(sentence.getIndex());

                    generator.writeFieldName("Text");
                    generator.writeString(sentence.getText());

                    generator.writeFieldName("ContainsBPElements");
                    generator.writeBoolean(sentence.currentlyMatchesAnyPattern());

                    generator.writeFieldName("TypesOfContainedBPElements");
                    generator.writeStartArray();
                    for(String s: sentence.getMatchedSemanticClasses()){
                        generator.writeString(s);
                    }
                    generator.writeEndArray();
                    if (includeMatchedPatterns) {
                        generator.writeFieldName("MatchedPatterns");
                        generator.writeStartArray();
                        for (SentencePattern pattern : sentence.getMatchedPatterns()) {
                            generator.writeString(pattern.toString());
                        }
                        generator.writeEndArray();
                    }

                generator.writeEndObject();
            }
        generator.writeEndArray();
        generator.writeEndObject();
        generator.close();
        return jsonObjectWriter.toString();
    }

}
