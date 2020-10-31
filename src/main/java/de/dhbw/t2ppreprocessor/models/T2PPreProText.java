package de.dhbw.t2ppreprocessor.models;

import edu.stanford.nlp.pipeline.CoreDocument;
import edu.stanford.nlp.pipeline.CoreSentence;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class T2PPreProText {
    List<T2PPreProSentence> sentences = new ArrayList<T2PPreProSentence>();
    CoreDocument document;

    public T2PPreProText(CoreDocument document){
        this.document = document;
        buildSentences();
    }

    private void buildSentences(){
        int sentenceIndex = 1;
        for(CoreSentence sentence :document.sentences()){
            sentences.add(new T2PPreProSentence(sentence, this, sentenceIndex));
            sentenceIndex++;
        }
    }

    public List<T2PPreProSentence> getSentences(){
        return this.sentences;
    }

    /*further document access goes here*/

}
