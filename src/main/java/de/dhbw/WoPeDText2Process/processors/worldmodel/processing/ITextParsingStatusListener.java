/**
 * modified taken from https://github.com/FabianFriedrich/Text2Process
 */
package de.dhbw.WoPeDText2Process.processors.worldmodel.processing;

public interface ITextParsingStatusListener {
	
	public void setNumberOfSentences(int number);
	public void sentenceParsed(int number);

}
