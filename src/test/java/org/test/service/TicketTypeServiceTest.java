package org.test.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.NoResultException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.test.dao.ITicketTypeDao;
import org.test.dto.TicketType;


@RunWith(MockitoJUnitRunner.class)
public class TicketTypeServiceTest {

	@Mock
	private ITicketTypeDao ticketTypeDao;
	
	@Autowired
	@InjectMocks
	private TicketTypeService ticketTypeService;
	
	@Test
	public void shouldReturnAllTicketTypes() {
		List<TicketType> expected = new ArrayList<>();
		expected.add(new TicketType("A", "a.png"));
		expected.add(new TicketType("B", "b.png"));
		
		when(ticketTypeDao.getTicketTypes()).thenReturn(expected);
		
		assertEquals(expected, ticketTypeService.getTicketTypes());
		
		verify(ticketTypeDao, times(1)).getTicketTypes();
	}
	
	@Test
	public void shouldCallDaoSaveOrUpdate() {
		ticketTypeService.addTicketType(new TicketType("A", "a.png"));
		
		verify(ticketTypeDao, times(1)).saveOrUpdate(new TicketType("A", "a.png"));
	}
	
	@Test
	public void shouldCallDaoGetByType() {
		TicketType expected = new TicketType("A", "a.png");
		when(ticketTypeDao.getByType("A")).thenReturn(expected);
		
		assertEquals(expected, ticketTypeService.getByType("A"));
		
		verify(ticketTypeDao, times(1)).getByType("A");
	}
	
	@Test
	public void shouldHandleDaoGetByTypeException() {
		when(ticketTypeDao.getByType("A")).thenThrow(new NoResultException());
		
		assertNull(ticketTypeService.getByType("A"));
		
		verify(ticketTypeDao, times(1)).getByType("A");
	}

}
