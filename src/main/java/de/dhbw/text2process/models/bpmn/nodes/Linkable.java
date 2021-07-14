package de.dhbw.text2process.models.bpmn.nodes;

import de.dhbw.text2process.helper.ReferenceChooserRestriction;

public interface Linkable {
	
	 /**
     * @return a {@link ReferenceChooserRestriction}.
     */
    public ReferenceChooserRestriction getReferenceRestrictions();

}
