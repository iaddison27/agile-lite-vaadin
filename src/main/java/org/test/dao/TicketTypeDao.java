package org.test.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.springframework.stereotype.Repository;
import org.test.dto.TicketType;

@Repository
public class TicketTypeDao implements ITicketTypeDao {

	@PersistenceContext
    private EntityManager manager;
	
	public TicketTypeDao() {
		super();
		System.out.println("TicketTypeDao constructor");
	}
	
	@Override
	public TicketType saveOrUpdate(TicketType ticketType) {
		return manager.merge(ticketType);
	}
	
	@Override
	public List<TicketType> getTicketTypes() {
		 Query query = manager.createNamedQuery("TicketType.findAll");            
         return (List<TicketType>) query.getResultList();
	}
	
	@Override
	public TicketType getByType(String type) {
		Query query = manager.createNamedQuery("TicketType.getByType");
		query.setParameter("type", type);
        return (TicketType) query.getSingleResult();
	}

}
