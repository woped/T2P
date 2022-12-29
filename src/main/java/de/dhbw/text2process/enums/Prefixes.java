package de.dhbw.text2process.enums;

public enum Prefixes {
  DUMMY("DUMMY");

  String prefix;

  private Prefixes(String prefix) {
    setPrefix(prefix);
  }

  private void setPrefix(String prefix) {
    this.prefix = prefix;
  }
}
