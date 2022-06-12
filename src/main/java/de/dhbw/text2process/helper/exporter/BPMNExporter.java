package de.dhbw.text2process.helper.exporter;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

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
import de.dhbw.text2process.processors.worldmodel.transform.TextAnalyzer;
import org.camunda.bpm.model.bpmn.Bpmn;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.builder.AbstractFlowNodeBuilder;

public class BPMNExporter extends Exporter{

	public boolean exportBPMN(TextAnalyzer analyzer, File outputFile) {

		//Create Start Node
		AbstractFlowNodeBuilder process = Bpmn.createProcess()
				.executable()
				.startEvent()
				.name("StartEvent");


		for (Action action : analyzer.getWorld().getActions()) {
			if(action.getName() != "Dummy Node") {
				process = process.userTask()
						.name(action.getFinalLabel());
			}
		}
		process = process.endEvent()
					.name("EndEvent");

		BpmnModelInstance modelInstance = process.done();


		//Try to create File and return true on success
		try{
			Bpmn.writeModelToFile(outputFile, modelInstance);
			return true;
		}catch(Exception e){
			return false;
		}
	}

}
