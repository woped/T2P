/** modified taken from https://github.com/FabianFriedrich/Text2Process */
package de.dhbw.text2process.models.worldModel;

import de.dhbw.text2process.enums.PhraseType;
import de.dhbw.text2process.enums.SpecifierType;
import de.saar.coli.salsa.reiter.framenet.FrameElement;

public class Specifier extends SpecifiedElement {

  private SpecifierType f_type = SpecifierType.DIRECT; // how this was found,
  private String f_headWord = null;
  private ExtractedObject f_object = null;
  // type of this phrase (used for deciding if it can be used in a label or not)
  private PhraseType f_pt = PhraseType.UNKNOWN;
  // if it was possible to detect, the FrameElement is set here, for further analysis
  private FrameElement f_fe;

  /**
   * @param origin
   * @param wordInSentence
   */
  public Specifier(T2PSentence origin, int wordInSentence, String phrase) {
    super(origin, wordInSentence, phrase.toLowerCase());
  }

  public SpecifierType getType() {
    return f_type;
  }

  public String getPhrase() {
    return getName();
  }

  public void setSpecifierType(SpecifierType type) {
    f_type = type;
  }

  @Override
  public String toString() {
    return "[" + getPhrase() + "]";
  }

  public void setObject(ExtractedObject object) {
    this.f_object = object;
  }

  public ExtractedObject getObject() {
    return f_object;
  }

  public void setHeadWord(String headWord) {
    this.f_headWord = headWord;
  }

  public String getHeadWord() {
    return f_headWord;
  }

  /** @param pt */
  public void setPhraseType(PhraseType pt) {
    f_pt = pt;
  }

  public PhraseType getPhraseType() {
    return f_pt;
  }

  public void setFrameElement(FrameElement fe) {
    f_fe = fe;
  }

  public FrameElement getFrameElement() {
    return f_fe;
  }
}
