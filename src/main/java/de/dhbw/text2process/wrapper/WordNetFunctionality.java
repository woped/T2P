package de.dhbw.text2process.wrapper;

import de.dhbw.text2process.models.worldModel.Action;
import de.dhbw.text2process.processors.worldmodel.Constants;
import de.dhbw.text2process.processors.worldmodel.processing.ProcessingUtils;
import de.dhbw.text2process.processors.worldmodel.transform.ListUtils;
import edu.mit.jwi.item.*;
import edu.mit.jwi.morph.WordnetStemmer;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WordNetFunctionality {

  Logger logger = LoggerFactory.getLogger(WordNetFunctionality.class);

  private WordNetInitializer wni;

  public WordNetFunctionality() {
    logger.debug("Instantiate WordNetInitializer as wni ...");
    wni = WordNetInitializer.getInstance();
  }

  // checks whether a noun is a animate_thing
  public boolean isAnimate(String noun) {
    try {
      return checkHypernymTree(noun, POS.NOUN, ListUtils.getList("animate_thing"));
    } catch (Exception e) {
      e.printStackTrace();
    }
    return false;
  }
  // checks whether a noun can be a group_action
  public boolean canBeGroupAction(String mainNoun) {
    try {
      return checkHypernymTree(mainNoun, POS.NOUN, ListUtils.getList("group_action"));
    } catch (Exception e) {
      e.printStackTrace();
    }
    return false;
  }
  // checks wether a thing is a person or a system
  public boolean canBePersonOrSystem(String fullNoun, String mainNoun) {
    try {
      if (Constants.f_personCorrectorList.contains(fullNoun)) {
        return true;
      }
      if (ProcessingUtils.isPersonalPronoun(mainNoun)) {
        return true;
      }
      String[] _idw = wni.getIndexWord(fullNoun, POS.NOUN);
      if (_idw == null || (!_idw[0].contains(mainNoun))) {
        return checkHypernymTree(mainNoun, POS.NOUN, Constants.f_realActorDeterminers);
      }
      return checkHypernymTree(_idw, Constants.f_realActorDeterminers);
    } catch (Exception e) {
      e.printStackTrace();
    }
    return false;
  }
  // checks wether a noun is a timeperiod
  public boolean isTimePeriod(String mainNoun) {
    try {
      return checkHypernymTree(mainNoun, POS.NOUN, ListUtils.getList("time_period"));
    } catch (Exception e) {
      e.printStackTrace();
    }
    return false;
  }
  // checks wether verb is an interaction verb
  public boolean isInteractionVerb(Action a) {
    String _verb = getBaseForm(a.getName());
    if (Constants.f_interactionVerbs.contains(_verb)) {
      return true;
    }
    if (!isWeakVerb(_verb)) {
      try {
        String[] _idw = null;
        if (a.getMod() != null && ((a.getModPos() - a.getWordIndex()) < 2)) {
          _idw = wni.getIndexWord(_verb + " " + a.getMod(), POS.VERB);
        }
        if (_idw == null) {
          _idw = wni.getIndexWord(_verb + " " + a.getPrt(), POS.VERB);
        }
        if (_idw == null) {
          _idw = wni.getIndexWord(_verb, POS.VERB);
        }
        if (_idw != null) {
          return checkHypernymTree(_idw, Constants.f_interactionVerbs);
        }
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
    return false;
  }
  // derive verb from noun
  public String deriveVerb(String noun) {
    return wni.deriveVerb(noun);
  }

  // ???
  public boolean isWeakAction(Action a) {
    if (isWeakVerb(a.getName())) {
      if (a.getXcomp() == null || isWeakVerb(a.getXcomp().getVerb())) {
        return true;
      }
    }
    return false;
  }
  // checks if verb is a weak verb
  public boolean isWeakVerb(String v) {
    return Constants.f_weakVerbs.contains(getBaseForm(v));
  }
  // ???
  public boolean isMetaActor(String fullNoun, String noun) {
    logger.debug(
        "Check if " + fullNoun + " is listed in the Constant attribute f_personCorrectorList ...");
    if (!Constants.f_personCorrectorList.contains(fullNoun)) {
      logger.debug("\t" + fullNoun + " not found");
      try {
        logger.debug("\tCreate a IndexWord based on " + fullNoun);
        String[] indexWord = wni.getIndexWord(fullNoun, POS.NOUN);
        logger.debug("\tCheck if indexWord is null or not part of its lemmas");
        if (indexWord == null || (!indexWord[0].contains(noun))) {
          logger.debug("\t\t Fill indexWord with information of the WordNet dictionary");
          indexWord = wni.getIndexWord(noun, POS.NOUN);
        }
        boolean isHypernymTree = false;
        logger.debug("Checking if " + indexWord + " has a hypernym tree.");
        isHypernymTree = checkHypernymTree(indexWord, Constants.f_metaActorsDeterminers);
        logger.debug("Returning the value of isHypernymTree: " + isHypernymTree);
        return isHypernymTree;
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
    return false;
  }
  // checks verbtype
  public boolean isVerbOfType(String verb, String type) {
    try {
      return checkHypernymTree(verb, POS.VERB, ListUtils.getList(type));
    } catch (Exception e) {
      e.printStackTrace();
    }
    return false;
  }
  // checks data object
  public boolean canBeDataObject(String fullNoun, String noun) {
    try {
      String[] _idw = wni.getIndexWord(fullNoun, POS.NOUN);

      if (_idw == null || (!_idw[0].contains(noun)))
        return checkHypernymTree(noun, POS.NOUN, Constants.f_dataObjectDeterminers);
      return checkHypernymTree(_idw, Constants.f_dataObjectDeterminers);
    } catch (Exception e) {
      e.printStackTrace();
    }
    return false;
  }

  // helpers
  private boolean checkHypernymTree(String word, POS pos, List<String> wordsToCheck){

    String [] iw = {word, ""+pos.getTag()};
    return checkHypernymTree(iw, wordsToCheck);
  }
  private boolean checkHypernymTree(String[] idw, List<String> wordsToCheck) {
    if (idw != null) {

    boolean status = wni.checkHypernymTree(idw, wordsToCheck);

    return status;
    /*  logger.debug("checking senses of: " + idw.getLemma());

      List<IWordID> wordIDs = idw.getWordIDs();
      for (IWordID wi : wordIDs) {
        IWord word = dict.getWord(wi);
        ISynset syn = word.getSynset();

        // ignore instances
        List<ISynsetID> _pts = syn.getRelatedSynsets(Pointer.HYPERNYM_INSTANCE);
        if (!_pts.isEmpty()) {
          continue;
        }
        try {
          if (canBe(syn, wordsToCHeck, new LinkedList<>())) {
            return true;
          }
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    }
    return false;*/

    }
    return false;
  }

  private boolean canBe(ISynset s, List<String> lookFor, LinkedList<ISynset> checked)
      throws Exception {

    if (!checked.contains(s)) {
      checked.add(s); // to avoid circles
      for (String lf : lookFor) {
        List<IWord> words = s.getWords();

        for (IWord w : words) {
          if (w.getLemma().equals(lf)) {
            return true;
          }
        }
      }

      // Wieso werden Hypernyme durchsucht? Resourcen Zuweisung funktioniert ohne diese Suche.
      List<ISynsetID> _pts = s.getRelatedSynsets(Pointer.HYPERNYM);
      /*for (ISynsetID p : _pts) {
          if (canBe(dict.getSynset(p), lookFor, checked)) {
              return true;
          }
      }*/
    }
    return false;
  }


  // ???
  public String getBaseForm(String verb) {
    return getBaseForm(verb, true, POS.VERB);
  }

  public String getBaseForm(String verb, boolean keepAuxiliaries, POS pos) {
    String[] _parts = verb.split(" "); // verb can contain auxiliary verbs
    // (to acquire)
    try {
      if (_parts.length == 0) return verb;
      String lasPart = _parts[_parts.length - 1];
      if (lasPart.length() == 0) return verb;
      String[] word = wni.getIndexWord(lasPart, pos);
      if (word != null) {
        _parts[_parts.length - 1] = word[0];
        StringBuilder _b = new StringBuilder();
        for (int i = keepAuxiliaries ? 0 : _parts.length - 1; i < _parts.length; i++) {
          String _s = _parts[i];
          _b.append(_s);
          _b.append(' ');
        }
        _b.deleteCharAt(_b.length() - 1);
        return _b.toString();
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    return verb;
  }
}
