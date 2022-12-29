package de.dhbw.text2process.helper.exporter;

import de.dhbw.text2process.helper.exporter.meta.Exporter;
import de.dhbw.text2process.models.bpmn.*;
import de.dhbw.text2process.models.meta.BPMNModel;
import de.dhbw.text2process.models.worldModel.Flow;
import de.dhbw.text2process.processors.worldmodel.transform.TextAnalyzer;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import org.camunda.bpm.model.bpmn.Bpmn;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.builder.AbstractFlowNodeBuilder;

public class BPMNExporter extends Exporter {
  TextAnalyzer analyzer;
  public AbstractFlowNodeBuilder process;
  ArrayList<Flow> allFlows;

  public void setTextAnalyzer(TextAnalyzer analyzer) {
    this.analyzer = analyzer;
    process = Bpmn.createProcess().executable().startEvent().name("StartEvent");
    allFlows = analyzer.getWorld().getFlows();
  }

  public boolean createBPMN(File outputFile) {
    BpmnModelInstance modelInstance = process.endEvent().name("EndEvent").done();

    // Try to create File and return true on success
    try {
      Bpmn.writeModelToFile(outputFile, modelInstance);
      return true;
    } catch (Exception e) {
      return false;
    }
  }

  private String start =
      "<definitions id=\"Definition\""
          + "\ntargetNamespace=\"http://www.example.org/MinimalExample\""
          + "\ntypeLanguage=\"http://www.java.com/javaTypes\""
          + "\nexpressionLanguage=\"http://www.mvel.org/2.0\""
          + "\nxmlns=\"http://www.omg.org/spec/BPMN/20100524/MODEL\""
          + "\nxmlns:xs=\"http://www.w3.org/2001/XMLSchema-instance\""
          + "\nxs:schemaLocation=\"http://www.omg.org/spec/BPMN/20100524/MODEL BPMN20.xsd\""
          + "\nxmlns:bpmndi=\"http://www.omg.org/spec/BPMN/20100524/DI\""
          + "\nxmlns:dc=\"http://www.omg.org/spec/DD/20100524/DC\""
          + "\nxmlns:di=\"http://www.omg.org/spec/DD/20100524/DI\""
          + "\nxmlns:tns=\"http://www.jboss.org/drools\">";

  private String end = "\n</process>" + "\n</definitions>";

  private StringBuffer bpmn = new StringBuffer(start);

  private BPMNModel model = null;

  private File outputFile;

  public BPMNExporter(BPMNModel bpmnm) {
    model = bpmnm;
    init(model);
  }

  public BPMNExporter() {}

  private void init(BPMNModel bpmnm) {
    String id = bpmnm.getId();
    String name = bpmnm.getProcessName();
    bpmn.append(
        "\n\n<process processType=\"Private\" isExecutable=\"true\" id=\""
            + id
            + "\""
            + " name=\""
            + name
            + "\">");
  }

  public void addFlowObjects(ArrayList<Task> tasks) {
    for (Task t : tasks) {
      String id = t.getId();
      String name = t.getName();
      bpmn.append("\n<task id=\"" + id + "\" name=\"" + name + "\"/>");
    }
  }

  public void addFlows(ArrayList<SequenceFlow> flows) {
    for (SequenceFlow f : flows) {
      if (f.getSource() != null && f.getTarget() != null) {
        String id = f.getId();
        String source = f.getSource().getId();
        String target = f.getTarget().getId();
        bpmn.append(
            "\n<sequenceFlow id=\""
                + id
                + "\" sourceRef=\""
                + source
                + "\" targetRef=\""
                + target
                + "\"/>");
      }
    }
  }

  public void export(File outputFile) {
    if (outputFile == null) {
      this.outputFile = new File("BPMN.bpmn");
    } else {
      this.outputFile = outputFile;
    }
    try {
      BufferedWriter out = new BufferedWriter(new FileWriter(this.outputFile));
      String outText = bpmn.toString();
      out.write(outText);
      out.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public void addLanes(ArrayList<Lane> lanes) {
    bpmn.append("\n<laneSet id=\"4711\">");
    for (Lane l : lanes) {
      String id = l.getId();
      String name = l.getName();
      bpmn.append("\n<lane name=\"" + name + "\" id=\"" + id + "\"/>");
    }
    bpmn.append("\n</laneSet>");
  }

  public void addPools(ArrayList<Pool> pools) {
    for (Pool p : pools) {
      String id = p.getId();
      String name = p.getName();
      bpmn.append(
          "\n<collaboration id=\""
              + id
              + "\">"
              + "\nparticipant name=\""
              + name
              + "\" processRef=\""
              + model.getId()
              + "\" id=\""
              + id
              + "\"/>"
              + "\n</collaboration>");
    }
  }

  public void addGateways(
      ArrayList<ComplexGateway> com,
      ArrayList<EventBasedGateway> eBas,
      ArrayList<ExclusiveGateway> ex,
      ArrayList<InclusiveGateway> inc,
      ArrayList<ParallelGateway> par) {
    for (ComplexGateway g : com) {
      String id = g.getId();
      bpmn.append("\n<complexGateway id=\"" + id + "\"/>");
    }
    for (EventBasedGateway g : eBas) {
      String id = g.getId();
      bpmn.append("\n<eventBasedGateway id=\"" + id + "\"/>");
    }
    for (ExclusiveGateway g : ex) {
      String id = g.getId();
      bpmn.append("\n<exclusiveGateway id=\"" + id + "\"/>");
    }
    for (InclusiveGateway g : inc) {
      String id = g.getId();
      bpmn.append("\n<inclusiveGateway id=\"" + id + "\"/>");
    }
    for (ParallelGateway g : par) {
      String id = g.getId();
      bpmn.append("\n<parallelGateway id=\"" + id + "\"/>");
    }
  }

  public void end() {
    bpmn.append(end);
  }

  public void goThrough(int flowIndex) {

    // If Flow element does not contain any "Multiple Objects" end the process
    if (allFlows.get(flowIndex).getMultipleObjects().size() == 0) {
      process.done();

      // If Flow element leads to one action, add it to the process
    } else if ((allFlows.get(flowIndex).getMultipleObjects().size() == 1)) {
      process =
          process
              .userTask()
              .name(allFlows.get(flowIndex).getMultipleObjects().get(0).getFinalLabel());

      // If Flow element is not the last, rerun method with incremented index
      if (allFlows.size() - 1 > flowIndex) {
        goThrough(flowIndex + 1);
      }

      // If Flow element contains multiple elements in "Multiple Objects" add Gateway
    } else if ((allFlows.get(flowIndex).getMultipleObjects().size() > 1)) {

      // If Gateway is a Split, procede multiple paths
      if (allFlows.get(flowIndex).getDirection().name().equals("split")) {
        if (allFlows.get(flowIndex).getType().name().equals("choice")) {
          process = process.exclusiveGateway().name("split" + flowIndex).id("split" + flowIndex);
        } else {
          process = process.parallelGateway().name("split" + flowIndex).id("split" + flowIndex);
        }
        for (int multipleObjectsIndex = 0;
            multipleObjectsIndex < allFlows.get(flowIndex).getMultipleObjects().size();
            multipleObjectsIndex++) {
          String finalLabel =
              allFlows
                  .get(flowIndex)
                  .getMultipleObjects()
                  .get(multipleObjectsIndex)
                  .getFinalLabel();
          process =
              process
                  .userTask()
                  .name(finalLabel)
                  .id(finalLabel.replaceAll("\\s+", "").replaceAll("[-+.^:,]", ""));

          if (multipleObjectsIndex < allFlows.get(flowIndex).getMultipleObjects().size() - 1) {
            process = process.moveToLastGateway();
          }
        }
        if (allFlows.size() > flowIndex) {
          goThrough(flowIndex + 1);
        }

        // If Gateway is Join, procede Join
      } else if (allFlows.get(flowIndex).getDirection().name().equals("join")) {
        if (allFlows.get(flowIndex).getType().name().equals("choice")) {
          process = process.exclusiveGateway().name("join" + flowIndex).id("join" + flowIndex);
        } else {
          process = process.parallelGateway().name("join" + flowIndex).id("join" + flowIndex);
        }
        for (int i = 0; i < allFlows.get(flowIndex).getMultipleObjects().size(); i++) {
          process =
              process
                  .moveToNode(
                      allFlows
                          .get(flowIndex - 1)
                          .getMultipleObjects()
                          .get(i)
                          .getFinalLabel()
                          .replaceAll("\\s+", "")
                          .replaceAll("[-+.^:,]", ""))
                  .connectTo("join" + flowIndex);
        }
        if (allFlows.size() - 1 > flowIndex) {
          goThrough(flowIndex + 1);
        }
      }
    }
  }
}
