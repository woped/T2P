/** modified taken from https://github.com/FabianFriedrich/Text2Process */
package de.dhbw.text2process.models.textModel;

import de.dhbw.text2process.models.bpmn.nodes.ProcessEdge;
import de.dhbw.text2process.models.bpmn.nodes.ProcessNode;
import de.dhbw.text2process.processors.meta.ProcessUtils;

public class TextModelUtils extends ProcessUtils {

  @Override
  public ProcessEdge createDefaultEdge(ProcessNode source, ProcessNode target) {
    return null;
  }
}
