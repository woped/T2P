package de.dhbw.text2process.enums;

public enum FlowType {
  concurrency,
  sequence,
  iteration, // This flowtype is not implemented -> can't occur
  choice,
  multiChoice,
  exception
}
