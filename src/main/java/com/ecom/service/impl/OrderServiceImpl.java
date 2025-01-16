package com.ecom.service.impl;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.ecom.model.OrderAddress;
import com.ecom.model.OrderRequest;
import com.ecom.model.TicketOrder;
import com.ecom.repository.TicketOrderRepository;
import com.ecom.service.OrderService;
//import com.ecom.util.CommonUtil;
import com.ecom.util.OrderStatus;

@Service
public class OrderServiceImpl implements OrderService {

	@Autowired
	private TicketOrderRepository orderRepository;

//	@Autowired
//	private CommonUtil commonUtil;

	@Override
    public void saveOrder(Integer userId, OrderRequest orderRequest) throws Exception {
        TicketOrder order = new TicketOrder();

        order.setOrderId(UUID.randomUUID().toString());
        order.setOrderDate(LocalDate.now());
        order.setTicket(orderRequest.getTicket()); // Lấy thông tin vé từ OrderRequest
        order.setPrice(orderRequest.getTicket().getPrice());
        order.setQuantity(orderRequest.getQuantity());
        order.setUser(orderRequest.getUser());

        order.setStatus(OrderStatus.IN_PROGRESS.getName());
        order.setPaymentType(orderRequest.getPaymentType());

        OrderAddress address = new OrderAddress();
        address.setFirstName(orderRequest.getFirstName());
        address.setLastName(orderRequest.getLastName());
        address.setEmail(orderRequest.getEmail());
        address.setMobileNo(orderRequest.getMobileNo());
        address.setAddress(orderRequest.getAddress());
        address.setCity(orderRequest.getCity());
        address.setState(orderRequest.getState());
        address.setPincode(orderRequest.getPincode());
        order.setOrderAddress(address);

        orderRepository.save(order);
    }

	@Override
	public List<TicketOrder> getOrdersByUser(Integer userId) {
		List<TicketOrder> orders = orderRepository.findByUserId(userId);
		return orders;
	}

	@Override
	public TicketOrder updateOrderStatus(Integer id, String status) {
		Optional<TicketOrder> findById = orderRepository.findById(id);
		if (findById.isPresent()) {
			TicketOrder TicketOrder = findById.get();
			TicketOrder.setStatus(status);
			TicketOrder updateOrder = orderRepository.save(TicketOrder);
			return updateOrder;
		}
		return null;
	}

	@Override
	public List<TicketOrder> getAllOrders() {
		return orderRepository.findAll();
	}

	@Override
	public Page<TicketOrder> getAllOrdersPagination(Integer pageNo, Integer pageSize) {
		Pageable pageable = PageRequest.of(pageNo, pageSize);
		return orderRepository.findAll(pageable);

	}

	@Override
	public TicketOrder getOrdersByOrderId(String orderId) {
		return orderRepository.findByOrderId(orderId);
	}

}
