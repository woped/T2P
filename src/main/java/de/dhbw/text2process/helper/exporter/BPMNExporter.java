package de.dhbw.text2process.helper.exporter;

import java.io.File;
import java.util.ArrayList;

import de.dhbw.text2process.helper.exporter.meta.Exporter;
import de.dhbw.text2process.models.worldModel.Flow;
import de.dhbw.text2process.processors.worldmodel.transform.TextAnalyzer;
import org.camunda.bpm.model.bpmn.Bpmn;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.builder.AbstractFlowNodeBuilder;

public class BPMNExporter extends Exporter{
	TextAnalyzer analyzer;
	public AbstractFlowNodeBuilder process;
	ArrayList<Flow> allFlows;

	public void setTextAnalyzer(TextAnalyzer analyzer){
		this.analyzer = analyzer;
		process = Bpmn.createProcess()
				.executable()
				.startEvent()
				.name("StartEvent");
		allFlows = analyzer.getWorld().getFlows();

	}

	public boolean	createBPMN(File outputFile){
		BpmnModelInstance modelInstance = process
										.endEvent()
										.name("EndEvent")
										.done();

		//Try to create File and return true on success
		try{
			Bpmn.writeModelToFile(outputFile, modelInstance);
			return true;
		}catch(Exception e){
			return false;
		}
	}

	/*
	public boolean exportBPMN(TextAnalyzer analyzer) {

		ArrayList<Action> allProccesActions;
		ArrayList<Flow> allFlows = analyzer.getWorld().getFlows();
		List<Action> flowActions = null;
		int flowActionsSize = 0;
		Flow flow = null;






		/* for (Action action : analyzer.getWorld().getActions()) {
			if(action.getName() != "Dummy Node") {
				process = process.userTask()
						.name(action.getName() + " " + action.getObject());
			}
		} */

		/*
		for (int i = 0; i < allFlows.size(); i++) {
			flow = allFlows.get(i);
			flowActions = flow.getMultipleObjects();
			flowActionsSize  = flowActions.size();


			//Wenn ein Flow keine Actions enthält
			if (flowActionsSize == 0){
				process.done();
			//Wenn ein Flow genau eine Action enthält
			}else if (flowActionsSize == 1){
				process = process.userTask()
						.name(flowActions.get(1).getFinalLabel());
			//Wenn ein Flow mehr als 2 Actions enthält
			}else if (flowActionsSize > 1){
				if(flow.getDirection().name().equals("split")){
					//Anzahl der Tasks durchgehen
					process = process.exclusiveGateway().name("");
					for (int j = 1; i <= flowActionsSize; j++) {
						if(j == 1){
							//Ersten Zweig vom Gateway mit Action 1
							process = process.userTask()
									.name(flowActions.get(1).getFinalLabel())
									.userTask()
									.name(allFlows.get(i+1).getMultipleObjects().get(1).getFinalLabel());
						}else{
							//Zum Gateway zurückgehen
							process = process.moveToLastGateway();
							//Action hinzufügen
							process = process.userTask()
									.name(flowActions.get(i).getFinalLabel())

									.connectTo(allFlows.get(i+1).getMultipleObjects().get(1).getFinalLabel());
						}
					}
				}else if(flow.getDirection().name().equals("join")){

				}

				process = process.exclusiveGateway().name("Gateway").gatewayDirection(GatewayDirection.Diverging);
			}
		}



	}
	*/


	public void goThrough(int flowIndex){

		// If Flow element does not contain any "Multiple Objects" end the process
		if(allFlows.get(flowIndex).getMultipleObjects().size() == 0){
			process.done();

		// If Flow element leads to one action, add it to the process
		}else if((allFlows.get(flowIndex).getMultipleObjects().size() == 1)){
			process = process.userTask()
					.name(allFlows.get(flowIndex).getMultipleObjects().get(0).getFinalLabel());

			// If Flow element is not the last, rerun method with incremented index
			if(allFlows.size()-1 > flowIndex){
				goThrough(flowIndex+1);
			}

		// If Flow element contains multiple elements in "Multiple Objects" add Gateway
		}else if((allFlows.get(flowIndex).getMultipleObjects().size() > 1)){

			// If Gateway is a Split, procede multiple paths
			if(allFlows.get(flowIndex).getDirection().name().equals("split")){
				process = process.exclusiveGateway().name("split"+flowIndex)
				.id("split"+flowIndex);
				for (int multipleObjectsIndex = 0; multipleObjectsIndex < allFlows.get(flowIndex).getMultipleObjects().size(); multipleObjectsIndex++) {
					String finalLabel = allFlows.get(flowIndex).getMultipleObjects().get(multipleObjectsIndex).getFinalLabel();
					process = process.userTask()
							.name(finalLabel)
							.id(finalLabel.replaceAll("\\s+","").replaceAll("[-+.^:,]",""));

					if(multipleObjectsIndex < allFlows.get(flowIndex).getMultipleObjects().size()-1 ){
						process = process.moveToLastGateway();
					}

				}
				if(allFlows.size() > flowIndex){
					goThrough(flowIndex+1);
				}

			// If Gateway is Join, procede Join
			}else if(allFlows.get(flowIndex).getDirection().name().equals("join")){
				process = process.exclusiveGateway().name("join"+flowIndex)
				.id("join"+flowIndex);
				for (int i = 0; i < allFlows.get(flowIndex).getMultipleObjects().size(); i++) {
					process = process.moveToNode(allFlows.get(flowIndex-1).getMultipleObjects().get(i).getFinalLabel().replaceAll("\\s+","").replaceAll("[-+.^:,]",""))
							.connectTo("join"+flowIndex);
				}
				if(allFlows.size() - 1 > flowIndex){
					goThrough(flowIndex+1);
				}
			}
		}

	}


}
