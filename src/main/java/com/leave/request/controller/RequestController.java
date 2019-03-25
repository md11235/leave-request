/**
 * 
 */
package com.leave.request.controller;

import java.util.List;

import org.flowable.engine.task.Attachment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.leave.request.constants.ApprovementStageEnum;
import com.leave.request.constants.RequestStatusEnum;
import com.leave.request.constants.UserRoleEnum;
import com.leave.request.dto.MyHistoryTask;
import com.leave.request.dto.MyTask;
import com.leave.request.dto.RequestApprovalDto;
import com.leave.request.model.ConstructionFlowRequest;
import com.leave.request.service.MyTaskService;
import com.leave.request.service.RequestService;
import com.leave.request.util.SecurityUtil;
import com.leave.request.validator.RequestValidator;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Eraine
 *
 */
@Controller
@SessionAttributes({"requestForm"})
public class RequestController {

	private final static Logger logger = LoggerFactory.getLogger(RequestController.class);
	
	@Autowired
	private RequestValidator validator;

	@Autowired
	private RequestService requestService;

	@Autowired
	private MyTaskService myTaskService;

	@GetMapping("/request")
	public String request(Model model) {
		model.addAttribute("requestForm", new ConstructionFlowRequest());
		return "request";
	}

	@PostMapping("/request")
	public String processRequest(@ModelAttribute("requestForm") ConstructionFlowRequest leaveRequest,
								 HttpServletRequest request,
								 @RequestParam("file1") MultipartFile file1,
								 BindingResult bindingResult,
			Model model) {
		validator.validate(leaveRequest, bindingResult);

		String encoding = request.getCharacterEncoding();
		System.out.println(encoding);

		if (bindingResult.hasErrors()) {
			return "request";
		}


		requestService.save(leaveRequest);
		requestService.submit(leaveRequest, file1);

		model.addAttribute("success", true);

		return "request";
	}

	@GetMapping("/view/{id}")
	public String requestView(@PathVariable("id") Long id, Model model) {
		ConstructionFlowRequest leaveRequest = requestService.findById(id);

		model.addAttribute("leaveRequest", leaveRequest);
		
		return "request-view";
	}
	
	@GetMapping("/edit/{id}")
	public String requestEdit(@PathVariable("id") Long id, Model model, RedirectAttributes redirectAttributes) {
		ConstructionFlowRequest leaveRequest = requestService.findById(id);
		
		if (!leaveRequest.getStatus().equals(RequestStatusEnum.REJECTED.getValue())) {
			redirectAttributes.addFlashAttribute("error", "You are not authorized to view that page!");
			return "redirect:/home";
		}
		
		model.addAttribute("requestForm", leaveRequest);
		return "request-edit";
	}
	
	@PostMapping("/edit")
	public String processRequestEdit(@ModelAttribute("requestForm") ConstructionFlowRequest leaveRequest, BindingResult bindingResult,
                                     Model model) {
		validator.validate(leaveRequest, bindingResult);

		if (bindingResult.hasErrors()) {
			return "request-edit";
		}

		requestService.save(leaveRequest);
		requestService.submit(leaveRequest, null);

		model.addAttribute("success", true);

		return "request-edit";
	}

	@GetMapping("/json/get-task-history/{id}")
	public ResponseEntity<List<MyHistoryTask>> getAllHistoryTask(@PathVariable("id") Long id, Model model) {
		List<MyHistoryTask> myHistoryTask = myTaskService.getTaskHistory(String.valueOf(id));
		return new ResponseEntity<List<MyHistoryTask>>(myHistoryTask, HttpStatus.OK);
	}

	@GetMapping("/json/get-historic-tasks")
    public ResponseEntity<List<MyHistoryTask>> getHistoricTasks(Model model) {
        List<MyHistoryTask> historicTasks = myTaskService.getHistoricTasks(SecurityUtil.getUsername());
        return new ResponseEntity<List<MyHistoryTask>>(historicTasks, HttpStatus.OK);
    }

	@GetMapping("/review/{id}")
	public String manageEmployeeLeavesReview(@PathVariable("id") String taskId, RedirectAttributes redirectAttributes,
			Model model) {
		MyTask myTask = myTaskService.findTaskByTaskId(taskId);

		ConstructionFlowRequest leaveRequest = requestService.findById((Long) myTask.getProcessVariables().get("leaveId"));

		List<Attachment> attachments = requestService.findAllAttachmentsByLeaveId(Long.valueOf(leaveRequest.getId()));

		if (SecurityUtil.getUsername().equals(leaveRequest.getCreateBy())) {
			redirectAttributes.addFlashAttribute("error", "You are not authorized to view that page!");
			return "redirect:/home";
		}

		model.addAttribute("leaveRequest", leaveRequest);
		model.addAttribute("taskId", myTask.getId());
		RequestApprovalDto dto = new RequestApprovalDto(taskId, String.valueOf(leaveRequest.getId()));
		model.addAttribute("requestApprovalForm", dto);
		model.addAttribute("attachments", attachments);

		return "request-review";
	}

	@PostMapping(value = "/review/submit", params = "action=approve")
	public String processApproveRequest(@ModelAttribute("requestApprovalForm") RequestApprovalDto requestApprovalDto,
			@RequestParam("file1") MultipartFile file1,
			BindingResult bindingResult, Model model, RedirectAttributes redirectAttributes) {

	    if(SecurityUtil.hasRole(UserRoleEnum.COST_CONTROL_DPMT_EMPLOYEE.getValue())) {
            requestApprovalDto.setIsApproved(ApprovementStageEnum.COST_CONTROL_APPROVED.getValue());
        }

        if(SecurityUtil.hasRole(UserRoleEnum.ENQUIRY_DPTM_EMPLOYEE.getValue())) {
            requestApprovalDto.setIsApproved(ApprovementStageEnum.ENQUIRY_APPROVED.getValue());
        }

        if(SecurityUtil.hasRole(UserRoleEnum.CONSTRUCT_DPTM_EMPLOYEE.getValue())) {
            requestApprovalDto.setIsApproved(ApprovementStageEnum.CONSTRUCTION_DONE.getValue());
        }

		requestService.approveOrReject(requestApprovalDto, file1);

		redirectAttributes.addFlashAttribute("requestReviewed", "您已经处理了本任务！");
		
		return "redirect:/home";
	}
	
	@PostMapping(value = "/review/submit", params = "action=reject")
	public String processRejectRequest(@ModelAttribute("requestApprovalForm") RequestApprovalDto requestApprovalDto,
			@RequestParam("file1") MultipartFile file1,
			BindingResult bindingResult, Model model, RedirectAttributes redirectAttributes) {

        if(SecurityUtil.hasRole(UserRoleEnum.COST_CONTROL_DPMT_EMPLOYEE.getValue())) {
            requestApprovalDto.setIsApproved(ApprovementStageEnum.COST_CONTROL_REJECTED.getValue());
        }

        if(SecurityUtil.hasRole(UserRoleEnum.ENQUIRY_DPTM_EMPLOYEE.getValue())) {
            requestApprovalDto.setIsApproved(ApprovementStageEnum.ENQUIRY_REJECTED.getValue());
        }

        if(SecurityUtil.hasRole(UserRoleEnum.CONSTRUCT_DPTM_EMPLOYEE.getValue())) {
            requestApprovalDto.setIsApproved(ApprovementStageEnum.CONSTRUCTION_REJECTED.getValue());
        }

		requestService.approveOrReject(requestApprovalDto, file1);
		redirectAttributes.addFlashAttribute("requestReviewed", "您已经驳回了本任务！");
		
		return "redirect:/home";
	}

}
