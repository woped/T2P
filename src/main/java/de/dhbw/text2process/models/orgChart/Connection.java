/**
 * modified taken from https://github.com/FabianFriedrich/Text2Process
 */
package de.dhbw.text2process.models.orgChart;


import de.dhbw.text2process.models.bpmn.nodes.ProcessEdge;
import de.dhbw.text2process.models.bpmn.nodes.ProcessNode;

public class Connection extends ProcessEdge {

	
	/**
	 * 
	 */
	public Connection() {
		super();
	}
	
	/**
	 * @param source
	 * @param target
	 */
	public Connection(ProcessNode source, ProcessNode target) {
		super(source,target);
	}


}
