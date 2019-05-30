package com.leave.request.constants;

import java.util.ArrayList;
import java.util.List;

public class ClassAContractProcess {
    private static List<ContractStage> stages = new ArrayList<ContractStage>() {{
        add(new ContractStage("设计", ApprovementStageEnum.DESIGN_COMPLETE.getValue()));
        add(new ContractStage("成本", ApprovementStageEnum.COST_CONTROL_APPROVED.getValue()));
        add(new ContractStage("招标", ApprovementStageEnum.ENQUIRY_APPROVED.getValue()));
        add(new ContractStage("施工", ApprovementStageEnum.CONSTRUCTION_DONE.getValue()));
    }};

    public static List<ContractStage> buildStages(int currentUndecidedStage) {
        return stages.subList(0, currentUndecidedStage);
    }
}
