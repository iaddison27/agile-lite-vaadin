package org.test.service;

import java.util.List;

import org.test.dto.TicketType;

public interface ITicketTypeService {

	public void addTicketType(TicketType ticketType);
	
	public TicketType getByType(String type);
	
	public List<TicketType> getTicketTypes();
}
