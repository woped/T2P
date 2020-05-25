/**
 * modified taken from https://github.com/FabianFriedrich/Text2Process
 */
package de.dhbw.WoPeDText2Process.processors.worldmodel.transform;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


import de.dhbw.WoPeDText2Process.processors.worldmodel.Constants;
import de.dhbw.WoPeDText2Process.processors.worldmodel.processing.ProcessingUtils;
import de.dhbw.WoPeDText2Process.wrapper.FrameNetFunctionality;
import de.dhbw.WoPeDText2Process.wrapper.WordNetFunctionality;
import de.dhbw.WoPeDText2Process.enums.SpecifierType;
import de.dhbw.WoPeDText2Process.models.worldModel.*;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.IndexedWord;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.TypedDependency;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ElementsBuilder {

	static Logger logger = LoggerFactory.getLogger(ElementsBuilder.class);
    /**
     * Creates an Actor object out of the extracted subject
     * @param f_root Tree structure of the whole sentence with the root node included
     * @param origin original T2P sentence
     * @param fullSentence Tree structure of the whole sentence without the root node
     * @param determinedActor ActorWord that had been extracted from the sentence
     * @param dependencies Collection of all TypedDependencies of the sentence
     * @return created Actor _a
     */
	public static Actor createActor(Tree f_root, T2PSentence origin, List<Tree> fullSentence, IndexedWord determinedActor, Collection<TypedDependency> dependencies) {
		logger.info("Creating an actor for: " + determinedActor.value());
		Actor actor = null;
		logger.debug("");
		Tree node = fullSentence.get(determinedActor.index()-1);
		logger.debug("Extract the noun and its dependecies");
		String fullNoun = getFullNoun(determinedActor, dependencies);
		WordNetFunctionality wnf = new WordNetFunctionality();
		logger.debug("Check whether noun is a person or a system ...");
		if(!wnf.canBePersonOrSystem(fullNoun, node.value().toLowerCase())) {
			//try to extract the real actor here?
			if(node.parent(f_root).value().equals("CD") || wnf.canBeGroupAction(node.value())) { //one of the physicians
				List<TypedDependency> _preps = SearchUtils.findDependency("prep", dependencies);
				for(TypedDependency spec: _preps) {
					if(Constants.f_realActorPPIndicators.contains(spec.reln().getSpecific()) && spec.gov().equals(node)) {
						//possible candidate of the real actor
						fullNoun = getFullNoun(spec.dep(), dependencies);
						if(wnf.canBePersonOrSystem(fullNoun,spec.dep().value())) {
							actor = createActorInternal(f_root, origin, fullSentence, spec.dep(),dependencies);
							break;
						}
					}							
				}
			}
			if(actor == null) {
				actor = createActorInternal(f_root, origin, fullSentence, determinedActor,dependencies);
				actor.setUnreal(true);
			}
		}else {
			logger.debug("Noun represents a person or system, start creating an internal Actor");
			actor = createActorInternal(f_root, origin, fullSentence, determinedActor, dependencies);
		}
		if(Constants.DEBUG_EXTRACTION) System.out.println("Identified actor: "+actor);
		return actor;
	}

    /**
     * Creates an Actor object out of the extracted subject
     * @param f_root Tree structure of the whole sentence with the root node included
     * @param origin original T2P sentence
     * @param fullSentence Tree structure of the whole sentence without the root node
     * @param determinedActor ActorWord that had been extracted from the sentence
     * @param dependencies Collection of all TypedDependencies of the sentence
     * @return created Actor _a
     */
	private static Actor createActorInternal(Tree f_root, T2PSentence origin, List<Tree> fullSentence, IndexedWord determinedActor,
			Collection<TypedDependency> dependencies) {
		logger.info("Creating an actor for: " + determinedActor.value());
		Actor _a = new Actor(origin,determinedActor.index(),determinedActor.value().toLowerCase());
		logger.debug("Instantiate WordNetFunctionality ...");
		WordNetFunctionality wnf = new WordNetFunctionality();
		logger.debug("Determine noun specifiers ...");
		determineNounSpecifiers(f_root, origin, fullSentence, determinedActor, dependencies, _a);
		logger.debug("whether it is a meta actor or not ...");
		if(wnf.isMetaActor(getFullNoun(determinedActor, dependencies),determinedActor.value())) {
			logger.debug("Set metaActor attribute on Actor instance = true");
			_a.setMetaActor(true);
		}
		logger.debug("Returning internal actor: " + _a);
		return _a;
	}

    /**
     * Creates an Action object out of the extracted subject
     * @param f_root Tree structure of the whole sentence with the root node included
     * @param origin original T2P sentence
     * @param fullSentence Tree structure of the whole sentence without the root node
     * @param node Treenode of the extracted verb
     * @param dependencies Collection of all TypedDependencies of the sentence
     * @param active indicates if the sentence is a passive or active sentence
     * @return created Action _result
     */
	public static Action createAction(T2PSentence origin, List<Tree> fullSentence, Tree node, Collection<TypedDependency> dependencies, boolean active, Tree f_root) {
	    logger.info("Creating an BPMN action object ...");
	    logger.debug("Instantiating a CoreLabel for the verb");
		CoreLabel verbLabel = (CoreLabel) node.label();
		logger.debug("Setting the node value for the verb lemma");
	    node.setValue(verbLabel.lemma());
	    logger.debug("Instantiating an action object ...");
	    Action result = new Action(origin,verbLabel.index(),node.value());

		logger.debug("Searching for an auxiliary verb ...");
		String auxiliaries = getAuxiliaries(node, dependencies);

		logger.debug("Check if auxiliary verbs are found");
		if(auxiliaries.length() > 0) {
			logger.debug("\t-> true: Setting auxiliaries on result action");
			result.setAux(auxiliaries);
		}

		logger.debug("Instantiating an IndexedWord object for modifiers");
		IndexedWord modifiers = getModifiers(node, dependencies);
		logger.debug("Check if modifiers are not null");
		if(modifiers != null) {
			logger.debug("\t-> true: Setting modifiers on result action");
			result.setMod(modifiers.value());
			result.setModPos(modifiers.index());
		}

		logger.debug("Setting negated state on result action");
		result.setNegated(isNegated(node,dependencies));

		logger.debug("Instantiating IndexedWord for cop");
		IndexedWord cop = getCop(node, dependencies);
		logger.debug("Checking if cop is not null");
		if(cop != null) {
			logger.debug("\t-> true: Setting cop on result action");
			result.setCop(cop.value(),cop.index());
		}

		logger.debug("Instantiating a string for prt");
		String prt = getPrt(node, dependencies);
		logger.debug("Checking if prt string is longer than 0");
		if(prt.length() > 0) {
			logger.debug("\t-> true: Setting prt on result action");
			result.setPrt(prt);
		}

		logger.debug("Instantiating a IndexedWord object for indexObjects");
		IndexedWord iObj = getIObj(node, dependencies);
		logger.debug("Checking if iObj is not null");
		if(iObj != null) {
			logger.debug("\t-> true: Instantiating a Tree object and filling it with information from fullSentence");
            Tree iobjNode = fullSentence.get(iObj.index()-1);
            logger.debug("\tInstatiating a Specifier object based on origin, iObj and iobjNodes leaves");
			Specifier specifier = new Specifier(origin, iObj.index(), PrintUtils.toString(iobjNode.getLeaves()));
			logger.debug("\tSetting specifier type to IOBJ");
			specifier.setSpecifierType(SpecifierType.IOBJ);
			logger.debug("\tAdding specifier to result action");
			result.addSpecifiers(specifier);
		}

		logger.debug("Checking if active parameter is false");
		if(!active) {
			logger.debug("\t-> true: calling checkDobj(node, dependencies, result, origin, fullSentence)");
			checkDobj(node, dependencies, result, origin, fullSentence);
		}

		logger.debug("Instantiating a list object and filling it with TypedDependency");
		List<TypedDependency> typedDependencies = SearchUtils.findDependency(ListUtils.getList("xcomp","dep"), dependencies);
		// TODO: typedDependencies are empty, loop does not do anything.
		logger.debug("Iterating over the typedDependencies");
		for(TypedDependency typedDependency:typedDependencies) {
			logger.debug("\tFound typedDependency: " + typedDependencies.toString());
			logger.debug("\tCheck if the typedDependency.gov() equals the node parameter");
			if(typedDependency.gov().equals(node)) {
				logger.debug("\t\t-> true: Intantiating a Tree object for a xcompNode");
				Tree xcompNode = null;
				logger.debug("\t\tCheck if relName of typedDependency is dep");
				if(typedDependency.reln().getShortName().equals("dep")) {
					logger.debug("\t\t\t-> true: extracting xcompNode from fullSentence parameter");
					//only consider verbs and forwards dependencies
					xcompNode = fullSentence.get(typedDependency.dep().index()-1);
					logger.debug("\t\t\tCheck if the parent entry does not start with the character V or the typedDependenxy dep index is smaller than the gov index");
					if(!xcompNode.parent(f_root).value().startsWith("V") || (typedDependency.dep().index()<typedDependency.gov().index())) {
						logger.debug("\t\t\t\t-> true: continue with next typedDependency");
						continue;
					}
				}
				logger.debug("\tInstantiate a new action object for the xcomp using origin, fullSentence, xcompNode, dependencies, true, f_root");
				Action xcomp = createAction(origin, fullSentence, xcompNode, dependencies, true, f_root);
				logger.debug("\tSetting the xcomp on the result action");
				result.setXcomp(xcomp);
				logger.debug("\tLeaving the loop over the typedDependencies, now");
				break;
			}
		}

		//extracting further information and specifiers
		Tree fullPhraseTree = SearchUtils.getFullPhraseTree("VP", node, f_root);
		extractSBARSpecifier(origin, fullSentence, result, fullPhraseTree,node);
		extractPPSpecifier(origin, fullSentence, result, node, dependencies, f_root);
		extractRCMODSpecifier(origin, result, node, dependencies, fullSentence, f_root);
		if(Constants.DEBUG_EXTRACTION) System.out.println("Identified Action: "+result);
		result.setBaseForm(node.value());
		return result;
	}

    /**
     * Checks sentence for dobj dependencies
     * @param origin original T2P sentence
     * @param fullSentence Tree structure of the whole sentence without the root node
     * @param node Treenode of the extracted verb
     * @param dependencies Collection of all TypedDependencies of the sentence
     */
	private static void checkDobj(Tree node, Collection<TypedDependency> dependencies,Action result,T2PSentence origin, List<Tree> fullSentence) {
		List<String> _lookFor = ListUtils.getList("dobj");
		List<TypedDependency> _toCheck = SearchUtils.findDependency(_lookFor,dependencies);
		for(TypedDependency td:_toCheck) {
			if(td.gov().equals(node)) {
				System.err.println("Hey we found a dobj in a passive sentence!!!"+td);
				Specifier _sp = new Specifier(origin,td.dep().index(),getFullNoun(td.dep(), dependencies));
				_sp.setSpecifierType(SpecifierType.DOBJ);
				ExtractedObject _obj = ElementsBuilder.createObject(node, origin, fullSentence, td.dep(), dependencies);
				_sp.setObject(_obj);
				result.addSpecifiers(_sp);
			}
		}		
	}

	/**Checks sentence for cop dependencies
	 * @param node Treenode of the extracted verb
	 * @param dependencies Collection of all TypedDependencies of the sentence
	 * @return IndexedWord of the node that has a cop dependency to the node
	 */
	private static IndexedWord getCop(Tree node, Collection<TypedDependency> dependencies) {
		List<TypedDependency> _toCheck = SearchUtils.findDependency(ListUtils.getList("cop"),dependencies);
		for(TypedDependency td:_toCheck) {
			if(td.dep().equals(node)) {
				//found something
				return td.gov();					
			}
		}
		return null;
	}
	
	/**
	 * @param node Treenode of the extracted verb
	 * @param dependencies Collection of all TypedDependencies of the sentence
	 * @return
	 */
	private static String getPrt(Tree node, Collection<TypedDependency> dependencies) {
		List<String> _lookFor = ListUtils.getList("prt");
		return findDependants(node, dependencies, _lookFor,true);	
	}
	
	/**
	 * @param node
	 * @param dependencies
	 * @return
	 */
	private static IndexedWord getIObj(Tree node, Collection<TypedDependency> dependencies) {
		List<TypedDependency> _toCheck = SearchUtils.findDependency(ListUtils.getList("iobj"),dependencies);
		for(TypedDependency td:_toCheck) {
			if(td.dep().equals(node)) {
				//found something
				return td.gov();					
			}
		}
		return null;	
	}

	/**
	 * cop(case-6, is-3)
	 * neg(case-6, not-4)
	 * @param node
	 * @param dependencies
	 * @return
	 */
	private static boolean isNegated(Tree node,Collection<TypedDependency> dependencies) {
		IndexedWord _indexedWord = new IndexedWord(node.label());
		//setting node to the object in case of a cop sentence (see example in documentation)
		List<TypedDependency> _toCheck = SearchUtils.findDependency("cop",dependencies);
		for(TypedDependency td:_toCheck) {
			if(td.dep().equals(_indexedWord)){
				_indexedWord = td.gov();
				break;
			}
		}
		_toCheck = SearchUtils.findDependency("neg",dependencies);
		for(TypedDependency td:_toCheck) {
			if(td.gov().equals(_indexedWord)) {
				return true;
			}
		}		
		return false;
	}

    /**
     *
     * @param node
     * @param dependencies
     * @return
     */
	private static String getAuxiliaries(Tree node, Collection<TypedDependency> dependencies) {
		List<String> _lookFor = ListUtils.getList("aux","auxpass");
		return findDependants(node, dependencies, _lookFor,true);
	}

    /**
     *
     * @param node
     * @param dependencies
     * @param lookFor
     * @param isGovernor
     * @return
     */
	private static String findDependants(Tree node,Collection<TypedDependency> dependencies, List<String> lookFor,boolean isGovernor) {
		List<TypedDependency> _toCheck = SearchUtils.findDependency(lookFor,dependencies);
		StringBuilder _b = new StringBuilder();
		for(TypedDependency td:_toCheck) {
			if(isGovernor) {
				if(td.gov().equals(node)) {
					//found something
					_b.append(td.dep().value());
					_b.append(" ");
				}
			}else {
				if(td.dep().equals(node)) {
					//found something
					_b.append(td.gov().value());
					_b.append(" ");
				}	
			}
		}
		if(_b.length() > 0)_b.deleteCharAt(_b.length()-1);
		return _b.toString();
	}

    /**
     *
     * @param node
     * @param dependencies
     * @return
     */
	private static IndexedWord getModifiers(Tree node, Collection<TypedDependency> dependencies) {
		List<TypedDependency> _toCheck = SearchUtils.findDependency(ListUtils.getList("advmod","acomp"),dependencies);
		for(TypedDependency td:_toCheck) {
			if(td.gov().equals(node)) {
				//only take following modifiers as other are adverbs (e.g. quickly walk vs. walk >out<)
				if(td.gov().index() < td.dep().index() && !Constants.f_sequenceIndicators.contains(td.dep().value())) {
					//found something
					return td.dep();			
				}
			}
		}
		return null;
	}
	
	public static Action createActionSyntax(T2PSentence origin, List<Tree> fullSentence, Tree vpHead, boolean active) {
		List<Tree> _verbParts = extractVerbParts(vpHead,active);
		int index = 0;
		if(vpHead.getLeaves().get(0) instanceof Tree) {
			index = ((Tree)vpHead.getLeaves().get(0)).objectIndexOf(vpHead);
		}else {
			index = SearchUtils.getIndex(fullSentence, vpHead.getLeaves());
		}		
		Action _result = new Action(origin,index,PrintUtils.toString(_verbParts));
		WordNetFunctionality wnf = new WordNetFunctionality();
		_result.setBaseForm(wnf.getBaseForm(PrintUtils.toString(_verbParts)));
		//extracting further information and specifiers
		extractSBARSpecifier(origin, fullSentence, _result, vpHead,null);		
		//determineLinkedActions
		extractPPSpecifierSyntax(origin, fullSentence, _result, vpHead);	
		if(Constants.DEBUG_EXTRACTION) System.out.println("Identified Action: "+_result);	
		return _result;
	}
	
	/**
	 * @param node
	 * @param active
	 * @return
	 */
	private static List<Tree> extractVerbParts(Tree node, boolean active) {
		ArrayList<Tree> _result = new ArrayList<Tree>();
		if((node.isLeaf())) {
			_result.add(node);
		}else {
			for(Tree t:node.children()) {
				if(!t.value().equals("SBAR") && !t.value().equals("NP") && !t.value().equals("ADJP") && !t.value().equals("ADVP") && !t.value().equals("PRN")) {
					if(!(node.value().equals("PP"))) {
						_result.addAll(extractVerbParts(t,active));
					}
				}
			}
		}
		return _result;
	}
	
	/**
	 * creates a new specified elements which can either be a Resource
	 * or an Actor
	 * @param origin
	 * @param fullSentence
	 * @param indexedWord
	 * @param dependencies
	 * @return ExtractedObject
	 */
	public static ExtractedObject createObject(Tree f_root, T2PSentence origin, List<Tree> fullSentence, IndexedWord indexedWord,Collection<TypedDependency> dependencies) {
		String _fullNoun = getFullNoun(indexedWord, dependencies);
		WordNetFunctionality wnf = new WordNetFunctionality();
		if(wnf.canBePersonOrSystem(_fullNoun, indexedWord.value().toLowerCase()) || ProcessingUtils.canBePersonPronoun(indexedWord.value())) {
			Actor _a = createActorInternal(f_root, origin, fullSentence, indexedWord, dependencies);
			_a.setSubjectRole(false);			
			if(Constants.DEBUG_EXTRACTION) System.out.println("Identified object: "+_a);
			return _a;
		}
		Resource _r = new Resource(origin,indexedWord.index(),indexedWord.value().toLowerCase());
		_r.setSubjectRole(false);
		determineNounSpecifiers(f_root, origin, fullSentence, indexedWord, dependencies, _r);
		
		if(Constants.DEBUG_EXTRACTION) System.out.println("Identified object: "+_r);
		return _r;				
	}

	private static String getFullNoun(IndexedWord indexedWord, Collection<TypedDependency> dependencies) {
		List<TypedDependency> _toCheck = SearchUtils.findDependency(ListUtils.getList("nn","dep"),dependencies);
		//extracting full compound name
		StringBuilder _builder = new StringBuilder();
		StringBuilder _addAfter = new StringBuilder();
		if(_toCheck.size() > 0) {
			for(TypedDependency td:_toCheck) {
				if(td.gov().equals(indexedWord)) {
					if(td.reln().getShortName().equals("dep")) {
						if(td.gov().index()+1 !=  td.dep().index()) {
							continue; //skip this one
						}
						_addAfter.append(' ');
						_addAfter.append(td.dep().value());						
					}else {
						_builder.append(td.dep().value());
						_builder.append(' ');
					}
				}
			}
		}
		_builder.append(indexedWord.value());
		_builder.append(_addAfter.toString());
		String fullNoun = _builder.toString().toLowerCase();
		return fullNoun;
	}
	
	private static void determineNounSpecifiers(Tree f_root, T2PSentence origin,
			List<Tree> fullSentence, IndexedWord indexedWord,
			Collection<TypedDependency> dependencies, ExtractedObject element) {
	    Tree node = fullSentence.get(indexedWord.index()-1);
		findDeterminer(indexedWord, dependencies, element);
		findAMODSpecifiers(origin, indexedWord, dependencies, element);
		findNNSpecifiers(origin, indexedWord, dependencies, element);
		findINFMODSpecifiers(origin, node, dependencies, element);
		getPARTMODSpecifiers(origin, indexedWord, dependencies, element, fullSentence, f_root);
		getSpecifierFromDependencies(origin,indexedWord,dependencies,element,"num",SpecifierType.NUM);
		
		//extracting further information and specifiers
		Tree _tree = SearchUtils.getFullPhraseTree("NP", node, f_root);
		extractSBARSpecifier(origin, fullSentence, element, _tree, node);
		extractPPSpecifier(origin, fullSentence, element, node, dependencies, f_root);
		if(Constants.f_relativeResolutionTags.contains(node.parent(f_root).value()) ||
				Constants.f_relativeResolutionWords.contains(node.value())) {
			if(node.parent(f_root).parent(f_root).children().length == 1) {
				for(Specifier spec:element.getSpecifiers(SpecifierType.PP)) {
					if("of".equals(spec.getHeadWord())) {
						return;
					}
				}
				element.setResolve(true);	
			}
		}
	}

	

	/**
	 * @param origin
	 * @param node
	 * @param dependencies
	 * @param element
	 */
	private static void findINFMODSpecifiers(T2PSentence origin,Tree node, Collection<TypedDependency> dependencies,
			SpecifiedElement element) {
		List<TypedDependency> _toCheck = SearchUtils.findDependency("infmod",dependencies);
		if(_toCheck.size() > 0) {
			StringBuilder _builder = new StringBuilder();
			for(TypedDependency td:_toCheck) {
				if(td.gov().equals(node)) {					
					//found it
					_toCheck = SearchUtils.findDependency(ListUtils.getList("aux","cop","neg"),dependencies);
					for(TypedDependency acn:_toCheck) {
						if(acn.gov().equals(td.dep())) {
							_builder.append(acn.dep().value());
							_builder.append(" ");
						}
					}
					_builder.append(td.dep().value());
					Specifier _sp = new Specifier(origin,td.dep().index(),_builder.toString());
					_sp.setSpecifierType(SpecifierType.INFMOD);
					element.addSpecifiers(_sp);
					break;				
				}
			}			
		}
	}
	
	
	/**
	 * @param origin
	 * @param indexedWord
	 * @param dependencies
	 * @param element
	 */
	private static void getPARTMODSpecifiers(T2PSentence origin, IndexedWord indexedWord, Collection<TypedDependency> dependencies,
			ExtractedObject element, List<Tree> fullSentence, Tree f_root) {
		List<TypedDependency> _toCheck = SearchUtils.findDependency("partmod",dependencies);
		if(_toCheck.size() > 0) {
			for(TypedDependency td:_toCheck) {
				if(td.gov().equals(indexedWord)) {
				    Tree node = fullSentence.get(td.dep().index()-1);
					String _phr = SearchUtils.getFullPhrase("VP", node, f_root);
					//found it					
					Specifier _sp = new Specifier(origin,td.dep().index(),_phr);
					_sp.setSpecifierType(SpecifierType.PARTMOD);
					element.addSpecifiers(_sp);
					break;				
				}
			}			
		}
	}

	private static void extractPPSpecifierSyntax(T2PSentence origin,
			List<Tree> fullSentence, SpecifiedElement element, Tree fullPhrase) {
		//search for a PP determiner
		List<Tree> _ppList = getPPSpecifierSyntax(fullPhrase);
		for(Tree pp:_ppList){
			Specifier _sp = new Specifier(origin,SearchUtils.getIndex(fullSentence, _ppList),PrintUtils.toString(pp));
			_sp.setSpecifierType(SpecifierType.PP);
			element.addSpecifiers(_sp);
		}
	}
	
	private static void extractPPSpecifier(T2PSentence origin, List<Tree> fullSentence, SpecifiedElement element,Tree node, Collection<TypedDependency> dependencies, Tree f_root) {
		//search for a PP determiner
		List<TypedDependency> _toCheck = SearchUtils.findDependency(ListUtils.getList("prep","prepc"),dependencies);
		List<TypedDependency> _rcMod = SearchUtils.findDependency(ListUtils.getList("rcmod"),dependencies);
		for(TypedDependency td:_toCheck) {
			String _cop = null;
			if(element instanceof Action) {
				Action _act = (Action)element;
				_cop = _act.getCop();
			}
			if((td.gov().equals(node) || td.gov().value().equals(_cop)) && !partOfrcMod(_rcMod, td, fullSentence, f_root)) {
				//found something
                Tree DepNode = fullSentence.get(td.dep().index()-1);
				Tree _phraseTree = SearchUtils.getFullPhraseTree("PP", DepNode, f_root);
				if(!_phraseTree.parent().value().equals("PRN")) {
					_phraseTree = deleteBranches(ListUtils.getList("S","SBAR"),_phraseTree);
					String _phrase = PrintUtils.toString(_phraseTree);
					String _specific = null;
					if(_phrase.indexOf(' ') >= 0) { //does not have to S(interact ...) 
						if(td.reln().getSpecific() != null) {// ... is reviewed, resulting in.... (BPMN MRG Ex5)
							_phrase = _phrase.substring(_phrase.indexOf(' ')); //cutting of first word (is included in getSpecific)
							_specific = td.reln().getSpecific().replace('_', ' ');
							_phrase = _specific+_phrase;
						}
						Specifier _sp = new Specifier(origin,td.dep().index(),_phrase);
						_sp.setSpecifierType(SpecifierType.PP);
						Tree TDnode = fullSentence.get(td.dep().index()-1);
						if(TDnode.parent().parent().value().startsWith("NP")) {
							ExtractedObject _object = createObject(node, origin, fullSentence, td.dep(), dependencies);
							_sp.setObject(_object);	
							//TODO add conjunct elements							
						}
						_sp.setHeadWord(_specific);
						FrameNetFunctionality fnf = new FrameNetFunctionality();
						fnf.determineSpecifierFrameElement(element, _sp);
						element.addSpecifiers(_sp);				
				}
				}
			}
		}
	}

    /**
     *
     * @param origin
     * @param element
     * @param node
     * @param dependencies
     * @param fullSentence
     * @param f_root
     */
	private static void extractRCMODSpecifier(T2PSentence origin, SpecifiedElement element,Tree node, Collection<TypedDependency> dependencies, List<Tree> fullSentence, Tree f_root) {
		//search for a rcmod determiner
		List<TypedDependency> _toCheck = SearchUtils.findDependency(ListUtils.getList("rcmod"),dependencies);
		for(TypedDependency td:_toCheck) {
			String _cop = null;
			if(element instanceof Action) {
				Action _act = (Action)element;
				_cop = _act.getCop();
			}
			if(td.dep().equals(node) || td.dep().value().equals(_cop)) {				
				//found something
				Tree GovNode = fullSentence.get(td.gov().index()-1);
                Tree _phraseTree = SearchUtils.getFullPhraseTree("PP", GovNode, f_root);
				if(_phraseTree != null) {//it was not a PP, but e.g. an SBAR
					_phraseTree = deleteBranches(ListUtils.getList("S","SBAR"),_phraseTree);
					String _phrase = PrintUtils.toString(_phraseTree);
					Specifier _sp = new Specifier(origin,td.dep().index(),_phrase);
					_sp.setSpecifierType(SpecifierType.RCMOD);
					element.addSpecifiers(_sp);
				}
			}
		}
	}

	private static boolean partOfrcMod(List<TypedDependency> _rcMod,TypedDependency td, List<Tree> fullSentence, Tree f_root) {
		for(TypedDependency rcm:_rcMod) {
			if(rcm.gov().equals(td.dep())) {
			    Tree DepNode = fullSentence.get(td.dep().index()-1);
				Tree _phraseTree = SearchUtils.getFullPhraseTree("PP", DepNode, f_root);
				_phraseTree = deleteBranches(ListUtils.getList("S","SBAR"),_phraseTree);
				String _phrase = PrintUtils.toString(_phraseTree).toLowerCase();
				if(Constants.f_conditionIndicators.contains(_phrase)) {
					return true;
				}
			}
		}
		return false;
	}
	
	/**
	 * @param list
	 * @param input
	 */
	private static Tree deleteBranches(List<String> list, Tree input) {
		Tree _result = input.deepCopy();
		deleteBranchesInternal(list, _result);
		return _result;
	}
	
	/**
	 * only pass copies in here!
	 * @param list
	 * @param tree
	 */
	private static void deleteBranchesInternal(List<String> list, Tree tree) {
		for(int i=0;i<tree.children().length;i++) {
			Tree t=tree.children()[i];
			if(list.contains(t.value())) {
				ProcessingUtils.removeChild(tree, i);
				i--;
			}else {
				deleteBranches(list, t);
			}
		}
	}

	private static List<Tree> getPPSpecifierSyntax(Tree fullPhrase) {
		ArrayList<String> _excludes = new ArrayList<String>();
		_excludes.add("SBAR");
		_excludes.add("S");
		_excludes.add("NP");
		_excludes.add("PRN");
		//search for a PP determiner
		return SearchUtils.find("PP", fullPhrase,_excludes);
		
	}

	private static void extractSBARSpecifier(T2PSentence origin,List<Tree> fullSentence, SpecifiedElement element, Tree phraseHead, Tree node) {
		//search for an SBAR determiner
		ArrayList<String> _excludes = new ArrayList<String>();
		List<Tree> _sbarList = SearchUtils.find("SBAR", phraseHead,_excludes);
		for(Tree sbar:_sbarList){
			Tree _sbarNode = sbar.getLeaves().get(0);
			Tree _phraseNode = phraseHead.getLeaves().get(0);
			int idx1=0;
			int idx2=0;
			if(_sbarNode instanceof Tree && node != null) {
				idx1 = ((Tree)sbar.getLeaves().get(0)).objectIndexOf(node);
				idx2 = origin.indexOf(node);
			}else {
				idx1 = SearchUtils.getIndex(fullSentence, _sbarNode.getLeaves());
				idx2 = SearchUtils.getIndex(fullSentence, _phraseNode.getLeaves());
			}			
			if( idx1 > idx2) {
				Specifier _sp = new Specifier(origin,SearchUtils.getIndex(fullSentence, sbar.getLeaves()),PrintUtils.toString(sbar));
				_sp.setSpecifierType(SpecifierType.SBAR);
				element.addSpecifiers(_sp);
			}
		}
	}
	
	/**
	 * @param origin
	 * @param indexedWord
	 * @param dependencies
	 * @param element
	 */
	private static void findAMODSpecifiers(T2PSentence origin,
			IndexedWord indexedWord, Collection<TypedDependency> dependencies,
			SpecifiedElement element) {
		getSpecifierFromDependencies(origin, indexedWord, dependencies, element, "amod", SpecifierType.AMOD);
	}

	private static void findNNSpecifiers(T2PSentence origin,IndexedWord indexedWord, Collection<TypedDependency> dependencies,
			SpecifiedElement element) {
		getSpecifierFromDependencies(origin, indexedWord, dependencies, element,"nn",SpecifierType.NN);
		List<TypedDependency> _toCheck = SearchUtils.findDependency("dep",dependencies);
		//extracting compound names which were only recognized as dependencies
		if(_toCheck.size() > 0) {
			for(TypedDependency td:_toCheck) {
				if(td.gov().equals(indexedWord)) {
					if(td.gov().index()+1 !=  td.dep().index()) {
						continue; //skip this one
					}					
					Specifier _sp = new Specifier(origin,td.dep().index(),td.dep().value().toLowerCase());
					_sp.setSpecifierType(SpecifierType.NNAFTER);
					element.addSpecifiers(_sp);
				}
			}
			
		}
	}

	private static void getSpecifierFromDependencies(T2PSentence origin,
			IndexedWord indexedWord, Collection<TypedDependency> dependencies,
			SpecifiedElement element, String depType, SpecifierType specifierType) {
		//search for specifiers
		List<TypedDependency> _toCheck = SearchUtils.findDependency(depType,dependencies);
		int _index = -1;
		if(_toCheck.size() > 0) {
			StringBuilder _builder = new StringBuilder();
			for(TypedDependency td:_toCheck) {
				if(td.gov().equals(indexedWord)) {
					_builder.append(td.dep().value());
					_builder.append(' ');
					List<TypedDependency> _toCheck2 = SearchUtils.findDependency("conj",dependencies);
					for(TypedDependency td2:_toCheck2) {
						if(td2.gov().equals(td.dep())) {
							//found a conjunction -> also add this one
							_builder.append(td2.reln().getSpecific());
							_builder.append(' ');
							_builder.append(td2.dep().value());
							_builder.append(' ');
						}
					}
					if(_index == -1) _index = td.dep().index();
				}
			}
			if(_index != -1) {
				_builder.deleteCharAt(_builder.length()-1);
				Specifier _sp = new Specifier(origin,_index,_builder.toString());
				_sp.setSpecifierType(specifierType);
				element.addSpecifiers(_sp);
			}
		}
	}
	

	private static void findDeterminer(IndexedWord indexedWord,Collection<TypedDependency> dependencies, ExtractedObject _r) {
		//search for a determiner/article etc.
		List<TypedDependency> _toCheck = new ArrayList<TypedDependency>();
		_toCheck.addAll(SearchUtils.findDependency("poss",dependencies));
		_toCheck.addAll(SearchUtils.findDependency("det",dependencies));
		for(TypedDependency td:_toCheck) {
			if(td.gov().equals(indexedWord)) {
				//found something
				_r.setDeterminer(td.dep().value());
				break;
			}
		}
	}

	

}
