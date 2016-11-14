package org.test.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doAnswer;
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
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.test.CreateTicketBackingBean;
import org.test.dao.ITicketDao;
import org.test.dto.Attachment;
import org.test.dto.Bug;
import org.test.dto.Subtask;
import org.test.dto.SubtaskState;
import org.test.dto.Ticket;
import org.test.dto.TicketLocation;
import org.test.dto.TicketType;
import org.test.dto.UserStory;
import org.test.factory.ITicketFactory;


@RunWith(MockitoJUnitRunner.class)
public class TicketServiceTest {

	@Mock
	private ITicketDao ticketDao;
	
	@Mock
	private ITicketFactory ticketFactory;
	
	@Autowired
	@InjectMocks
	private TicketService ticketService;
	
	@Test
	public void shouldSaveTicket() {		
		ticketService.saveOrUpdate(new UserStory(null, 3, "User Story 1", "description for user story 1", createAttachments(1), null, null, 1));
		verify(ticketDao, times(1)).saveOrUpdate(new UserStory(null, 3, "User Story 1", "description for user story 1", createAttachments(1), null, null, 1));
	}
	
	@Test
	public void shouldReturnAllTickets() {
		List<Ticket> expected = new ArrayList<>();
		expected.add(new UserStory(null, 3, "User Story 1", "description for user story 1", createAttachments(1), null, null, 1));
		expected.add(new Bug(null, 1, "Bug 1", "description for bug 1", null, null, 2));
		
		when(ticketDao.getAllTickets()).thenReturn(expected);
		
		assertEquals(expected, ticketService.getAllTickets());
		
		verify(ticketDao, times(1)).getAllTickets();
	}
	
	@Test
	public void shouldReturnAllTicketsByLocation() {
		List<Ticket> expected = new ArrayList<>();
		expected.add(new UserStory(null, 3, "User Story 1", "description for user story 1", null, null, null, 1));
		expected.add(new Bug(null, 1, "Bug 1", "description for bug 1", createAttachments(2), null, 2));
		
		when(ticketDao.getAllTicketsByLocation(TicketLocation.BACKLOG)).thenReturn(expected);
		
		assertEquals(expected, ticketService.getAllTicketsByLocation(TicketLocation.BACKLOG));
		
		verify(ticketDao, times(1)).getAllTicketsByLocation(TicketLocation.BACKLOG);
	}
	
	@Test
	public void shouldReturnTicketWithTheSpecifiedId() {
		Ticket expected = new UserStory(null, 3, "User Story 1", "description for user story 1", createAttachments(2), null, null, 1);
		
		when(ticketDao.getById(5L)).thenReturn(expected);
		
		assertEquals(expected, ticketService.getById(5L));
		
		verify(ticketDao, times(1)).getById(5L);
	}
	
	@Test
	public void shouldReturnNullWhenNoTicketExistsWithTheSpecifiedId() {
		when(ticketDao.getById(5L)).thenThrow(NoResultException.class);
		
		assertNull(ticketService.getById(5L));
		
		verify(ticketDao, times(1)).getById(5L);
	}
	
	@Test
	public void createOrUpdateTicketShouldHandleNullBackingBean() {
		ticketService.createOrUpdateTicket(null);
		
		// Verify no methods called
		verify(ticketFactory, times(0)).createTicket(any(CreateTicketBackingBean.class));
		verify(ticketDao, times(0)).getLargestOrderValue();
		verify(ticketDao, times(0)).saveOrUpdate(any(Ticket.class));
	}
	
	@Test
	public void createOrUpdateTicketShouldHandleBackingBeanWithNullTicketType() {
		ticketService.createOrUpdateTicket(new CreateTicketBackingBean());
		
		// Verify no methods called
		verify(ticketFactory, times(0)).createTicket(any(CreateTicketBackingBean.class));
		verify(ticketDao, times(0)).getLargestOrderValue();
		verify(ticketDao, times(0)).saveOrUpdate(any(Ticket.class));
	}
	
	@Test
	public void createOrUpdateTicketShouldHandleNullResultFromTicketFactory_CreateTicket() {
		CreateTicketBackingBean backingBean = new CreateTicketBackingBean();
		backingBean.setTicketType(new TicketType("Invalid Type", "invalid"));
		
		when(ticketFactory.createTicket(backingBean)).thenReturn(null);
		
		ticketService.createOrUpdateTicket(backingBean);
		
		// Verify no methods called
		verify(ticketFactory, times(1)).createTicket(backingBean);
		verify(ticketDao, times(0)).getLargestOrderValue();
		verify(ticketDao, times(0)).saveOrUpdate(any(Ticket.class));
	}
	
	@Test
	public void createOrUpdateTicketShouldSetOrderCorrectlyWhenCreatedTicketsOrderIsNull() {
		CreateTicketBackingBean backingBean = new CreateTicketBackingBean();
		backingBean.setTicketType(new TicketType("Invalid Type", "invalid"));
		
		when(ticketFactory.createTicket(backingBean)).thenReturn(new Bug(null, 2, "Bug 1", "bug 1 description", null, null, null));
		when(ticketDao.getLargestOrderValue()).thenReturn(null);
		
		ticketService.createOrUpdateTicket(backingBean);
		
		// Verify no methods called
		verify(ticketFactory, times(1)).createTicket(backingBean);
		verify(ticketDao, times(1)).getLargestOrderValue();
		verify(ticketDao, times(1)).saveOrUpdate(new Bug(null, 2, "Bug 1", "bug 1 description", null, null, Integer.valueOf(100)));
	}
	
	@Test
	public void createOrUpdateTicketShouldSetOrderCorrectlyWhenCreatedTicketsOrderIsNotNull() {
		CreateTicketBackingBean backingBean = new CreateTicketBackingBean();
		backingBean.setTicketType(new TicketType("Invalid Type", "invalid"));
		
		when(ticketFactory.createTicket(backingBean)).thenReturn(new Bug(null, 2, "Bug 1", "bug 1 description", null, null, null));
		when(ticketDao.getLargestOrderValue()).thenReturn(Integer.valueOf(350));
		
		ticketService.createOrUpdateTicket(backingBean);
		
		// Verify no methods called
		verify(ticketFactory, times(1)).createTicket(backingBean);
		verify(ticketDao, times(1)).getLargestOrderValue();
		verify(ticketDao, times(1)).saveOrUpdate(new Bug(null, 2, "Bug 1", "bug 1 description", null, null, Integer.valueOf(450)));
	}
	
	@Test
	public void shouldUpdateSubtaskStateWhenSubtasksAreAllDone() {
		Bug parent = new Bug(1L, 3, "Bug 1", "description for bug 1", null, null, 1);
		List<Subtask> subtasks = new ArrayList<>();
		Subtask subtask = new Subtask();
		subtask.setState(SubtaskState.TODO);
		subtask.setParent(parent);
		subtasks.add(subtask);
		subtask = new Subtask();
		subtask.setState(SubtaskState.DONE);
		subtask.setParent(parent);
		subtasks.add(subtask);
		parent.setSubtasks(subtasks);
		
		//when(ticketDao.allSubtasksDone(1L)).thenReturn(true);
		
		ticketService.updateSubtaskState(subtask, SubtaskState.DONE);
		
		assertEquals(SubtaskState.DONE, subtask.getState());
		
		verify(ticketDao, times(1)).saveOrUpdate(subtask.getParent());
	}
	
	@Test
	public void shouldUpdateSubtaskStateWhenSubtasksAreNotAllDone() {
		Bug parent = new Bug(1L, 3, "Bug 1", "description for bug 1", null, null, 1);
		List<Subtask> subtasks = new ArrayList<>();
		Subtask subtask = new Subtask();
		subtask.setState(SubtaskState.TODO);
		subtask.setParent(parent);
		subtasks.add(subtask);
		parent.setSubtasks(subtasks);
		
		//when(ticketDao.allSubtasksDone(1L)).thenReturn(false);
		
		ticketService.updateSubtaskState(subtask, SubtaskState.DONE);
		
		assertEquals(SubtaskState.DONE, subtask.getState());
		
		verify(ticketDao, times(1)).saveOrUpdate(subtask.getParent());
	}
	
	@Test
	public void shouldMoveToBack() {
		Ticket ticket1 = new Bug(1L, 1, "bug 1", "description 1", null, null, 100);
		Ticket ticket2 = new Bug(2L, 2, "bug 2", "description 2", null, null, 200);
		
		when(ticketDao.getById(1L)).thenReturn(ticket1);
		when(ticketDao.getById(2L)).thenReturn(ticket2);
		
		ticketService.moveTicket(ticket1, ticket2, "after");
		
		assertTrue(ticket1.getOrder() > ticket2.getOrder());
	}
	
	@Test
	public void shouldMoveToFront() {
		Ticket ticket1 = new Bug(1L, 1, "bug 1", "description 1", null, null, 100);
		Ticket ticket2 = new Bug(2L, 2, "bug 2", "description 2", null, null, 200);
		
		when(ticketDao.getById(2L)).thenReturn(ticket2);
		
		ticketService.moveTicket(ticket2, null, "before");
		
		assertTrue(ticket2.getOrder() < ticket1.getOrder());
	}
	
	@Test
	public void shouldMoveToMiddle() {
		Ticket ticket1 = new Bug(1L, 1, "bug 1", "description 1", null, null, 100);
		Ticket ticket2 = new Bug(2L, 2, "bug 2", "description 2", null, null, 200);
		Ticket ticket3 = new Bug(3L, 3, "bug 3", "description 3", null, null, 300);
		
		when(ticketDao.getById(3L)).thenReturn(ticket3);
		when(ticketDao.getById(1L)).thenReturn(ticket1);
		
		ticketService.moveTicket(ticket3, ticket1, "after");
		
		assertTrue(ticket1.getOrder() < ticket3.getOrder());
		assertTrue(ticket3.getOrder() < ticket2.getOrder());
	}
	
	@Test
	public void shouldHandleMoveWhenOrderContainsNoGaps() {
		Ticket ticket1 = new Bug(1L, 1, "bug 1", "description 1", null, null, 1);
		Ticket ticket2 = new Bug(2L, 2, "bug 2", "description 2", null, null, 2);
		Ticket ticket3 = new Bug(3L, 3, "bug 3", "description 3", null, null, 3);
		
		// Mock the behaviour of ITicketDao.reindex()
		doAnswer(new Answer<Void>() {
		    public Void answer(InvocationOnMock invocation) {
		    	ticket1.setOrder(100);
		    	ticket2.setOrder(200);
		    	ticket3.setOrder(300);
		    	return null;
		    }
		}).when(ticketDao).reindex();
		
		when(ticketDao.getById(3L)).thenReturn(ticket3);
		when(ticketDao.getById(1L)).thenReturn(ticket1);
		
		ticketService.moveTicket(ticket3, ticket1, "after");
		
		verify(ticketDao, times(1)).reindex();
		
		assertTrue(ticket1.getOrder() < ticket3.getOrder());
		assertTrue(ticket3.getOrder() < ticket2.getOrder());
	}
	
	@Test
	public void shouldUpdateTicketLocation() {
		Ticket ticket = new Bug(1L, 1, "bug 1", "description 1", null, null, 1);
		
		assertEquals(TicketLocation.BACKLOG, ticket.getLocation());
		
		ticketService.updateLocation(ticket, TicketLocation.CURRENT_SPRINT);
		
		verify(ticketDao, times(1)).saveOrUpdate(ticket);
		
		assertEquals(TicketLocation.CURRENT_SPRINT, ticket.getLocation());
	}
	
	@Test
	public void shouldGetEmptyListWhenNoSubtasksMatchingSpecifiedState() {
		List<Ticket> tickets = new ArrayList<>();
		tickets.add(new Bug(1L, 1, "bug 1", "description 1", null, null, 1));
		tickets.add(new Bug(2L, 1, "bug 2", "description 2", null, null, 2));
		tickets.add(new Bug(3L, 1, "bug 3", "description 3", null, null, 3));
		
		when(ticketDao.getAllTicketsByLocation(TicketLocation.CURRENT_SPRINT)).thenReturn(tickets);
		
		List<Subtask> expected = new ArrayList<>();
		assertEquals(expected, ticketDao.getSubtasksByState(SubtaskState.DONE));
	}
	
	@Test
	public void shouldGetSubtasksMatchingSpecifiedState() {
		List<Ticket> tickets = new ArrayList<>();
		Bug ticket = new Bug(1L, 1, "bug 1", "description 1", null, null, 1);
		List<Subtask> subtasks = new ArrayList<>();
		subtasks.add(new Subtask(1L, "bug 1 subtask 1", "description 1", null, 8, SubtaskState.TODO));
		subtasks.add(new Subtask(2L, "bug 1 subtask 2", "description 2", null, 20, SubtaskState.DONE));
		ticket.setSubtasks(subtasks);
		tickets.add(ticket);
		ticket = new Bug(2L, 2, "bug 2", "description 2", null, null, 2);
		subtasks = new ArrayList<>();
		subtasks.add(new Subtask(1L, "bug 2 subtask 1", "description 1", null, 1, SubtaskState.IN_PROGRESS));
		subtasks.add(new Subtask(2L, "bug 2 subtask 2", "description 2", null, 5, SubtaskState.DONE));
		ticket.setSubtasks(subtasks);
		tickets.add(ticket);
		
		when(ticketDao.getAllTicketsByLocation(TicketLocation.CURRENT_SPRINT)).thenReturn(tickets);
		
		List<Subtask> expected = new ArrayList<>();
		expected.add(new Subtask(2L, "bug 1 subtask 2", "description 2", null, 20, SubtaskState.DONE));
		expected.add(new Subtask(2L, "bug 2 subtask 2", "description 2", null, 5, SubtaskState.DONE));
		assertEquals(expected, ticketService.getSubtasksByState(SubtaskState.DONE));
	}
	
	@Test
	public void shouldSumStoryPointsCorrectly() {
		List<Ticket> tickets = new ArrayList<>();
		tickets.add(new Bug(1L, 1, "bug 1", "description 1", null, null, 1));
		tickets.add(new UserStory(null, 8, "User Story 1", "description for user story 1", null, null, null, 1));
		tickets.add(new UserStory(null, 3, "User Story 1", "description for user story 1", null, null, null, 1));
		tickets.add(new Bug(1L, 20, "bug 1", "description 1", null, null, 1));
		
		assertEquals(32, ticketService.sumStoryPoints(tickets));
	}
	
	@Test
	public void shouldSumStoryPointsCorrectlyWhenTicketListIsNull() {
		assertEquals(0, ticketService.sumStoryPoints(null));
	}
	
	private List<Attachment> createAttachments(int number) {
		List<Attachment> attachments = new ArrayList<>(number);
		for (int i = 0; i < number; i++) {
			attachments.add(new Attachment("file_" + i + ".bmp", new byte[]{}));
		}
		return attachments;
	}

}
