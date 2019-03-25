/**
 * 
 */
package com.leave.request;

import org.flowable.engine.IdentityService;
import org.flowable.idm.api.Group;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import com.leave.request.model.RequestType;
import com.leave.request.model.Role;
import com.leave.request.repository.RequestTypeRepository;
import com.leave.request.repository.RoleRepository;

/**
 * @author Eraine
 *
 */
@Component
public class DefaultDataLoader implements ApplicationRunner {

	@Autowired
	private RequestTypeRepository requestTypeRepository;
	
	@Autowired
	private RoleRepository roleRepository;
	
	@Autowired
	private IdentityService identityService;

	public void run(ApplicationArguments args) throws Exception {
		// For sample users, please use the register page. It will automatically save the user to its
		// corresponding group based on the user role.
		
		// Insert roles
        // for use by custom application logic code
		if (roleRepository.count() == 0) {
            roleRepository.save(new Role("ADMIN", "管理员"));
            //HR("人力资源"),
            roleRepository.save(new Role("DESIGN_DPMT_EMPLOYEE", "设计部门员工"));
            roleRepository.save(new Role("COST_CONTROL_DPMT_EMPLOYEE", "成本控制部门员工"));
            roleRepository.save(new Role("ENQUIRY_DPTM_EMPLOYEE", "招标部门员工"));
            roleRepository.save(new Role("CONSTRUCT_DPTM_EMPLOYEE", "施工单位员工"));
            //roleRepository.save(new Role("MANAGER", "经理"));
		}
		
		// Insert request type
		if (requestTypeRepository.count() == 0) {
			requestTypeRepository.save(new RequestType("municipal_landscape_construction", "市政景观工程"));
		}
		
		// Insert groups
        // used by Flowable
		if (identityService.createGroupQuery().count() == 0) {
			String[] groups = new String[] {
			        "design_dpmt_employee",
                    "cost_control_dpmt_employee",
                    "enquiry_dptm_employee",
                    "construct_dptm_employee",
                    "manager"};
			for (String group: groups) {
				Group newGroup = identityService.newGroup(group);
				newGroup.setName(group);
				newGroup.setType("assignment");
				identityService.saveGroup(newGroup);
			}
		}
	}

}
