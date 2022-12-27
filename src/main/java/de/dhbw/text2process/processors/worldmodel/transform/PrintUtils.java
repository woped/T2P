/**
 * modified taken from https://github.com/FabianFriedrich/Text2Process
 */
package de.dhbw.text2process.processors.worldmodel.transform;

import java.util.ArrayList;
import java.util.List;

import de.dhbw.text2process.models.worldModel.Action;
import edu.stanford.nlp.process.PTBTokenizer;
import edu.stanford.nlp.trees.Tree;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * provides utilities for textual output
 *
 */
public class PrintUtils {

	static Logger logger = LoggerFactory.getLogger(PrintUtils.class);
	
	/**
	 * @param nodes
	 * @return
	 */
	public static String toString(List<Tree> nodes) {
		List<String> _result = new ArrayList<String>(nodes.size());
		for(Tree t:nodes) {
			for(Tree leaf: t.getLeaves()) {
				_result.add(leaf.value());
			}
		}
		return PTBTokenizer.ptb2Text(_result);
	}
	
	public static String toString(Tree node) {
		return toString(node.getLeaves());
	}
	
	/**
	 * @param analyzedSentence
	 */
	public static void printExtractedActions(AnalyzedSentence analyzedSentence) {
		logger.info("finally identifed actions in ("+PrintUtils.toString(analyzedSentence.getBaseSentence().getTree())+")");
		for(Action ac:analyzedSentence.getExtractedActions()) {
			logger.info("----------------");
			logger.info(ac.toFullString());
		}
		logger.info("+++++++++++++++++++++++++++++++++++++++++++++++");
	}

}
