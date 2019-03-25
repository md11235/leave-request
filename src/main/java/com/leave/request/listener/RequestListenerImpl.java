/**
 * 
 */
package com.leave.request.listener;

import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.flowable.engine.IdentityService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.runtime.Execution;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.idm.api.User;
import org.flowable.task.service.delegate.DelegateTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.leave.request.constants.RequestStatusEnum;
import com.leave.request.model.ConstructionFlowRequest;
import com.leave.request.repository.LeaveRequestRepository;
import com.leave.request.service.NotificationService;
import com.leave.request.util.SecurityUtil;

/**
 * @author eotayde
 *
 */
@Service("requestListener")
public class RequestListenerImpl implements RequestListener {

	private final static Logger logger = LoggerFactory.getLogger(RequestListenerImpl.class);

	@Autowired
	private LeaveRequestRepository leaveRequestRepository;
	
	@Autowired
	private NotificationService notificationService;

	@Autowired
	private TaskService taskService;

	@Autowired
	private IdentityService identityService;

	@Autowired
	private RuntimeService runtimeService;

	@Override
	public void onCreateCostControlReview(Execution execution, DelegateTask task) {
		logger.info("start: onCreateCostControlReview");
		ProcessInstance processInstance = runtimeService.createProcessInstanceQuery()
				.processInstanceId(execution.getProcessInstanceId()).singleResult();
		String strLeaveId = processInstance.getBusinessKey();

		Map<String, Object> variables = task.getVariables();
		String reviewer = (String) variables.get("reviewer");
		logger.debug("reviewer: {} ", reviewer);

		// if reviewer exists, set the assignee
		if (StringUtils.isNotBlank(reviewer)) {
			logger.debug("reviewer exists!");
			User user = identityService.createUserQuery().userId(reviewer).singleResult();
			taskService.claim(task.getId(), user.getId());
		}

        ConstructionFlowRequest leaveRequest = leaveRequestRepository.findOne(Long.valueOf(strLeaveId));
        leaveRequest.setStatus(RequestStatusEnum.COST_CONTROL_REVIEW_AWAITING.getValue());
        leaveRequest.setReviewedBy(SecurityUtil.getUsername());
        leaveRequestRepository.save(leaveRequest);

		logger.info("end: onCreateCostControlReview");
	}

	@Override
	public void onCompleteCostControlReview(DelegateTask task) {
		logger.info("start: onCompleteCostControlReview");
		// assign the reviewer, we need this so we'll be able to retrieve who
		// reviewed it
		Map<String, Object> variables = task.getVariables();
		String reviewer = (String) variables.get("reviewer");
		logger.debug("reviewer: {}", reviewer);

		if (StringUtils.isBlank(reviewer)) {
			logger.debug("reviewer is blank, assign to current user");
			variables.put("reviewer", SecurityUtil.getUsername());

			taskService.setVariables(task.getId(), variables);
		}

        // update the status
        logger.debug("update status...");
		String strLeaveId = String.valueOf(variables.get("leaveId"));
        ConstructionFlowRequest leaveRequest = leaveRequestRepository.findOne(Long.valueOf(strLeaveId));
        leaveRequest.setStatus(RequestStatusEnum.COST_CONTROL_REVIEW_DONE.getValue());
        leaveRequest.setReviewedBy(SecurityUtil.getUsername());
        leaveRequestRepository.save(leaveRequest);

        logger.info("end: onCompleteCostControlReview");
	}

	@Override
	public void onCreateEnquiryReview(Execution execution, DelegateTask task) {
		logger.info("start: onCreateManagerReview");
		ProcessInstance processInstance = runtimeService.createProcessInstanceQuery()
				.processInstanceId(execution.getProcessInstanceId()).singleResult();
		
		String strLeaveId = processInstance.getBusinessKey();

		// update the status
		logger.debug("update status...");
		ConstructionFlowRequest leaveRequest = leaveRequestRepository.findOne(Long.valueOf(strLeaveId));
		leaveRequest.setStatus(RequestStatusEnum.ENQUIRY_REVIEW_AWAITING.getValue());
		leaveRequest.setReviewedBy(SecurityUtil.getUsername());
		leaveRequestRepository.save(leaveRequest);

		logger.info("end: onCreateManagerReview");
	}

	@Override
	public void onCompleteEnquiryReview(DelegateTask task) {
        logger.info("start: onCompleteEnquiryReview");
        // assign the reviewer, we need this so we'll be able to retrieve who
        // reviewed it
        Map<String, Object> variables = task.getVariables();
        String reviewer = (String) variables.get("reviewer");
        logger.debug("previous reviewer: {}", reviewer);

        String new_reviewer = SecurityUtil.getUsername();
        variables.put("reviewer", new_reviewer);
        logger.debug("new reviewer: {}", new_reviewer);

        taskService.setVariables(task.getId(), variables);

        // update the status
        logger.debug("update status...");
        String strLeaveId = String.valueOf(variables.get("leaveId"));
        ConstructionFlowRequest leaveRequest = leaveRequestRepository.findOne(Long.valueOf(strLeaveId));
        leaveRequest.setStatus(RequestStatusEnum.ENQUIRY_REVIEW_DONE.getValue());
        leaveRequest.setReviewedBy(SecurityUtil.getUsername());
        leaveRequestRepository.save(leaveRequest);

        logger.info("end: onCompleteEnquiryReview");
	}

    @Override
    public void onCreateConstructionReview(Execution execution, DelegateTask task) {
        logger.info("start: onCreateConstructionReview");
        ProcessInstance processInstance = runtimeService.createProcessInstanceQuery()
                .processInstanceId(execution.getProcessInstanceId()).singleResult();

        String strLeaveId = processInstance.getBusinessKey();

        // update the status
        logger.debug("update status...");
        ConstructionFlowRequest leaveRequest = leaveRequestRepository.findOne(Long.valueOf(strLeaveId));
        leaveRequest.setStatus(RequestStatusEnum.CONSTRUCTION_AWAITING.getValue());
        leaveRequest.setReviewedBy(SecurityUtil.getUsername());
        leaveRequestRepository.save(leaveRequest);

        logger.info("end: onCreateConstructReview");
    }

    @Override
    public void onCompleteConstructionReview(DelegateTask task) {
        logger.info("start: onCompleteConstructionReview");
        // assign the reviewer, we need this so we'll be able to retrieve who
        // reviewed it
        Map<String, Object> variables = task.getVariables();
        String reviewer = (String) variables.get("reviewer");
        logger.debug("previous reviewer: {}", reviewer);

        String new_reviewer = SecurityUtil.getUsername();
        variables.put("reviewer", new_reviewer);
        logger.debug("new reviewer: {}", new_reviewer);

        taskService.setVariables(task.getId(), variables);

        // update the status
        logger.debug("update status...");
        String strLeaveId = String.valueOf(variables.get("leaveId"));
        ConstructionFlowRequest leaveRequest = leaveRequestRepository.findOne(Long.valueOf(strLeaveId));
        leaveRequest.setStatus(RequestStatusEnum.CONSTRUCTION_DONE.getValue());
        leaveRequest.setReviewedBy(SecurityUtil.getUsername());
        leaveRequestRepository.save(leaveRequest);

        logger.info("end: onCompleteConstructionReview");
    }

    @Override
	public void onApprove(DelegateExecution execution) {
		ProcessInstance processInstance = runtimeService.createProcessInstanceQuery()
				.processInstanceId(execution.getProcessInstanceId()).singleResult();

		String strLeaveId = processInstance.getBusinessKey();

//		ConstructionFlowRequest leaveRequest = leaveRequestRepository.findOne(Long.valueOf(strLeaveId));
//		leaveRequest.setStatus(RequestStatusEnum.APPROVED.getValue());
//		leaveRequest.setApprovedBy(SecurityUtil.getUsername());
//		leaveRequestRepository.save(leaveRequest);
	}

	@Override
	public void onReject(DelegateExecution execution) {
		ProcessInstance processInstance = runtimeService.createProcessInstanceQuery()
				.processInstanceId(execution.getProcessInstanceId()).singleResult();

		String strLeaveId = processInstance.getBusinessKey();

//		ConstructionFlowRequest leaveRequest = leaveRequestRepository.findOne(Long.valueOf(strLeaveId));
//		leaveRequest.setStatus(RequestStatusEnum.REJECTED.getValue());
//		leaveRequestRepository.save(leaveRequest);
	}

}
