/**
 * modified taken from https://github.com/FabianFriedrich/Text2Process
 */
package de.dhbw.text2process.processors.worldmodel.processing;

public interface ITextParsingStatusListener {
	
	public void setNumberOfSentences(int number);
	public void sentenceParsed(int number);

}
