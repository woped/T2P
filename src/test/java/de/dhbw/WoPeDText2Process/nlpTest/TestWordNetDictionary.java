package de.dhbw.WoPeDText2Process.nlpTest;

import de.dhbw.WoPeDText2Process.ToolWrapper.WordNetInitializer;
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

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Iterator;

public class TestWordNetDictionary {

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
                System.out.println("Id = " + wordID );
                System.out.println("Lemma = " + word.getLemma());
                System.out.println("Gloss = " + word.getSynset().getGloss ());
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
        System.out.print("\nLoading Wordnet into memory ... ");
        long t = System.currentTimeMillis ();
        dict.load(true);
        System.out.printf("done(%1d msec)\n", System.currentTimeMillis()-t);

        // try it again , this time in memory
        trek(dict);
        dict.close();
    }

    public void trek(IDictionary dict){
        int tickNext = 0;
        int tickSize = 20000;
        int seen = 0;
        System.out.print ("Treking across Wordnet");
        long t = System.currentTimeMillis ();
        for(POS pos : POS.values())
            for(Iterator<IIndexWord> i = dict.getIndexWordIterator(pos); i.hasNext();)
                for( IWordID wid : i.next().getWordIDs ()){
                    seen += dict.getWord (wid ).getSynset().getWords().size();
                    if( seen > tickNext ){
                        System.out.print ('.');
                        tickNext = seen + tickSize;
                    }
                }
        System.out.printf ("done (%1d msec )\n", System.currentTimeMillis()-t);
        System.out.println ("In my trek I saw " + seen + " words ");
    }

}
