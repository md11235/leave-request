package com.leave.request.constants;

public enum ApprovementStageEnum {
    DESIGN_COMPLETE(0),
    COST_CONTROL_APPROVED(1),
    ENQUIRY_APPROVED(2),
    CONSTRUCTION_DONE(3),
    COST_CONTROL_REJECTED(-1),
    ENQUIRY_REJECTED(-2),
    CONSTRUCTION_REJECTED(-3);

    private final int value;

    ApprovementStageEnum(final int newValue) {
        value = newValue;
    }

    public int getValue() {
        return value;
    }
}
