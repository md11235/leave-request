/**
 * 
 */
package com.leave.request.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.flowable.engine.RepositoryService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.repository.Deployment;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.engine.task.Attachment;
import org.flowable.task.api.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.leave.request.constants.ApprovementStageEnum;
import com.leave.request.dto.RequestApprovalDto;
import com.leave.request.model.ConstructionFlowRequest;
import com.leave.request.repository.LeaveRequestRepository;
import com.leave.request.util.SecurityUtil;

/**
 * @author Eraine
 *
 */
@Service("requestService")
public class RequestServiceImpl implements RequestService {

	private final static Logger logger = LoggerFactory.getLogger(RequestTypeService.class);

	@Autowired
	private LeaveRequestRepository leaveRequestRepository;

	@Autowired
	private RepositoryService repositoryService;

	@Autowired
	private RuntimeService runtimeService;

	@Autowired
	private TaskService taskService;

	@Override
	public List<ConstructionFlowRequest> findAllByUsername(String username) {
		return leaveRequestRepository.findAllByCreateBy(username);
	}

	@Override
	public void save(ConstructionFlowRequest leaveRequest) {
		leaveRequestRepository.save(leaveRequest);
	}

    @Override
    public List<Attachment> findAllAttachmentsByLeaveId(Long leaveId) {
        ProcessInstance processInstance = runtimeService.createProcessInstanceQuery()
                .processInstanceBusinessKey(String.valueOf(leaveId)).singleResult();

        assert processInstance != null: "FATAL: no process instance for this LEAVE REQUEST: " + String.valueOf(leaveId);

        return taskService.getProcessInstanceAttachments(processInstance.getId());
    }

    @Override
    public Attachment findAttachment(String attachmentId) {
	    return taskService.getAttachment(attachmentId);
    }

    @Override
    public InputStream getAttachmentContentInputStream(String attachmentId) {
	    return taskService.getAttachmentContent(attachmentId);
    }

    @Override
	public void submit(ConstructionFlowRequest leaveRequest, MultipartFile file) {
		Deployment deployment = repositoryService.createDeployment()
				.addClasspathResource("processes/Municipal_Landscape_Flow_V2.1.bpmn20.xml").deploy();
		ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery()
				.deploymentId(deployment.getId()).singleResult();

		if (processDefinition == null) {
			logger.error("Workflow does not exist.");
			return;
		}

		// check if there exists a process for this leave request
		ProcessInstance processInstance = runtimeService.createProcessInstanceQuery()
				.processInstanceBusinessKey(String.valueOf(leaveRequest.getId())).singleResult();

		// if there is none, start one
		if (processInstance == null) {
			logger.info("Starting process instance...");
			processInstance = runtimeService.startProcessInstanceByKey(processDefinition.getKey(),
					String.valueOf(leaveRequest.getId()));
		}

		Map<String, Object> variables = new HashMap<>();
		variables.put("employeeUsername", leaveRequest.getCreateBy());
		variables.put("reviewer", ""); // fill this later
		variables.put("leaveId", leaveRequest.getId());
		variables.put("startDate", leaveRequest.getStartDate());
		variables.put("endDate", leaveRequest.getEndDate());
		variables.put("isApproved", ApprovementStageEnum.DESIGN_COMPLETE.getValue());

		Task task = taskService.createTaskQuery().processInstanceId(processInstance.getProcessInstanceId())
				.singleResult();
		taskService.setAssignee(task.getId(), SecurityUtil.getUsername());
		taskService.setVariables(task.getId(), variables);


        attachFileToTask(task, file);

        taskService.complete(task.getId());

        for(Attachment attachment : taskService.getProcessInstanceAttachments(processInstance.getId())) {
            System.out.println(attachment.getName());
        }
	}

    private void attachFileToTask(Task task, MultipartFile file) {
        if(file != null) {
            try {
                taskService.createAttachment(
                file.getContentType(),
                task.getId(),
                task.getProcessInstanceId(),
                file.getOriginalFilename(),
                file.getName(),
                file.getInputStream()
                );
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
	public void sendAlert(DelegateExecution execution) {
		// TODO Auto-generated method stub

	}

	@Override
	public ConstructionFlowRequest findById(Long id) {
		return leaveRequestRepository.findOne(id);
	}

	@Override
	public void approveOrReject(RequestApprovalDto requestApprovalDto, MultipartFile file) {
		logger.info("start: approveOrReject");
		Task task = taskService.createTaskQuery().taskId(requestApprovalDto.getTaskId()).includeProcessVariables()
				.singleResult();

		if (task == null) {
			logger.error("This task does not exist");
			return;
		}

		runtimeService.setVariable(task.getExecutionId(), "isApproved", requestApprovalDto.getIsApproved());
		taskService.setAssignee(task.getId(), SecurityUtil.getUsername());
		taskService.addComment(task.getId(), task.getProcessInstanceId(), requestApprovalDto.getComment() != null ? requestApprovalDto.getComment() : "");
        attachFileToTask(task, file);

		taskService.complete(task.getId());

		logger.info("end: approveOrReject");
	}

}
