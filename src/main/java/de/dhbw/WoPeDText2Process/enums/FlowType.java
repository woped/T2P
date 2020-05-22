package de.dhbw.WoPeDText2Process.enums;

public enum FlowType {
    concurrency,
    sequence,
    iteration, // This flowtype is not implemented -> can't occur
    choice,
    multiChoice,
    exception
}
