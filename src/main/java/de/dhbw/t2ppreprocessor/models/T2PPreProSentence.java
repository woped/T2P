package de.dhbw.t2ppreprocessor.models;

import com.sun.source.util.SourcePositions;
import edu.stanford.nlp.ling.IndexedWord;
import edu.stanford.nlp.pipeline.CoreSentence;
import edu.stanford.nlp.semgraph.SemanticGraph;
import edu.stanford.nlp.semgraph.SemanticGraphEdge;

import java.util.*;

public class T2PPreProSentence {
    //This class acts as a facade to the CoreNLP Datastructures
    private CoreSentence sentence;
    private List<SentencePattern> matchedPatterns = new ArrayList<SentencePattern>();
    private SemanticGraph sg;
    private T2PPreProText text;
    private int index;

    public T2PPreProSentence(CoreSentence sentence, T2PPreProText text, int index){
        this.sentence = sentence;
        this.text = text;
        this.index = index;
        sg = sentence.dependencyParse();
    }

    public List<String> getPartOfSpeech(){
        return sentence.posTags();
    }
    public int getIndex(){
        return index;
    }

    public String getPartOfSpeechByIndex(int index){
        return sentence.posTags().get(index-1);//Word indexing starts with 1, Lists with 0
    }

    public String getText(){
        return sentence.text();
    }

    public int getSentenceNumber(){
        return 0;
    }

    public List<T2PPreProWord> getDependencyDependentsByRelType(String depType){
        List<T2PPreProWord> dependentsList = new ArrayList<T2PPreProWord>();
        List<SemanticGraphEdge> dependencyList = sg.findAllRelns(depType);
        Iterator<SemanticGraphEdge> i = dependencyList.iterator();
        while(i.hasNext()){
            IndexedWord idxWord = i.next().getTarget();
            dependentsList.add(new T2PPreProWord(idxWord.index(), idxWord.originalText()));
        }

    return dependentsList;
    }

    public boolean sentenceContainsDependencyWithRelType(String depType){
        return getDependencyDependentsByRelType(depType).stream().count() > 0;
    }

    public Set<String> getMatchedSemanticClasses(){
        Set<String> matchedSemanticClasses = new HashSet<String>();

        for(SentencePattern pattern: matchedPatterns){
            matchedSemanticClasses.add(pattern.getSemanticClass());
        }

       return matchedSemanticClasses;
    }

    public List<SentencePattern> getMatchedPatterns(){
        return matchedPatterns;
    }

    public void addMatchedPattern(SentencePattern pattern){
        matchedPatterns.add(pattern);
    }

    public boolean currentlyMatchesAnyPattern(){
            return matchedPatterns.size() > 0;
    }

}
