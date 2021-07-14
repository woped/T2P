/**
 * modified taken from https://github.com/FabianFriedrich/Text2Process
 */
package de.dhbw.text2process.models.textModel;

import java.util.ArrayList;

import de.dhbw.text2process.models.bpmn.SequenceFlow;
import de.dhbw.text2process.models.bpmn.nodes.Cluster;
import de.dhbw.text2process.models.bpmn.nodes.ProcessNode;


public class SentenceNode extends Cluster {
	
	private ArrayList<WordNode> f_wordNodes = new ArrayList<WordNode>();
	private int f_index = 0;

	/**
	 * 
	 */
	public SentenceNode() {

	}
	
	/**
	 * @param index 
	 * 
	 */
	public SentenceNode(int index) {
		f_index = index;		
	}
	
	public void addWord(WordNode word){
		f_wordNodes .add(word);
		super.addProcessNode(word);
	}

	/**
	 * @return
	 */
	public int getIndex() {
		return f_index;
	}
	
	@Override
	public void removeProcessNode(ProcessNode n) {
		//not possible its a build only model
	}

	@Override
	public void setIncoming(SequenceFlow flow) {
		throw new IllegalArgumentException("not implemented yet!");
	}

	@Override
	public void setOutgoing(SequenceFlow flow) {
		throw new IllegalArgumentException("not implemented yet!");
	}

}
