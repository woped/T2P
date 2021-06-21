package de.dhbw.text2process.models.textModel;

import java.util.ArrayList;
import java.util.List;

import de.dhbw.text2process.models.bpmn.nodes.ProcessEdge;
import de.dhbw.text2process.models.bpmn.nodes.ProcessNode;
import de.dhbw.text2process.models.meta.ProcessModel;
import de.dhbw.text2process.processors.meta.ProcessUtils;

public class TextModel extends ProcessModel {
	
	private LegendNode f_legend = new LegendNode();
	
	/**
	 * 
	 */
	public TextModel() {
		this.addNode(f_legend);
	}

	@Override
	public String getDescription() {
		return "Text Model";
	}

	@Override
	public ProcessUtils getUtils() {
		if(super.getUtils() != null) {
			return super.getUtils();
		}//else
		return new TextModelUtils();
	}
	
	@Override
	public List<Class<? extends ProcessEdge>> getSupportedEdgeClasses() {
		ArrayList<Class<? extends ProcessEdge>> _edges = new ArrayList<Class<? extends ProcessEdge>>(1);
		_edges.add(TextEdge.class);
		return _edges;
	}

	@Override
	public List<Class<? extends ProcessNode>> getSupportedNodeClasses() {
		List<Class<? extends ProcessNode>> _nodes = new ArrayList<Class<? extends ProcessNode>>(2);
		_nodes.add(SentenceNode.class);
		_nodes.add(WordNode.class);
		_nodes.add(LegendNode.class);
		return _nodes;
	}
	
	public LegendNode getLegend() {
		return f_legend;
	}

}
