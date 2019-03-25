/**
 * 
 */
package com.leave.request.listener;

import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.runtime.Execution;
import org.flowable.task.service.delegate.DelegateTask;

/**
 * @author eotayde
 *
 */
public interface RequestListener {

	void onCreateCostControlReview(Execution execution, DelegateTask task);

	void onCompleteCostControlReview(DelegateTask task);
	
	void onCreateEnquiryReview(Execution execution, DelegateTask task);
	
	void onCompleteEnquiryReview(DelegateTask task);

	void onCreateConstructionReview(Execution execution, DelegateTask task);

	void onCompleteConstructionReview(DelegateTask task);
	
	void onApprove(DelegateExecution execution);
	
	void onReject(DelegateExecution execution);
	
}
