package com.ecom.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ecom.model.TicketOrder;

public interface TicketOrderRepository extends JpaRepository<TicketOrder, Integer> {

	List<TicketOrder> findByUserId(Integer userId);

	TicketOrder findByOrderId(String orderId);

}
