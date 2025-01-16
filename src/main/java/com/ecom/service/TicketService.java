package com.ecom.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import com.ecom.model.Ticket;

public interface TicketService {

	public Ticket saveTicket(Ticket Ticket);

	public Boolean deleteTicket(Integer id);

	public Ticket getTicketById(Integer id);

	public Ticket updateTicket(Ticket Ticket);

	public Page<Ticket> getAllTicketsPagination(Integer pageNo, Integer pageSize);


	List<Ticket> getAllTickets();

}
