package org.test.dao;

import java.util.List;

import org.test.dto.TicketType;

public interface ITicketTypeDao {

	public TicketType saveOrUpdate(TicketType ticketType);
	
	public List<TicketType> getTicketTypes();
	
	public TicketType getByType(String type);
}
