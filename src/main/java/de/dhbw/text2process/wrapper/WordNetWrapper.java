/**
 * modified taken from https://github.com/FabianFriedrich/Text2Process
 */
package de.dhbw.text2process.wrapper;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.dhbw.text2process.models.worldModel.Action;
import de.dhbw.text2process.processors.worldmodel.Constants;
import de.dhbw.text2process.processors.worldmodel.processing.ProcessingUtils;
import de.dhbw.text2process.processors.worldmodel.transform.ListUtils;
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

public class WordNetWrapper {
	static Logger logger = LoggerFactory.getLogger(WordNetWrapper.class);

	private static ArrayList<String> f_acceptedAMODList = new ArrayList<String>();
	private static ArrayList<String> acceptedForwardLinkList = new ArrayList<String>();

	private static Dictionary dictionary;

	public static void init() {

		// TODO Prüfung der Initialisierung des Dictionary. Es muss das richtige WordNet
		Locale.setDefault(Locale.US);
		long _start = System.currentTimeMillis();

		// get the path where the jar file is located
		logger.info("Searching for file_properties.xml on ClassPath");
		String wordNetProperties = "jwnl.configuration";
		String wordNetDictionaryPath = "NLPTools/WordNet";
		logger.info("Collected infos:\n\t\t WordNetPropertiesFile: " + wordNetProperties + "\n\t\t WordNetDictionaryPath: " + wordNetDictionaryPath);
		try {
			wordNetProperties = URLDecoder.decode(wordNetProperties, "UTF-8");
		} catch (UnsupportedEncodingException e2) {
			e2.printStackTrace();
		}

		try {
			JWNL.initialize(new File(wordNetProperties).toURL().openStream());
			logger.info("used standard config");
		} catch (Exception e) {
			logger.debug(e.getMessage());
			logger.info("Start creating a individual config file");
			logger.debug("Inhalt der Systemvariable JAVA_HOME: " + System.getenv("JAVA_HOME"));
			logger.debug("Inhalt der Systemvariable WORDNET_HOME: " + System.getenv("WORDNET2_HOME"));
			
			if (!System.getenv("WORDNET2_HOME").isEmpty() && !System.getenv("WORDNET2_HOME").isBlank()) {
				logger.debug("We got: " + System.getenv("WORDNET2_HOME").trim());
				wordNetDictionaryPath = System.getenv("WORDNET2_HOME").trim() + "/dict";
				try {
					logger.info("Manipulating file_properties.xml to match the environment.");
					String newFilePropertiesString = wordNetProperties.replace(".configuration",
							"_env_spec.configuration");
					logger.debug("Lets have a look on the new file:\t\t" + newFilePropertiesString);

					logger.debug("Creating a fileProperties-Object from " + newFilePropertiesString);
					File fileProperties = new File(newFilePropertiesString);

					logger.debug("Creating a scanner using " + wordNetProperties);
					Scanner scannerOldFile = new Scanner(new File(wordNetProperties));
					StringBuffer bufferNewFileProperties = new StringBuffer();

					writeNewPropertiesFile(wordNetDictionaryPath, fileProperties, scannerOldFile,
							bufferNewFileProperties);

					readJwnlConfig(newFilePropertiesString);
					
				} catch (IOException e1) {
					System.out.println("ERROR loading WordNet!");
					e1.printStackTrace();
				}
			} else {

				try {
					logger.info("Manipulating file_properties.xml to match the environment.");
					String newFilePropertiesString = wordNetProperties.replace(".configuration",
							"_target_spec.configuration");
					logger.debug("Lets have a look on the new file:\t\t" + newFilePropertiesString);

					logger.debug("Creating a fileProperties-Object from " + newFilePropertiesString);
					File fileProperties = new File(newFilePropertiesString);

					logger.debug("Creating a scanner using " + wordNetProperties);
					Scanner scannerOldFile = new Scanner(new File(wordNetProperties));
					StringBuffer bufferNewFileProperties = new StringBuffer();

					writeNewPropertiesFile(wordNetDictionaryPath, fileProperties, scannerOldFile,
							bufferNewFileProperties);

					readJwnlConfig(newFilePropertiesString);
					
				} catch (IOException e1) {
					System.out.println("ERROR loading WordNet!");
					e1.printStackTrace();
				}
			}
		}

		dictionary = Dictionary.getInstance();

		try {
			for (String s : Constants.f_acceptedAMODforLoops) {
				IndexWord _iw = dictionary.lookupIndexWord(POS.ADJECTIVE, s);
				if (_iw != null) {
					addAllLemmas(_iw, f_acceptedAMODList);
				}
				_iw = dictionary.lookupIndexWord(POS.ADVERB, s);
				if (_iw != null) {
					addAllLemmas(_iw, f_acceptedAMODList);
				}
			}

			for (String s : Constants.f_acceptedForForwardLink) {
				IndexWord _iw = dictionary.lookupIndexWord(POS.ADJECTIVE, s);
				if (_iw != null) {
					addAllLemmas(_iw, acceptedForwardLinkList);
				}
				_iw = dictionary.lookupIndexWord(POS.ADVERB, s);
				if (_iw != null) {
					addAllLemmas(_iw, acceptedForwardLinkList);
				}
			}

		} catch (JWNLException e) {
			e.printStackTrace();
		}
		System.out.println("Loaded WordNet in " + (System.currentTimeMillis() - _start) + "ms.");
	}

	/**
	 * @param wordNetDictionaryPath
	 * @param fileProperties
	 * @param scannerOldFile
	 * @param bufferNewFileProperties
	 * @throws IOException
	 */
	private static void writeNewPropertiesFile(String wordNetDictionaryPath, File fileProperties,
			Scanner scannerOldFile, StringBuffer bufferNewFileProperties) throws IOException {
		logger.info("Reading the Lines:");
		while (scannerOldFile.hasNextLine()) {
			String line = scannerOldFile.nextLine();
			if (line.contains("<param name=\"dictionary_path\" value=\"")) {
				line = "			<param name=\"dictionary_path\" value=\"" + wordNetDictionaryPath
						+ "\"/>";
				logger.info("The modified line ... ");
			}
			logger.debug(line);
			bufferNewFileProperties.append(line + System.lineSeparator());
		}

		logger.debug("Let have a look on what the buffer contains:\n" + bufferNewFileProperties.toString());

		String newFileContent = bufferNewFileProperties.toString();
		logger.debug("The new file content looks like:\n" + newFileContent);

		scannerOldFile.close();

		FileWriter newFilePropertiesFileWriter = new FileWriter(fileProperties);
		newFilePropertiesFileWriter.append(newFileContent);
		newFilePropertiesFileWriter.flush();
	}

	/**
	 * @param filePropertiesString
	 * @throws FileNotFoundException
	 */
	private static void readJwnlConfig(String filePropertiesString) throws FileNotFoundException {
		try {
			logger.debug("Trying to read " + filePropertiesString);
			JWNL.initialize(new FileInputStream(filePropertiesString));
			System.out.println("used new config");
		} catch (JWNLException e1) {
			System.out.println(
					"ERROR in config file: Try to delete the jwnl.configuration file and run again!");
			e1.printStackTrace();
		}
	}

	/**
	 * @return the acceptedForwardLinkList
	 */
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

	public static boolean isAnimate(String noun) {
		try {
			IndexWord _idw = dictionary.lookupIndexWord(POS.NOUN, noun);
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
			IndexWord _idw = dictionary.lookupIndexWord(POS.NOUN, fullNoun);
			if (_idw == null || (!_idw.getLemma().contains(mainNoun)))
				_idw = dictionary.lookupIndexWord(POS.NOUN, mainNoun);
			return checkHypernymTree(_idw, Constants.f_realActorDeterminers);
		} catch (JWNLException e) {
			e.printStackTrace();
		}
		return false;
	}

	public static boolean canBeGroupAction(String mainNoun) {
		try {
			IndexWord _idw = dictionary.lookupIndexWord(POS.NOUN, mainNoun);
			return checkHypernymTree(_idw, ListUtils.getList("group_action"));
		} catch (JWNLException e) {
			e.printStackTrace();
		}
		return false;
	}

	public static boolean isTimePeriod(String mainNoun) {
		try {
			IndexWord _idw = dictionary.lookupIndexWord(POS.NOUN, mainNoun);
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
					_idw = dictionary.lookupIndexWord(POS.VERB, _verb + " " + a.getMod());
				}
				if (_idw == null) {
					_idw = dictionary.lookupIndexWord(POS.VERB, _verb + " " + a.getPrt());
				}
				if (_idw == null) {
					_idw = dictionary.lookupIndexWord(POS.VERB, _verb);
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

	private static boolean checkHypernymTree(IndexWord idw, List<String> wordsToCHeck) throws JWNLException {
		if (idw != null) {
			// System.out.println("checking senses of: "+_idw.getLemma());
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
	private static boolean canBe(Synset s, List<String> lookFor, LinkedList<Synset> checked) throws JWNLException {
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
			IndexWord _idw = dictionary.lookupIndexWord(POS.NOUN, noun);
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

	/**
	 * @param _tt
	 */
	@SuppressWarnings("unused")
	private static void print(PointerTargetTree _tt) {
		if (_tt.getRootNode() != null) {
			System.out.println(_tt.getRootNode());
			_tt.getRootNode().getChildTreeList().print();
		}
	}

	/**
	 * tries to lookup the word in wordnet and if found return the lemma (base form)
	 * of the verb. if it is not found, the verb is returned unchanged.
	 * 
	 * @param verb
	 * @return
	 */
	public static String getBaseForm(String verb) {
		return getBaseForm(verb, true, POS.VERB);
	}

	/**
	 * tries to lookup the word in wordnet and if found return the lemma (base form)
	 * of the verb. if it is not found, the verb is returned unchanged.
	 * 
	 * @param verb
	 * @return
	 */
	public static String getBaseForm(String verb, boolean keepAuxiliaries, POS pos) {
		String[] _parts = verb.split(" "); // verb can contain auxiliary verbs
											// (to acquire)
		try {
			IndexWord word = dictionary.lookupIndexWord(pos, _parts[_parts.length - 1]);
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
		} catch (JWNLException e) {
			e.printStackTrace();
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
				IndexWord _idw = dictionary.lookupIndexWord(POS.NOUN, fullNoun);
				if (_idw == null || (!_idw.getLemma().contains(noun)))
					_idw = dictionary.lookupIndexWord(POS.NOUN, noun);
				return checkHypernymTree(_idw, Constants.f_metaActorsDeterminers);
			} catch (JWNLException e) {
				e.printStackTrace();
			}
		}
		return false;
	}

	/**
	 * compares the given verb and all synonyms/hypernyms with the type word this
	 * way it can be checked if, e.g., a verb is of type "end" or "finish".
	 * 
	 * @param verb
	 * @param type
	 * @return
	 */
	public static boolean isVerbOfType(String verb, String type) {
		try {
			IndexWord _idw = dictionary.lookupIndexWord(POS.VERB, verb);
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
			IndexWord _idw = dictionary.lookupIndexWord(POS.NOUN, fullNoun);
			if (_idw == null || (!_idw.getLemma().contains(noun)))
				_idw = dictionary.lookupIndexWord(POS.NOUN, noun);
			return checkHypernymTree(_idw, Constants.f_dataObjectDeterminers);
		} catch (JWNLException e) {
			e.printStackTrace();
		}
		return false;
	}

	public static Dictionary getDictionary() {
		if (dictionary == null) {
			initDictionary();
		}
		return dictionary;
	}

	public static void initDictionary() {
		WordNetWrapper.dictionary = Dictionary.getInstance();
	}

}
