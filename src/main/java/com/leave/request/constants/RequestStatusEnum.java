/**
 * 
 */
package com.leave.request.constants;

/**
 * @author Eraine
 *
 */
public enum RequestStatusEnum {

    DESIGN_DPTM_COMPLETED("完成施工图设计"),
	COST_CONTROL_REVIEW_AWAITING("等待工程清单编制"),
    COST_CONTROL_REVIEW_DONE("完成工程清单编制"),
    ENQUIRY_REVIEW_AWAITING("等待招标"),
    ENQUIRY_REVIEW_DONE("完成招标"),
    CONSTRUCTION_AWAITING("等待施工"),
    CONSTRUCTION_DONE("完成施工"),

	APPROVED("通过"),
	REJECTED("驳回");


    private String value;

	/**
	 * @param value
	 */
	private RequestStatusEnum(String value) {
		this.value = value;
	}

	/**
	 * @return the value
	 */
	public String getValue() {
		return value;
	}

}
