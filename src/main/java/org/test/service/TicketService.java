package org.test.service;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.NoResultException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.test.CreateTicketBackingBean;
import org.test.dao.ITicketDao;
import org.test.dto.Subtask;
import org.test.dto.SubtaskState;
import org.test.dto.Ticket;
import org.test.dto.TicketLocation;
import org.test.factory.ITicketFactory;

@Service
public class TicketService implements ITicketService {

	@Autowired
	private ITicketDao ticketDao;
	
	@Autowired
	private ITicketFactory ticketFactory;

	@Override
	public List<Ticket> getAllTickets() {
		return ticketDao.getAllTickets();
	}
	
	@Override
	public List<Ticket> getAllTicketsByLocation(TicketLocation location) {
		return ticketDao.getAllTicketsByLocation(location);
	}
	
	@Override
	public Ticket getById(Long id) {
		try {
			return ticketDao.getById(id);
		} catch (NoResultException e) {
			return null;
		}
	}

	@Override
	@Transactional
	public void saveOrUpdate(Ticket ticket) {
		ticketDao.saveOrUpdate(ticket);

	}

	@Override
	@Transactional
	public void moveTicket(Ticket ticketToMove, Ticket ticketToAddAfter, String beforeOrAfter) {
		// Crude implementation - always do a reindex
		ticketDao.reindex();
		
		// Reload the ticket (as re-indexing will have updated the version number)
		ticketToMove = ticketDao.getById(ticketToMove.getId());
		// Modify the order (as we've done a reindex this will always be ok)
		if (ticketToAddAfter == null) {
			ticketToMove.setOrder(1);
		} else {
			ticketToAddAfter = ticketDao.getById(ticketToAddAfter.getId());
			if ("before".equals(beforeOrAfter)) {
				ticketToMove.setOrder(ticketToAddAfter.getOrder() - 50);
			} else {
				ticketToMove.setOrder(ticketToAddAfter.getOrder() + 50);
			}
			
		}
		ticketDao.saveOrUpdate(ticketToMove);
	}

	@Override
	@Transactional
	public void createOrUpdateTicket(CreateTicketBackingBean ticket) {
		if (ticket != null && ticket.getTicketType() != null) {
			Ticket newTicket = ticketFactory.createTicket(ticket);
			
			if (newTicket != null) {
				Integer order = ticketDao.getLargestOrderValue();
				if (order == null) {
					order = Integer.valueOf(0);
				}
				order += 100;
				newTicket.setOrder(order);
				ticketDao.saveOrUpdate(newTicket);
			}
		}
	}

	@Override
	public List<Subtask> getSubtasksByState(SubtaskState state) {
		List<Subtask> results = new ArrayList<>();
		for (Ticket ticket : ticketDao.getAllTicketsByLocation(TicketLocation.CURRENT_SPRINT)) {
			if (ticket.getSubtasks() != null) {
				for (Subtask subtask : ticket.getSubtasks()) {
					if (subtask.getState() == state) {
						results.add(subtask);
					}
				}
			}
		}
		return results;
	}

	@Override
	@Transactional
	public void updateSubtaskState(Subtask subtask, SubtaskState state) {
		subtask.setState(state);
		
		boolean allComplete = true;
		if (subtask.getParent() != null && subtask.getParent().getSubtasks() != null) {
			for (Subtask s : subtask.getParent().getSubtasks()) {
				if (s.getState() != SubtaskState.DONE) {
					allComplete = false;
					break;
				}
			}
		}
		if (allComplete) {
			subtask.getParent().setDone(Boolean.TRUE);
		}
		ticketDao.saveOrUpdate(subtask.getParent());
	}

	@Override
	@Transactional
	public void updateLocation(Ticket ticket, TicketLocation location) {
		ticket.setLocation(location);
		ticketDao.saveOrUpdate(ticket);
	}
	
	@Override
	public int sumStoryPoints(List<Ticket> tickets) {
		int sum = 0;
		if (tickets != null) {
			for (Ticket ticket : tickets) {
				sum += ticket.getStoryPoints();
			}
			
		}
		return sum;
	}

}
