/**
 * 
 */
package com.leave.request.validator;


import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import com.leave.request.model.ConstructionFlowRequest;

/**
 * @author Eraine
 *
 */
@Component
public class RequestValidator implements Validator {

	@Override
	public boolean supports(Class<?> clazz) {
		return ConstructionFlowRequest.class.equals(clazz);
	}

	@Override
	public void validate(Object obj, Errors errors) {
		ConstructionFlowRequest leaveRequest = (ConstructionFlowRequest) obj;
		
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "requestType", "message.notEmpty.requestType", "请选择流程类型。");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "startDate", "message.notEmpty.startDate", "请输入开始日期");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "endDate", "message.notEmpty.endDate", "请输入结束日期");
		// ValidationUtils.rejectIfEmptyOrWhitespace(errors, "reason", "message.notEmpty.reason", "请输入备注信息");
		
	}

}
