package de.dhbw.t2ppreprocessor.NLPToolWrapper;
import de.dhbw.t2ppreprocessor.models.T2PPreProText;
import edu.stanford.nlp.io.*;
import edu.stanford.nlp.pipeline.*;
import edu.stanford.nlp.semgraph.SemanticGraph;

import java.util.*;

public class StanfordCoreNLPFunctionality {

    public static void main(String [] args){
        Properties props = new Properties();
        props.setProperty("annotators", "tokenize,ssplit,pos,lemma,ner,depparse,parse");
        StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
        CoreDocument cd = pipeline.processToCoreDocument("The support employee informs the customer about the granted refund.");
        List<CoreSentence> aa = cd.sentences();

        CoreSentence cs = aa.get(0);
        SemanticGraph dp = cs.dependencyParse();
        System.out.println(dp);
    }

    public static T2PPreProText analyzeText(String text){

        Properties props = new Properties();
        props.setProperty("annotators", "tokenize,ssplit,pos,lemma,ner,depparse,parse");
        StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
        CoreDocument cd = pipeline.processToCoreDocument(text);
        return new T2PPreProText(cd);

    }


}
