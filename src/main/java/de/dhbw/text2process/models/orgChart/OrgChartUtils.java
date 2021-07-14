/**
 * modified taken from https://github.com/FabianFriedrich/Text2Process
 */
package de.dhbw.text2process.models.orgChart;

import java.util.LinkedList;
import java.util.List;

import de.dhbw.text2process.models.bpmn.nodes.ProcessEdge;
import de.dhbw.text2process.models.bpmn.nodes.ProcessNode;
import de.dhbw.text2process.models.meta.ProcessModel;
import de.dhbw.text2process.processors.meta.ProcessUtils;

public class OrgChartUtils extends ProcessUtils {

    @Override
    public ProcessEdge createDefaultEdge(ProcessNode source, ProcessNode target) {
        return new Connection(source, target);
    }

    @Override
    public List<Class<? extends ProcessNode>> getNextNodesRecommendation(
            ProcessModel model, ProcessNode node) {
        List<Class<? extends ProcessNode>> result = new LinkedList<Class<? extends ProcessNode>>();
        if (node instanceof OrgUnit) {
            result.add(OrgUnit.class);
            result.add(ManagerialRole.class);
            result.add(Role.class);
        }
        if (node instanceof ManagerialRole | node instanceof Role) {
            result.add(Person.class);
            result.add(Substitute.class);
        }

        return result;
    }
}
