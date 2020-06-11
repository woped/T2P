package de.dhbw.WoPeDText2Process.wrapper;

import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.trees.GrammaticalStructureFactory;
import edu.stanford.nlp.trees.PennTreebankLanguagePack;
import edu.stanford.nlp.trees.TreebankLanguagePack;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

public class StanfordParserInitializer {

    Logger logger = LoggerFactory.getLogger(StanfordParserInitializer.class);

    private static StanfordParserInitializer stanfordParserInitializer;
    private static StanfordCoreNLP pipeline;
    TreebankLanguagePack tlp;
    GrammaticalStructureFactory gsf;

    private StanfordParserInitializer(){}

    public synchronized  static StanfordParserInitializer getInstance(){
        if(stanfordParserInitializer == null){
            synchronized (StanfordParserInitializer.class){
                if(stanfordParserInitializer == null){
                    stanfordParserInitializer = new StanfordParserInitializer();
                    stanfordParserInitializer.init();
                }
            }
        }
        return stanfordParserInitializer;
    }

    public static synchronized void resetInstance(){
        stanfordParserInitializer =null;
    }

    public synchronized StanfordCoreNLP getPipeline() {return pipeline;};
    public synchronized GrammaticalStructureFactory getGrammaticalStructure() {return gsf;};

    private void init(){
        try{
            Properties props = new Properties();
            //props.setProperty("annotators", "tokenize,ssplit,pos,lemma,ner,parse");
            props.setProperty("annotators", "tokenize,ssplit,parse,lemma,ner,dcoref");
            //props.setProperty("annotators", "tokenize,ssplit,pos,lemma,ner,parse,depparse,dcoref");
            props.setProperty("parse.model", "edu/stanford/nlp/models/lexparser/englishPCFG.ser.gz");
            //props.setProperty("parse.model", "edu/stanford/nlp/models/srparser/englishSR.ser.gz");
            props.setProperty("depparse.model", "/edu/stanford/nlp/models/parser/nndep/english_UD.gz");
            pipeline = new StanfordCoreNLP(props);
            tlp = new PennTreebankLanguagePack();
            gsf = tlp.grammaticalStructureFactory();
        }
        catch(Exception ex) {
            ex.printStackTrace();
        }
    }

}
