package de.dhbw.WoPeDText2Process.nlpTest;

import de.dhbw.text2process.wrapper.WordNetInitializer;
import edu.mit.jwi.Dictionary;
import edu.mit.jwi.IDictionary;
import edu.mit.jwi.IRAMDictionary;
import edu.mit.jwi.RAMDictionary;
import edu.mit.jwi.data.ILoadPolicy;
import edu.mit.jwi.item.IIndexWord;
import edu.mit.jwi.item.IWord;
import edu.mit.jwi.item.IWordID;
import edu.mit.jwi.item.POS;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URL;
import java.util.Iterator;

public class TestWordNetDictionary {

    Logger logger = LoggerFactory.getLogger(TestWordNetDictionary.class);

    static URL url = null;
    @BeforeClass
    public static void initTest() {
        // construct the URL to the Wordnet dictionary directory
        url = WordNetInitializer.class.getResource("/NLPTools/WordNet/dict/");
    }

    @Test
    public void testWordNetDictionary () {

        // construct the dictionary object and open it
        IDictionary dict = new Dictionary(url);
        try {
            dict.open();
            try {
                // look up first sense of the word "dog "
                IIndexWord idxWord = dict.getIndexWord("dog", POS.NOUN );
                IWordID wordID = idxWord.getWordIDs().get (0) ;
                IWord word = dict.getWord( wordID );
                logger.info("Id = " + wordID );
                logger.info("Lemma = " + word.getLemma());
                logger.info("Gloss = " + word.getSynset().getGloss ());
            } finally {
                dict.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @Test
    public void testRAMDictionary() throws Exception {

        // construct the dictionary object and open it
        IRAMDictionary dict = new RAMDictionary(url, ILoadPolicy.NO_LOAD );
        dict.open();

        // do something
        trek(dict);

        // now load into memory
        logger.info("\nLoading Wordnet into memory ... ");
        long t = System.currentTimeMillis ();
        dict.load(true);
        logger.info(String.format("done(%1d msec)\n", System.currentTimeMillis()-t));

        // try it again , this time in memory
        trek(dict);
        dict.close();
    }

    public void trek(IDictionary dict){
        int tickNext = 0;
        int tickSize = 20000;
        int seen = 0;
        logger.info("Treking across Wordnet");
        long t = System.currentTimeMillis ();
        for(POS pos : POS.values())
            for(Iterator<IIndexWord> i = dict.getIndexWordIterator(pos); i.hasNext();)
                for( IWordID wid : i.next().getWordIDs ()){
                    seen += dict.getWord (wid ).getSynset().getWords().size();
                    if( seen > tickNext ){
                        logger.info(".");
                        tickNext = seen + tickSize;
                    }
                }
        logger.info(String.format("done (%1d msec )\n", System.currentTimeMillis()-t));
        logger.info("In my trek I saw " + seen + " words ");
    }

}
