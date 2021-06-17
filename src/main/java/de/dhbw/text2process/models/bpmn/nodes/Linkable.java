package de.dhbw.text2process.models.bpmn.nodes;

import tools.ReferenceChooserRestriction;

public interface Linkable {
	
	 /**
     * @return a {@link ReferenceChooserRestriction}.
     */
    public ReferenceChooserRestriction getReferenceRestrictions();

}
