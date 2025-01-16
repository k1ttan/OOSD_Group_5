package com.ecom.service.impl;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;

import com.ecom.model.Ticket;
import com.ecom.repository.TicketRepository;
import com.ecom.repository.TripRepository;
import com.ecom.service.TicketService;

@Service
public class TicketServiceImpl implements TicketService {

	@Autowired
	private TicketRepository TicketRepository;

	@Override
	public Ticket saveTicket(Ticket Ticket) {
		return TicketRepository.save(Ticket);
	}
	
	@Override
	public List<Ticket> getAllTickets() {
		return TicketRepository.findAll();
	}

	@Override
	public Page<Ticket> getAllTicketsPagination(Integer pageNo, Integer pageSize) {
		Pageable pageable = PageRequest.of(pageNo, pageSize);
		return TicketRepository.findAll(pageable);
	}

	@Override
	public Boolean deleteTicket(Integer id) {
		Ticket Ticket = TicketRepository.findById(id).orElse(null);

		if (!ObjectUtils.isEmpty(Ticket)) {
			TicketRepository.delete(Ticket);
			return true;
		}
		return false;
	}

	@Override
	public Ticket getTicketById(Integer id) {
		Ticket Ticket = TicketRepository.findById(id).orElse(null);
		return Ticket;
	}

	@Override
	public Ticket updateTicket(Ticket Ticket) {

		Ticket dbTicket = getTicketById(Ticket.getId());


		dbTicket.setPrice(Ticket.getPrice());
		dbTicket.setQuantity(Ticket.getQuantity());

		Ticket updateTicket = TicketRepository.save(dbTicket);

		if (!ObjectUtils.isEmpty(updateTicket)) {
			return Ticket;
		}
		return null;
	}






}
