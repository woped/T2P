package de.dhbw.WoPeDT2PPrePro;

import de.dhbw.t2ppreprocessor.Controller.SentencePatternInitializer;
import de.dhbw.t2ppreprocessor.models.SentencePattern;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class UTSentencePatternInitializer {
    @Test
    public void test(){
        List<SentencePattern> patternList = SentencePatternInitializer.initializeSentencePatterns();
        assertEquals(patternList.size(),3);

        SentencePattern pattern1 = patternList.get(0);
        assertEquals(pattern1.getDependencyPaterns().size(),2);

    }

}
