package com.examTecnico.Back.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.examTecnico.Back.Models.OrderAssignment;
import java.util.UUID;

@Repository
public interface OrderAssignmentRepository extends JpaRepository<OrderAssignment, UUID> {
	
}

