/** modified taken from https://github.com/FabianFriedrich/Text2Process */
package de.dhbw.text2process.wrapper;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import de.dhbw.text2process.config.PropertiesWithJavaConfig;
import com.google.gson.Gson;
import de.dhbw.text2process.models.worldModel.Action;
import de.dhbw.text2process.processors.worldmodel.Constants;
import de.dhbw.text2process.processors.worldmodel.processing.ProcessingUtils;
import de.dhbw.text2process.processors.worldmodel.transform.ListUtils;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.URLDecoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.*;

import net.didion.jwnl.JWNL;
import net.didion.jwnl.JWNLException;
import net.didion.jwnl.data.IndexWord;
import net.didion.jwnl.data.POS;
import net.didion.jwnl.data.Pointer;
import net.didion.jwnl.data.PointerType;
import net.didion.jwnl.data.Synset;
import net.didion.jwnl.data.Word;
import net.didion.jwnl.data.list.PointerTargetTree;
import net.didion.jwnl.dictionary.Dictionary;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WordNetWrapper {
  static Logger logger = LoggerFactory.getLogger(WordNetWrapper.class);


  private static HttpClient httpClient;
  private static Gson gson;
  private static ArrayList<String> f_acceptedAMODList = new ArrayList<String>();
  private static ArrayList<String> acceptedForwardLinkList = new ArrayList<String>();

  private static Dictionary dictionary;

  public static void init() throws JWNLException {

    // TODO Prüfung der Initialisierung des Dictionary. Es muss das richtige WordNet
    Locale.setDefault(Locale.US);
    long _start = System.currentTimeMillis();

    //Setup for microservice connection

    httpClient = HttpClient.newHttpClient();
    gson = new Gson();

    try {
      for (String s : Constants.f_acceptedAMODforLoops) {
        IndexWord _iw = lookupIndexWordRemote(POS.ADJECTIVE, s);
        if (_iw != null) {
          addAllLemmas(_iw, f_acceptedAMODList);
        }
        _iw = lookupIndexWordRemote(POS.ADVERB, s);
        if (_iw != null) {
          addAllLemmas(_iw, f_acceptedAMODList);
        }
      }

      for (String s : Constants.f_acceptedForForwardLink) {
        IndexWord _iw = lookupIndexWordRemote(POS.ADJECTIVE, s);
        if (_iw != null) {
          addAllLemmas(_iw, acceptedForwardLinkList);
        }
        _iw = lookupIndexWordRemote(POS.ADVERB, s);
        if (_iw != null) {
          addAllLemmas(_iw, acceptedForwardLinkList);
        }
      }

    } catch (JWNLException e) {
      e.printStackTrace();
    }
    logger.info("Loaded WordNet in " + (System.currentTimeMillis() - _start) + "ms.");
  }


  private static IndexWord lookupIndexWordRemote(POS pos, String word) {

    Map<String, String> data = new HashMap<>() {{
      put("word", word);
      put("pos", pos.getKey());
    }};

    Type typeObject = new TypeToken<HashMap>() {}.getType();
    String jsonObject = gson.toJson(data, typeObject);
    String uriString = PropertiesWithJavaConfig.wordnetHost;
    if (PropertiesWithJavaConfig.wordnetPort.length() > 0) {
      uriString += ":" + PropertiesWithJavaConfig.wordnetPort;
    }
    uriString += "/" + PropertiesWithJavaConfig.worndnetUri;
    HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(uriString))
            .POST(HttpRequest.BodyPublishers.ofString(jsonObject))
            .header("Content-Type", "application/json")
            .build();

    try {
      HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

      Type type = new TypeToken<Map<String, String>>(){}.getType();
      Map<String, String> responseMap = gson.fromJson(response.body(), type);

      String responseWord = responseMap.get("word");

      logger.warn("Got response: " + responseWord);

      IndexWord _idw = new IndexWord(responseWord, pos, new long[0]);
      return _idw;

    } catch (IOException | InterruptedException e) {
      return null;
    }

  }

  /** @return the acceptedForwardLinkList */
  public static ArrayList<String> getAcceptedForwardLinkList() {
    return acceptedForwardLinkList;
  }

  private static void addAllLemmas(IndexWord _iw, List<String> list) throws JWNLException {
    for (Synset syn : _iw.getSenses()) {
      addLemmas(syn, list);
      Pointer[] _pts = syn.getPointers(PointerType.ANTONYM);
      for (Pointer p : _pts) {
        addLemmas(p.getTargetSynset(), list);
      }
    }
  }

  public static boolean isAMODAcceptedForLinking(String value) {
    return f_acceptedAMODList.contains(value);
  }

  private static void addLemmas(Synset syn, List<String> list) {
    for (Word word : syn.getWords()) {
      String _str = word.getLemma();
      if (_str.indexOf('(') > 0) {
        _str = _str.substring(0, _str.indexOf('('));
      }
      _str = _str.replaceAll("_", " ");
      list.add(_str);
    }
  }

  //private static void addLemmasService()

  public static boolean isAnimate(String noun) {
    try {
      IndexWord _idw = lookupIndexWordRemote(POS.NOUN, noun);
      return checkHypernymTree(_idw, ListUtils.getList("animate_thing"));
    } catch (JWNLException e) {
      e.printStackTrace();
    }
    return false;
  }

  public static boolean canBePersonOrSystem(String fullNoun, String mainNoun) {
    try {
      if (Constants.f_personCorrectorList.contains(fullNoun)) {
        return true;
      }
      if (ProcessingUtils.isPersonalPronoun(mainNoun)) {
        return true;
      }
      IndexWord _idw = lookupIndexWordRemote(POS.NOUN, fullNoun);
      if (_idw == null || (!_idw.getLemma().contains(mainNoun)))
        _idw = lookupIndexWordRemote(POS.NOUN, mainNoun);
      return checkHypernymTree(_idw, Constants.f_realActorDeterminers);
    } catch (JWNLException e) {
      e.printStackTrace();
    }
    return false;
  }

  public static boolean canBeGroupAction(String mainNoun) {
    try {
      IndexWord _idw = lookupIndexWordRemote(POS.NOUN, mainNoun);
      return checkHypernymTree(_idw, ListUtils.getList("group_action"));
    } catch (JWNLException e) {
      e.printStackTrace();
    }
    return false;
  }

  public static boolean isTimePeriod(String mainNoun) {
    try {
      IndexWord _idw = lookupIndexWordRemote(POS.NOUN, mainNoun);
      return checkHypernymTree(_idw, ListUtils.getList("time_period"));
    } catch (JWNLException e) {
      e.printStackTrace();
    }
    return false;
  }

  public static boolean isInteractionVerb(Action a) {
    String _verb = getBaseForm(a.getName());
    if (Constants.f_interactionVerbs.contains(_verb)) {
      return true;
    }
    if (!isWeakVerb(_verb)) {
      try {
        IndexWord _idw = null;
        if (a.getMod() != null && ((a.getModPos() - a.getWordIndex()) < 2)) {
          _idw = lookupIndexWordRemote(POS.VERB, _verb + " " + a.getMod());
        }
        if (_idw == null) {
          _idw = lookupIndexWordRemote(POS.VERB, _verb + " " + a.getPrt());
        }
        if (_idw == null) {
          _idw = lookupIndexWordRemote(POS.VERB, _verb);
        }
        if (_idw != null) {
          return checkHypernymTree(_idw, Constants.f_interactionVerbs);
        }
      } catch (JWNLException e) {
        e.printStackTrace();
      }
    }
    return false;
  }

  private static boolean checkHypernymTree(IndexWord idw, List<String> wordsToCHeck)
      throws JWNLException {
    if (idw != null) {
      for (Synset s : idw.getSenses()) {
        // ignore instances!!!
        if (s.getPointers(PointerType.INSTANCE_HYPERNYM).length != 0) {
          continue;
        }
        if (canBe(s, wordsToCHeck, new LinkedList<Synset>())) {
          return true;
        }
      }
    }
    return false;
  }

  /**
   * @param s
   * @param lookFor
   * @return
   * @throws JWNLException
   */
  private static boolean canBe(Synset s, List<String> lookFor, LinkedList<Synset> checked)
      throws JWNLException {
    if (!checked.contains(s)) {
      checked.add(s); // to avoid circles
      for (String lf : lookFor) {
        if (s.containsWord(lf)) {
          return true;
        }
      }
      for (Pointer p : s.getPointers(PointerType.HYPERNYM)) {
        if (canBe(p.getTargetSynset(), lookFor, checked)) {
          return true;
        }
      }
    }
    return false;
  }

  public static String deriveVerb(String noun) {
    try {
      IndexWord _idw = lookupIndexWordRemote(POS.NOUN, noun);
      if (_idw == null) {
        System.err.println("Could not find IndexWord for: " + noun);
        return null;
      }
      String _selected = null;
      int _distance = 0;
      for (Synset s : _idw.getSenses()) {
        Pointer[] _targets = s.getPointers();
        for (Pointer p : _targets) {
          if (p.getType() == PointerType.NOMINALIZATION && p.getTargetPOS() == POS.VERB) {
            for (Word w : p.getTargetSynset().getWords()) {
              int _d = StringUtils.getLevenshteinDistance(w.getLemma(), noun);
              if (_selected == null || _d < _distance) {
                _selected = w.getLemma();
                _distance = _d;
              }
            }
          }
        }
      }
      return _selected;
    } catch (JWNLException e) {
      e.printStackTrace();
      return null;
    }
  }

  public static boolean isWeakAction(Action a) {
    if (isWeakVerb(a.getName())) {
      if (a.getXcomp() == null || isWeakVerb(a.getXcomp().getVerb())) {
        return true;
      }
    }
    return false;
  }

  public static boolean isWeakVerb(String v) {
    return Constants.f_weakVerbs.contains(getBaseForm(v));
  }

  /** @param _tt */
  @SuppressWarnings("unused")
  private static void print(PointerTargetTree _tt) {
    if (_tt.getRootNode() != null) {
      logger.info(_tt.getRootNode().toString());
      _tt.getRootNode().getChildTreeList().print();
    }
  }

  /**
   * tries to lookup the word in wordnet and if found return the lemma (base form) of the verb. if
   * it is not found, the verb is returned unchanged.
   *
   * @param verb
   * @return
   */
  public static String getBaseForm(String verb) {

    return getBaseForm(verb, true, POS.VERB);
  }


  /**
   * tries to lookup the word in wordnet and if found return the lemma (base form) of the verb. if
   * it is not found, the verb is returned unchanged.
   *
   * @param verb
   * @return
   */
  public static String getBaseForm(String verb, boolean keepAuxiliaries, POS pos) {
    String[] _parts = verb.split(" "); // verb can contain auxiliary verbs
    // (to acquire)
    IndexWord word = lookupIndexWordRemote(pos, _parts[_parts.length - 1]);
    //IndexWord word = lookupIndexWordRemote(pos, _parts[_parts.length - 1]);
    if (word != null) {
      _parts[_parts.length - 1] = word.getLemma();
      StringBuilder _b = new StringBuilder();
      for (int i = keepAuxiliaries ? 0 : _parts.length - 1; i < _parts.length; i++) {
        String _s = _parts[i];
        _b.append(_s);
        _b.append(' ');
      }
      _b.deleteCharAt(_b.length() - 1);
      return _b.toString();
    }
    return verb;
  }

  /**
   * @param lowerCase
   * @return
   */
  public static boolean isMetaActor(String fullNoun, String noun) {
    if (!Constants.f_personCorrectorList.contains(fullNoun)) {
      try {
        IndexWord _idw = lookupIndexWordRemote(POS.NOUN, fullNoun);
        if (_idw == null || (!_idw.getLemma().contains(noun)))
          _idw = lookupIndexWordRemote(POS.NOUN, noun);
        return checkHypernymTree(_idw, Constants.f_metaActorsDeterminers);
      } catch (JWNLException e) {
        e.printStackTrace();
      }
    }
    return false;
  }

  /**
   * compares the given verb and all synonyms/hypernyms with the type word this way it can be
   * checked if, e.g., a verb is of type "end" or "finish".
   *
   * @param verb
   * @param type
   * @return
   */
  public static boolean isVerbOfType(String verb, String type) {
    try {
      IndexWord _idw = lookupIndexWordRemote(POS.VERB, verb);
      return checkHypernymTree(_idw, ListUtils.getList(type));
    } catch (JWNLException e) {
      e.printStackTrace();
    }
    return false;
  }

  /**
   * @param name
   * @return
   */
  public static boolean canBeDataObject(String fullNoun, String noun) {
    try {
      IndexWord _idw = lookupIndexWordRemote(POS.NOUN, fullNoun);
      if (_idw == null || (!_idw.getLemma().contains(noun)))
        _idw = lookupIndexWordRemote(POS.NOUN, noun);
      return checkHypernymTree(_idw, Constants.f_dataObjectDeterminers);
    } catch (JWNLException e) {
      e.printStackTrace();
    }
    return false;
  }

}
