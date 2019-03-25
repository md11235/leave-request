/**
 * 
 */
package com.leave.request.constants;

/**
 * @author Eraine
 *
 */
public enum UserRoleEnum {


	ADMIN("ADMIN"),
	//HR("人力资源"),
	DESIGN_DPMT_EMPLOYEE("DESIGN_DPMT_EMPLOYEE"),
	COST_CONTROL_DPMT_EMPLOYEE("COST_CONTROL_DPMT_EMPLOYEE"),
    ENQUIRY_DPTM_EMPLOYEE("ENQUIRY_DPTM_EMPLOYEE"),
    CONSTRUCT_DPTM_EMPLOYEE("CONSTRUCT_DPTM_EMPLOYEE"),
	MANAGER("MANAGER");
	
	private String value;

	/**
	 * @param value
	 */
	private UserRoleEnum(String value) {
		this.value = value;
	}

	/**
	 * @return the value
	 */
	public String getValue() {
		return value;
	}
}
