/**
 * 
 */
package com.leave.request.service;

import java.io.InputStream;
import java.util.List;

import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.task.Attachment;
import org.springframework.web.multipart.MultipartFile;

import com.leave.request.dto.RequestApprovalDto;
import com.leave.request.model.ConstructionFlowRequest;

/**
 * @author Eraine
 *
 */
public interface RequestService {

	ConstructionFlowRequest findById(Long id);
	
	List<ConstructionFlowRequest> findAllByUsername(String username);
	
	void save(ConstructionFlowRequest leaveRequest);
	
	void submit(ConstructionFlowRequest leaveRequest, MultipartFile file);

	void sendAlert(DelegateExecution execution);
	
	void approveOrReject(RequestApprovalDto requestApprovalDto, MultipartFile file);

	List<Attachment> findAllAttachmentsByLeaveId(Long LeaveId);

	Attachment findAttachment(String attachmentId);

    InputStream getAttachmentContentInputStream(String attachmentId);
}
