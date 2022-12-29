package de.dhbw.text2process.models.bpmn.nodes;

import de.dhbw.text2process.models.meta.ProcessModel;

public interface AttachedNode {

  /**
   * Returns the ProcessNode this AttachedNode belongs to.
   *
   * @return
   */
  public ProcessNode getParentNode(ProcessModel model);

  /**
   * Returns the id of the parent node.
   *
   * @return
   */
  public String getParentNodeId();

  /**
   * Sets the parent node.
   *
   * @param node
   * @return
   */
  public void setParentNode(ProcessNode node);
}
