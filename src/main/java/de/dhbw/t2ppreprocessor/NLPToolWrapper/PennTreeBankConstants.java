package de.dhbw.t2ppreprocessor.NLPToolWrapper;

import edu.mit.jwi.item.POS;

import java.util.HashMap;

public class PennTreeBankConstants {

    //To the best knowledge taken from here: https://www.ling.upenn.edu/courses/Fall_2003/ling001/penn_treebank_pos.html

    public static final String	Coordinatingconjunction = "CC";
    public static final String  cardinalNumber = "CD";
    public static final String	determiner = "DT";
    public static final String	ExistentialThere = "EX";
    public static final String	foreignWord = "FW";
    public static final String	prepositionOrSubordinatingConjunction = "IN";
    public static final String	Adjective = "JJ";
    public static final String	AdjectiveComparative = "JJR";
    public static final String	AdjectiveSuperlative = "JJS";
    public static final String	ListItemMarker = "LS";
    public static final String	Modal = "MD";
    public static final String	NounSingularOrMass = "NN";
    public static final String	NounPlural = "NNS";
    public static final String	ProperNounSingular = "NNP";
    public static final String	ProperNounPlural = "NNPS";
    public static final String	Predeterminer = "PDT";
    public static final String	PossessiveEnding = "POS";
    public static final String	PersonalPronoun = "PRP";
    public static final String	PossessivePronoun = "PRP$";
    public static final String	Adverb = "RB";
    public static final String	AdverbComparative = "RBR";
    public static final String	AdverbSuperlative = "RBS";
    public static final String	Particle = "RP";
    public static final String	Symbol = "SYM";
    public static final String	to = "TO";
    public static final String	Interjection = "UH";
    public static final String	VerbBaseForm = "VB";
    public static final String	VerbPastTense = "VBD";
    public static final String	VerbGerundOrPresentParticiple = "VBG";
    public static final String	VerbPastParticiple = "VBN";
    public static final String	VerbNon3rdPersonSingularPresent = "VBP";
    public static final String	Verb3rdPersonSingularPresent = "VBZ";
    public static final String	WhDeterminer = "WDT";
    public static final String	WhPronoun = "WP";
    public static final String	PossessiveWHPronoun = "WP$";
    public static final String	WhAdverb = "WRB";

    private HashMap<String,String> posLookupMap;
    public PennTreeBankConstants(){
        buildHashsetForDescLookup( );
    }

    public String lookupPOSTagDesc(String POSTag){
        return posLookupMap.get(POSTag); // This is intended for readable outputs (POS tags -> Descriptions)
    }

    private void buildHashsetForDescLookup(){
        posLookupMap.put("CC","Coordinating conjunction");
        posLookupMap.put("CD","Cardinal number");
        posLookupMap.put("DT","Determiner");
        posLookupMap.put("EX","Existential there");
        posLookupMap.put("FW","Foreign word");
        posLookupMap.put("IN","Preposition or subordinating conjunction");
        posLookupMap.put("JJ","Adjective");
        posLookupMap.put("JJR","Adjective, comparative");
        posLookupMap.put("JJS","Adjective, superlative");
        posLookupMap.put("LS","List item marker");
        posLookupMap.put("MD","Modal");
        posLookupMap.put("NN","Noun, singular or mass");
        posLookupMap.put("NNS","Noun, plural");
        posLookupMap.put("NNP","Proper noun, singular");
        posLookupMap.put("NNPS","Proper noun, plural");
        posLookupMap.put("PDT","Predeterminer");
        posLookupMap.put("POS","Possessive ending");
        posLookupMap.put("PRP","Personal pronoun");
        posLookupMap.put("PRP$","Possessive pronoun");
        posLookupMap.put("RB","Adverb");
        posLookupMap.put("RBR","Adverb, comparative");
        posLookupMap.put("RBS","Adverb, superlative");
        posLookupMap.put("RP","Particle");
        posLookupMap.put("SYM","Symbol");
        posLookupMap.put("TO","to");
        posLookupMap.put("UH","Interjection");
        posLookupMap.put("VB","Verb, base form");
        posLookupMap.put("VBD","Verb, past tense");
        posLookupMap.put("VBG","Verb, gerund or present participle");
        posLookupMap.put("VBN","Verb, past participle");
        posLookupMap.put("VBP","Verb, non-3rd person singular present");
        posLookupMap.put("VBZ","Verb, 3rd person singular present");
        posLookupMap.put("WDT","Wh-determiner");
        posLookupMap.put("WP","Wh-pronoun");
        posLookupMap.put("WP$","Possessive wh-pronoun");
        posLookupMap.put("WRB","Wh-adverb");

    }

}
