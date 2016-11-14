package org.test.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.springframework.stereotype.Repository;
import org.test.dto.Subtask;
import org.test.dto.SubtaskState;
import org.test.dto.Ticket;
import org.test.dto.TicketLocation;

@Repository
public class TicketDao implements ITicketDao {

	@PersistenceContext
    private EntityManager manager;
	
	public TicketDao() {
		super();
	}
	
	@Override
	public Ticket getById(Long id) throws NoResultException {
		Query query = manager.createNamedQuery("Ticket.getById");
		query.setParameter("id", id);
        return (Ticket) query.getSingleResult();
	}
	
	@Override
	public List<Ticket> getAllTickets() {
		Query query = manager.createNamedQuery("Ticket.findAll");
        return (List<Ticket>) query.getResultList();
	}
	
	@Override
	public List<Ticket> getAllTicketsByLocation(TicketLocation location) {
		Query query = manager.createNamedQuery("Ticket.findAllByLocation");
		query.setParameter("location", location);
		return (List<Ticket>) query.getResultList();
	}

	@Override
	public Ticket saveOrUpdate(Ticket ticket) {
		return manager.merge(ticket);
	}
	
	@Override
	public Subtask saveOrUpdate(Subtask subtask) {
		return manager.merge(subtask);
	}

	@Override
	public List<Subtask> getSubtasksByState(SubtaskState state) {
		Query query = manager.createNamedQuery("Subtask.findByState");
		query.setParameter("state", state);
        return (List<Subtask>) query.getResultList();
	}
	
	@Override
	public Integer getLargestOrderValue() {
		Query query = manager.createNamedQuery("Ticket.findLargestOrderValue");
		return (Integer) query.getSingleResult();
	}
	
	@Override
	public void reindex() {
		int order = 100;
		for (Ticket ticket : getAllTickets()) {
			ticket.setOrder(order);
			order += 100;
			saveOrUpdate(ticket);
		}
	}

	@Override
	public boolean allSubtasksDone(Long id) {
		Query query = manager.createNamedQuery("Subtask.allSubtasksDone");
		query.setParameter("id", id);
		return (boolean) query.getSingleResult();
	}

}
