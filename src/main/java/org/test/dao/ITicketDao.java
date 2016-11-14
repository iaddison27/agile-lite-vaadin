package org.test.dao;

import java.util.List;

import org.test.dto.Subtask;
import org.test.dto.SubtaskState;
import org.test.dto.Ticket;
import org.test.dto.TicketLocation;

public interface ITicketDao {

	public List<Ticket> getAllTickets();
	
	public List<Ticket> getAllTicketsByLocation(TicketLocation location);
	
	public Ticket getById(Long id);
	
	public Ticket saveOrUpdate(Ticket ticket);
	
	public Subtask saveOrUpdate(Subtask subtask);
	
	public List<Subtask> getSubtasksByState(SubtaskState state);
	
	public Integer getLargestOrderValue();
	
	public void reindex();
	
	public boolean allSubtasksDone(Long id);
}
