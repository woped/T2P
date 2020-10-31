package de.dhbw.t2ppreprocessor.Controller;

import de.dhbw.t2ppreprocessor.models.*;

import java.util.List;

public class SentencePatternMatcher {

    public boolean match(T2PPreProSentence sentence, SentencePattern pattern){
        boolean dep = checkDependencyPattern(sentence,pattern);
        boolean pos = checkPOSTags(pattern.getPOSTags(),sentence);

        return dep && pos;
    }
    private boolean checkDependencyPattern(T2PPreProSentence sentence, SentencePattern pattern){
        List<DependencyPatternItem> dp = pattern.getDependencyPaterns();

        //DependencyPatternItems are concatenated by a logical AND -> Every Item has to match

        for (DependencyPatternItem dependencyPatternItem : dp) {
            if (!checkDependencyPatternItem(dependencyPatternItem, sentence))
                    return false;
        }
        return true;
    }

    private boolean checkDependencyPatternItem(DependencyPatternItem dpi,T2PPreProSentence sentence){
        List<String> dependencies = dpi.getDependencyType();
        List<String> linkedPOSTags = dpi.getLinkedPOSTags();
        List<T2PPreProWord> words;
        for(String dep: dependencies){
             words = sentence.getDependencyDependentsByRelType(dep);
            if(words.size()==0){
                //no match :(
                continue;
            }else{
                if (linkedPOSTags.size() == 0)
                    return true;
                if (checkPOSTagsByWords(words,linkedPOSTags, sentence ))
                    return true;
            }
        }

        return false;
    }

    private boolean checkPOSTagsByWords(List<T2PPreProWord> words,List<String> POSTags, T2PPreProSentence sentence ){
        String posTagOfWord;
        for (T2PPreProWord word: words){
            posTagOfWord = sentence.getPartOfSpeechByIndex(word.getIndex());
            if (POSTags.contains(posTagOfWord))
                return true;
        }
        return false;
    }

    private boolean checkPOSTags(List<POSTagPatternItem> POSTags, T2PPreProSentence sentence){
        for (POSTagPatternItem patternitem : POSTags){
            if (!checkPosTagPatternItem(patternitem, sentence.getPartOfSpeech()))
                return false;
        }
        return true;
    }

    private boolean checkPosTagPatternItem(POSTagPatternItem item, List<String> POSTagsInTheSentence ){
        List<String> POSTagsInThePatternItem =  item.getPOSTags();

        for (String patternTag : POSTagsInThePatternItem) {
            if (POSTagsInTheSentence.contains(patternTag))
                return true;
        }

        return false;
    }

}
