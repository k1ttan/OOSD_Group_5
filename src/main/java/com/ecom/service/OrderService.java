package com.ecom.service;

import java.util.List;

import org.springframework.data.domain.Page;

import com.ecom.model.OrderRequest;
import com.ecom.model.TicketOrder;

public interface OrderService {

	public void saveOrder(Integer userid, OrderRequest orderRequest) throws Exception;

	public List<TicketOrder> getOrdersByUser(Integer userId);

	public TicketOrder updateOrderStatus(Integer id, String status);

	public List<TicketOrder> getAllOrders();

	public TicketOrder getOrdersByOrderId(String orderId);
	
	public Page<TicketOrder> getAllOrdersPagination(Integer pageNo,Integer pageSize);
}
