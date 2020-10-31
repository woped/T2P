package de.dhbw.t2ppreprocessor.Controller;

import de.dhbw.t2ppreprocessor.NLPToolWrapper.PennTreeBankConstants;
import de.dhbw.t2ppreprocessor.NLPToolWrapper.StanfordDependencyConstants;
import de.dhbw.t2ppreprocessor.models.DependencyPatternItem;
import de.dhbw.t2ppreprocessor.models.POSTagPatternItem;
import de.dhbw.t2ppreprocessor.models.SentencePattern;
import de.dhbw.t2ppreprocessor.models.T2PPreProSentence;

import java.util.ArrayList;
import java.util.List;

public class SentencePatternInitializer {

    static PennTreeBankConstants pCons;
    static StanfordDependencyConstants sfCons;
    static List<String> depAnySubject = new ArrayList<String>();
    static List<String> depAnyObject = new ArrayList<String>();
    static List<String> posAnyNounType = new ArrayList<String>();

    public static List<SentencePattern> initializeSentencePatterns(){
        /*This set of patterns is considered fixed*/

        initializeGenericLists();

        List<SentencePattern> patternList = new ArrayList<SentencePattern>();

        // Activities
        patternList.add(buildActivityRule1());
        patternList.add(buildActivityRule2());
        patternList.add(buildActivityRule3());

        return  patternList;
    }

    private static void initializeGenericLists(){
        //Generic Dependency Groups
        List<String> depAnySubject = new ArrayList<String>();
        depAnySubject.add(sfCons.nominalSubject);
        depAnySubject.add(sfCons.passiveNominalSubject);
        depAnySubject.add(sfCons.subject);
        depAnySubject.add(sfCons.clausalSubject);
        depAnySubject.add(sfCons.passiveClausalSubject);

        List<String> depAnyObject = new ArrayList<String>();
        depAnyObject.add(sfCons.directObject);
        depAnyObject.add(sfCons.indirectObject);
        depAnyObject.add(sfCons.object);

        //Generic POS Groups
        List<String> posAnyNounType = new ArrayList<String>();
        posAnyNounType.add(pCons.NounPlural);
        posAnyNounType.add(pCons.NounSingularOrMass);
        posAnyNounType.add(pCons.PersonalPronoun);

    }
    private static SentencePattern buildActivityRule1(){
        //-------------------------------------------------------
        // <Subject> <Verb(present Tense / Third Person)> <Object>
        //-------------------------------------------------------

        List<DependencyPatternItem> dependencyPatterns = new ArrayList<DependencyPatternItem>();
        List<POSTagPatternItem> POSTagPatterns = new ArrayList<POSTagPatternItem>();
        List<String> verbPresentTense = new ArrayList<String>();

        dependencyPatterns.add(new DependencyPatternItem(depAnySubject,posAnyNounType));
        dependencyPatterns.add(new DependencyPatternItem(depAnySubject,posAnyNounType));

        verbPresentTense.add(pCons.Verb3rdPersonSingularPresent);
        POSTagPatterns.add(new POSTagPatternItem(verbPresentTense));

        return new SentencePattern(dependencyPatterns,POSTagPatterns, SentencePattern.semanticClassAction);
    }

    private static SentencePattern buildActivityRule2(){
        //-------------------------------------------------------
        // <Subject> <Verb(future Tense)> <Object>
        //-------------------------------------------------------

        List<DependencyPatternItem> dependencyPatterns = new ArrayList<DependencyPatternItem>();
        List<POSTagPatternItem> POSTagPatterns = new ArrayList<POSTagPatternItem>();
        List<String> posModal = new ArrayList<String>();
        List<String> depAux = new ArrayList<String>();
        List<String> posVerbBase = new ArrayList<String>();

        dependencyPatterns.add(new DependencyPatternItem(depAnySubject,posAnyNounType));
        dependencyPatterns.add(new DependencyPatternItem(depAnyObject,posAnyNounType));

        posModal.add(pCons.Modal);
        depAux.add(sfCons.auxiliary);
        dependencyPatterns.add(new DependencyPatternItem(depAux,posModal));

        posVerbBase.add(pCons.VerbBaseForm);
        POSTagPatterns.add(new POSTagPatternItem(posVerbBase));

        return new SentencePattern(dependencyPatterns,POSTagPatterns, SentencePattern.semanticClassAction);
    }

    private static SentencePattern buildActivityRule3(){
        //-------------------------------------------------------
        // <Subject> <Verb(Present Tense / non 3rd person / Impperative)> <Object>
        //-------------------------------------------------------

        List<DependencyPatternItem> dependencyPatterns = new ArrayList<DependencyPatternItem>();
        List<POSTagPatternItem> POSTagPatterns = new ArrayList<POSTagPatternItem>();
        List<String> verbPresentTense = new ArrayList<String>();

        dependencyPatterns.add(new DependencyPatternItem(depAnySubject,posAnyNounType));
        dependencyPatterns.add(new DependencyPatternItem(depAnySubject,posAnyNounType));

        verbPresentTense.add(pCons.VerbNon3rdPersonSingularPresent);
        POSTagPatterns.add(new POSTagPatternItem(verbPresentTense));

        return new SentencePattern(dependencyPatterns,POSTagPatterns, SentencePattern.semanticClassAction);
    }


}
