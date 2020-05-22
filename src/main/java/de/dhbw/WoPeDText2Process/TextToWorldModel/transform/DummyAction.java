/**
 * modified taken from https://github.com/FabianFriedrich/Text2Process
 */
package de.dhbw.WoPeDText2Process.TextToWorldModel.transform;

import de.dhbw.WoPeDText2Process.petrinet.processors.IDHandler;
import de.dhbw.WoPeDText2Process.worldModel.Action;

public class DummyAction extends Action {

	/**
	 * @param action 
	 * @param origin
	 * @param wordInSentece
	 * @param verb
	 */

	private int dummyID;


	public DummyAction(Action action, IDHandler dummyIDHandler) {
		super(action.getOrigin(), action.getWordIndex()+1, "Dummy Node");
		setBaseForm("Dummy Node");
		dummyID=dummyIDHandler.getNext();
	}

	public DummyAction(IDHandler dummyIDHandler) {
		super(null, -1, "Dummy Node");
		setBaseForm("Dummy Node");
		dummyID=dummyIDHandler.getNext();
	}

	public int getDummyID() {
		return dummyID;
	}
	
}
