package com.example.lab_net;

/**
 * An Enum which refers to specific types of Experiments.
 *
 * Mapped value conversion is necessary because database names do not match to this enum.
 */
public enum ExperimentTypes {

    BINOMIAL (ExperimentTypes.BINOMIAL_STRING),
    NON_NEGATIVE_NUMBER (ExperimentTypes.NON_NEGATIVE_NUMBER_STRING),
    COUNT (ExperimentTypes.COUNT_STRING),
    MEASUREMENT (ExperimentTypes.MEASUREMENT_STRING);

    // for switches...
    public static final String BINOMIAL_STRING = "Binomial";
    public static final String NON_NEGATIVE_NUMBER_STRING = "NonNegativeInteger";
    public static final String COUNT_STRING = "Count-based";
    public static final String MEASUREMENT_STRING = "Measurement";

    private final String mappedValue;

    ExperimentTypes(String value) {
        this.mappedValue=value;
    }

    public final String value() {
        return this.mappedValue;
    }
}