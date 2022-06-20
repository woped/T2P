package de.dhbw.text2process.helper.exporter;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import de.dhbw.text2process.helper.exporter.meta.Exporter;
import de.dhbw.text2process.models.bpmn.ComplexGateway;
import de.dhbw.text2process.models.bpmn.EventBasedGateway;
import de.dhbw.text2process.models.bpmn.ExclusiveGateway;
import de.dhbw.text2process.models.bpmn.InclusiveGateway;
import de.dhbw.text2process.models.bpmn.Lane;
import de.dhbw.text2process.models.bpmn.ParallelGateway;
import de.dhbw.text2process.models.bpmn.Pool;
import de.dhbw.text2process.models.bpmn.SequenceFlow;
import de.dhbw.text2process.models.bpmn.Task;
import de.dhbw.text2process.models.meta.BPMNModel;
import de.dhbw.text2process.models.worldModel.Action;
import de.dhbw.text2process.models.worldModel.Flow;
import de.dhbw.text2process.processors.worldmodel.transform.TextAnalyzer;
import org.camunda.bpm.model.bpmn.Bpmn;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.GatewayDirection;
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
		BpmnModelInstance modelInstance = process.done();

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


	public void goTrough(int flowIndex){
		if(allFlows.get(flowIndex).getMultipleObjects().size() == 0){
			process.done();

		}else if((allFlows.get(flowIndex).getMultipleObjects().size() == 1)){
			process = process.userTask()
					.name(allFlows.get(flowIndex).getMultipleObjects().get(0).getFinalLabel());
			if(allFlows.size()-1 > flowIndex){
				goTrough(flowIndex+1);
			}
		}else if((allFlows.get(flowIndex).getMultipleObjects().size() > 1)){
			if(allFlows.get(flowIndex).getDirection().name().equals("split")){
				process = process.exclusiveGateway().name("split"+flowIndex)
				.id("split"+flowIndex);
				for (int i = 0; i < allFlows.get(flowIndex).getMultipleObjects().size(); i++) {

					process = process.userTask()
							.name(allFlows.get(flowIndex).getMultipleObjects().get(i).getFinalLabel())
							.id(allFlows.get(flowIndex).getMultipleObjects().get(i).getFinalLabel().replaceAll("\\s+",""));

					if(i < allFlows.get(flowIndex).getMultipleObjects().size()-1 ){
						process = process.moveToLastGateway();
					}

				}
				if(allFlows.size() > flowIndex){
					goTrough(flowIndex+1);
				}
			}else if(allFlows.get(flowIndex).getDirection().name().equals("join")){
				process = process.exclusiveGateway().name("join"+flowIndex)
				.id("join"+flowIndex);
				for (int i = 0; i < allFlows.get(flowIndex).getMultipleObjects().size(); i++) {
					process = process.moveToNode(allFlows.get(flowIndex-1).getMultipleObjects().get(i).getFinalLabel().replaceAll("\\s+",""))
							.connectTo("join"+flowIndex);
				}
				if(allFlows.size() > flowIndex){
					goTrough(flowIndex+1);
				}
			}
		}

	}


}
