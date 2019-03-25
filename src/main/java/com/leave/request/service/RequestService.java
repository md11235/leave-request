/**
 * 
 */
package com.leave.request.service;

import java.util.List;

import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.task.Attachment;
import org.springframework.web.multipart.MultipartFile;

import com.leave.request.dto.RequestApprovalDto;
import com.leave.request.model.LeaveRequest;

/**
 * @author Eraine
 *
 */
public interface RequestService {

	LeaveRequest findById(Long id);
	
	List<LeaveRequest> findAllByUsername(String username);
	
	void save(LeaveRequest leaveRequest);
	
	void submit(LeaveRequest leaveRequest, MultipartFile file);

	void sendAlert(DelegateExecution execution);
	
	void approveOrReject(RequestApprovalDto requestApprovalDto);

	List<Attachment> findAllAttachmentsByLeaveId(Long LeaveId);
	
}
