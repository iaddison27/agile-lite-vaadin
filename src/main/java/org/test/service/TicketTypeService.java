package org.test.service;

import java.util.List;

import javax.persistence.NoResultException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.test.dao.ITicketTypeDao;
import org.test.dto.TicketType;

@Service
public class TicketTypeService implements ITicketTypeService {

	@Autowired
	private ITicketTypeDao ticketTypeDao;
	
	public TicketTypeService() {
		super();
	}
	
	@Override
	@Transactional
	public void addTicketType(TicketType ticketType) {
		ticketTypeDao.saveOrUpdate(ticketType);
	}

	@Override
	public List<TicketType> getTicketTypes() {
		return ticketTypeDao.getTicketTypes();
	}
	
	@Override
	public TicketType getByType(String type) {
		try {
			return ticketTypeDao.getByType(type);
		} catch (NoResultException e) {
			return null;
		}
	}

}
