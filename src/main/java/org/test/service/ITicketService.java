package org.test.service;

import java.util.List;

import org.test.CreateTicketBackingBean;
import org.test.dto.Subtask;
import org.test.dto.SubtaskState;
import org.test.dto.Ticket;
import org.test.dto.TicketLocation;

public interface ITicketService {

	public List<Ticket> getAllTickets();
	
	public List<Ticket> getAllTicketsByLocation(TicketLocation location);
	
	public Ticket getById(Long id);
	
	public void saveOrUpdate(Ticket ticket);
	
	public void moveTicket(Ticket ticketToMove, Ticket ticketToAddAfter, String beforeOrAfter);
	
	public void createOrUpdateTicket(CreateTicketBackingBean ticket);
	
	public List<Subtask> getSubtasksByState(SubtaskState state);

	public void updateSubtaskState(Subtask itemMoved, SubtaskState state);
	
	public void updateLocation(Ticket ticket, TicketLocation location);

	public int sumStoryPoints(List<Ticket> tickets);
	
}
