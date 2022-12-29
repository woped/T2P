package de.dhbw.text2process.enums;

public enum PersonalPronouns {
  PERSONAL_PRONOUN_HE("he"),
  PERSONAL_PRONOUN_SHE("she"),
  PERSONAL_PRONOUN_IT("it");
  String descriptor;

  PersonalPronouns(String descriptor) {
    this.descriptor = descriptor;
  }

  public String getDescriptor() {
    return this.descriptor;
  }
}
