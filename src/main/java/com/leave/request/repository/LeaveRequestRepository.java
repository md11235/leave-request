/**
 * 
 */
package com.leave.request.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.leave.request.model.ConstructionFlowRequest;

/**
 * @author Eraine
 *
 */
public interface LeaveRequestRepository extends JpaRepository<ConstructionFlowRequest, Long> {

	List<ConstructionFlowRequest> findAllByCreateBy(String createBy);
}
