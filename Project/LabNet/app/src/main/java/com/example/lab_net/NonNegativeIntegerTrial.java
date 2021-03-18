package com.example.lab_net;

public class NonNegativeIntegerTrial extends Trial{
    private Long nonNegativeCount;


    public NonNegativeIntegerTrial(String id, String title, Long nonNegativeCount) {
        super(id, title);
        this.nonNegativeCount = nonNegativeCount;
    }

    public Long getNonNegativeCount() {
        return nonNegativeCount;
    }

    public void setNonNegativeCount(Long nonNegativeCount) {
        this.nonNegativeCount = nonNegativeCount;
    }
}
