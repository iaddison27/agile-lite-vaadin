package org.test.factory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.test.AcceptanceCriteria;
import org.test.CreateTicketBackingBean;
import org.test.FileUpload;
import org.test.SubtaskBackingBean;
import org.test.dto.Attachment;
import org.test.dto.Bug;
import org.test.dto.Subtask;
import org.test.dto.SubtaskState;
import org.test.dto.Ticket;
import org.test.dto.TicketType;
import org.test.dto.UserStory;
import org.test.service.ITicketTypeService;

@RunWith(MockitoJUnitRunner.class)
public class TicketFactoryTest {
	
	@Mock
	private ITicketTypeService ticketTypeService;
	
	@InjectMocks
	private TicketFactory ticketFactory = new TicketFactory();
	
	@Test
	public void instantiateShouldReturnNullWhenNullTicketTypeSpecified() {
		CreateTicketBackingBean backingBean = new CreateTicketBackingBean();
		backingBean.setTicketType(new TicketType());
		assertNull(ticketFactory.instantiateTicket(backingBean, null, null));
	} 
	
	@Test
	public void instantiateShouldReturnNullWhenInvalidTicketTypeSpecified() {
		CreateTicketBackingBean backingBean = new CreateTicketBackingBean();
		backingBean.setTicketType(new TicketType("invalid", "invalid"));
		assertNull(ticketFactory.instantiateTicket(backingBean, null, null));
	}
	
	@Test
	public void instantiateShouldReturnBug() {
		Ticket expected = new Bug(null, 2, "Bug 1", "description for bug 1", createAttachments(1), null, 0);
		
		CreateTicketBackingBean backingBean = new CreateTicketBackingBean();
		backingBean.setAttachment(createFileUploads(1));
		backingBean.setDescription("description for bug 1");
		backingBean.setStoryPoints(2);
		backingBean.setTicketType(new TicketType("Bug", "bug"));
		backingBean.setTitle("Bug 1");
		
		assertEquals(expected, ticketFactory.instantiateTicket(backingBean, null, null));
	}
	
	@Test
	public void instantiateShouldReturnUserStory() {
		List<Subtask> expectedSubtasks = new ArrayList<>();
		expectedSubtasks.add(new Subtask(null, "Subtask 1", "description for subtask 1", createAttachments(1), 3, SubtaskState.TODO));
		expectedSubtasks.add(new Subtask(null, "Subtask 2", "description for subtask 2", null, 1, SubtaskState.TODO));
		Ticket expected = new UserStory(null, 8, "User Story 1", "description for user story 1", createAttachments(2), null, expectedSubtasks, 0);
		
		CreateTicketBackingBean backingBean = new CreateTicketBackingBean();
		backingBean.setAttachment(createFileUploads(2));
		backingBean.setDescription("description for user story 1");
		backingBean.setStoryPoints(8);
		backingBean.setTicketType(new TicketType("User Story", "story"));
		backingBean.setTitle("User Story 1");
		List<Subtask> subtasks = new ArrayList<>();
		subtasks.add(new Subtask(null, "Subtask 1", "description for subtask 1", createAttachments(1), 3, SubtaskState.TODO));
		subtasks.add(new Subtask(null, "Subtask 2", "description for subtask 2", null, 1, SubtaskState.TODO));
		backingBean.setTitle("User Story 1");
		assertEquals(expected, ticketFactory.instantiateTicket(backingBean, subtasks, null));
	}
	
	@Test
	public void instantiateShouldReturnUserStoryWithNoSubtasks() {
		Ticket expected = new UserStory(null, 8, "User Story 1", "description for user story 1", createAttachments(1), null, null, 0);
		
		CreateTicketBackingBean backingBean = new CreateTicketBackingBean();
		backingBean.setAttachment(createFileUploads(1));
		backingBean.setTicketType(new TicketType("User Story", "story"));
		backingBean.setDescription("description for user story 1");
		backingBean.setStoryPoints(8);
		backingBean.setSubtasks(null);
		backingBean.setTitle("User Story 1");
		assertEquals(expected, ticketFactory.instantiateTicket(backingBean, null, null));
	}
	
	@Test
	public void instantiateShouldReturnUserStoryWithAcceptanceCriteria() {
		Ticket expected = new UserStory(null, 8, "User Story 1", "description for user story 1", createAttachments(1), createAcceptanceCriteria(3), null, 0);
		
		CreateTicketBackingBean backingBean = new CreateTicketBackingBean();
		backingBean.setAcceptanceCriteria(createAcceptanceCriteriaForm(3));
		backingBean.setAttachment(createFileUploads(1));
		backingBean.setTicketType(new TicketType("User Story", "story"));
		backingBean.setDescription("description for user story 1");
		backingBean.setStoryPoints(8);
		backingBean.setSubtasks(null);
		backingBean.setTitle("User Story 1");
		assertEquals(expected, ticketFactory.instantiateTicket(backingBean, null, createAcceptanceCriteria(3)));
	}
	
	/* Create Ticket from CreateTicketBackingBean*/
	@Test
	public void createShouldReturnNullWhenNoTicketProvided() {
		assertNull(ticketFactory.createTicket(null));
	}
	
	@Test
	public void createShouldReturnNullWhenTicketTypeNotSet() {
		assertNull(ticketFactory.createTicket(new CreateTicketBackingBean()));
	}
	
	@Test
	public void createShouldReturnNullWhenNullTicketTypeSpecified() {
		CreateTicketBackingBean backingBean = new CreateTicketBackingBean();
		backingBean.setTicketType(new TicketType());
		assertNull(ticketFactory.createTicket(backingBean));
	}
	
	@Test
	public void createShouldReturnBug() {
		Ticket expected = new Bug(null, 2, "Bug 1", "description for bug 1", createAttachments(2), new ArrayList<Subtask>(), 0);
		
		CreateTicketBackingBean backingBean = new CreateTicketBackingBean();
		backingBean.setAttachment(createFileUploads(2));
		backingBean.setDescription("description for bug 1");
		backingBean.setStoryPoints(2);
		backingBean.setTicketType(new TicketType("Bug", "bug"));
		backingBean.setTitle("Bug 1");
		
		assertEquals(expected, ticketFactory.createTicket(backingBean));
	}
	
	@Test
	public void createShouldReturnUserStory() {
		List<Subtask> expectedSubtasks = new ArrayList<>();
		expectedSubtasks.add(new Subtask(null, "Subtask 1", "description for subtask 1", createAttachments(1), 3, SubtaskState.TODO));
		expectedSubtasks.add(new Subtask(null, "Subtask 2", "description for subtask 2", null, 1, SubtaskState.TODO));
		Ticket expected = new UserStory(null, 8, "User Story 1", "description for user story 1", createAttachments(3), createAcceptanceCriteria(2), expectedSubtasks, 0);
		
		CreateTicketBackingBean backingBean = new CreateTicketBackingBean();
		backingBean.setAcceptanceCriteria(createAcceptanceCriteriaForm(2));
		backingBean.setAttachment(createFileUploads(3));
		backingBean.setDescription("description for user story 1");
		backingBean.setStoryPoints(8);
		backingBean.setTicketType(new TicketType("User Story", "story"));
		backingBean.setTitle("User Story 1");
		List<SubtaskBackingBean> subtasks = new ArrayList<>();
		SubtaskBackingBean subtask = new SubtaskBackingBean();
		subtask.setAttachments(createFileUploads(1));
		subtask.setDescription("description for subtask 1");
		subtask.setEstimate(3);
		subtask.setTitle("Subtask 1");
		subtasks.add(subtask);
		subtask = new SubtaskBackingBean();
		subtask.setDescription("description for subtask 2");
		subtask.setEstimate(1);
		subtask.setTitle("Subtask 2");
		subtasks.add(subtask);
		backingBean.setSubtasks(subtasks);
		backingBean.setTitle("User Story 1");
		assertEquals(expected, ticketFactory.createTicket(backingBean));
	}
	
	@Test
	public void createShouldReturnUserStoryWithNoSubtasks() {
		Ticket expected = new UserStory(null, 8, "User Story 1", "description for user story 1", createAttachments(1), new ArrayList<String>(), new ArrayList<Subtask>(), 0);
		
		CreateTicketBackingBean backingBean = new CreateTicketBackingBean();
		backingBean.setAttachment(createFileUploads(1));
		backingBean.setTicketType(new TicketType("User Story", "story"));
		backingBean.setDescription("description for user story 1");
		backingBean.setStoryPoints(8);
		backingBean.setSubtasks(null);
		backingBean.setTitle("User Story 1");
		assertEquals(expected, ticketFactory.createTicket(backingBean));
	}
	
	/* Create CreateTicketBackingBean from Ticket*/
	@Test
	public void createTicketBackingBeanShouldReturnEmptyBackingBeanWhenNoTicketProvided() {
		assertEquals(new CreateTicketBackingBean(), ticketFactory.createTicketBackingBean(null));
	}
	
	@Test
	public void createTicketBackingBeanShouldReturnBug() {
		CreateTicketBackingBean expected = new CreateTicketBackingBean();
		expected.setDescription("description for bug 1");
		expected.setStoryPoints(2);
		expected.setSubtasks(new ArrayList<SubtaskBackingBean>());
		expected.setTicketType(new TicketType("Bug", "bug"));
		expected.setTitle("Bug 1");
		
		Ticket ticket = new Bug(null, 2, "Bug 1", "description for bug 1", createAttachments(1), new ArrayList<Subtask>(), 0);
		
		when(ticketTypeService.getByType("Bug")).thenReturn(new TicketType("Bug", "bug"));
		assertEquals(expected, ticketFactory.createTicketBackingBean(ticket));
		verify(ticketTypeService, times(1)).getByType("Bug");
	}
	
	@Test
	public void createTicketBackingBeanShouldReturnUserStory() {
		CreateTicketBackingBean expected = new CreateTicketBackingBean();
		expected.setDescription("description for user story 1");
		expected.setStoryPoints(8);
		expected.setTicketType(new TicketType("User Story", "story"));
		expected.setTitle("User Story 1");
		List<SubtaskBackingBean> expectedSubtasks = new ArrayList<>();
		SubtaskBackingBean subtask = new SubtaskBackingBean();
		subtask.setDescription("description for subtask 1");
		subtask.setEstimate(3);
		subtask.setTitle("Subtask 1");
		expectedSubtasks.add(subtask);
		subtask = new SubtaskBackingBean();
		subtask.setDescription("description for subtask 2");
		subtask.setEstimate(1);
		subtask.setTitle("Subtask 2");
		expectedSubtasks.add(subtask);
		expected.setSubtasks(expectedSubtasks);
		expected.setTitle("User Story 1");
		
		List<Subtask> subtasks = new ArrayList<>();
		subtasks.add(new Subtask(null, "Subtask 1", "description for subtask 1", null, 3, SubtaskState.TODO));
		subtasks.add(new Subtask(null, "Subtask 2", "description for subtask 2", createAttachments(2), 1, SubtaskState.TODO));
		Ticket ticket = new UserStory(null, 8, "User Story 1", "description for user story 1", null, null, subtasks, 0);
		
		when(ticketTypeService.getByType("User Story")).thenReturn(new TicketType("User Story", "story"));
		assertEquals(expected, ticketFactory.createTicketBackingBean(ticket));
		verify(ticketTypeService, times(1)).getByType("User Story");
	}
	
	@Test
	public void createTicketBackingBeanShouldReturnUserStoryWithNoSubtasks() {
		CreateTicketBackingBean expected = new CreateTicketBackingBean();
		expected.setTicketType(new TicketType("User Story", "story"));
		expected.setDescription("description for user story 1");
		expected.setStoryPoints(8);
		expected.setSubtasks(new ArrayList<SubtaskBackingBean>());
		expected.setTitle("User Story 1");
		
		Ticket ticket = new UserStory(null, 8, "User Story 1", "description for user story 1", createAttachments(1), null, new ArrayList<Subtask>(), 0);
		
		when(ticketTypeService.getByType("User Story")).thenReturn(new TicketType("User Story", "story"));
		assertEquals(expected, ticketFactory.createTicketBackingBean(ticket));
		verify(ticketTypeService, times(1)).getByType("User Story");
	}
	
	private List<FileUpload> createFileUploads(int number) {
		List<FileUpload> attachments = new ArrayList<>(number);
		for (int i = 0; i < number; i++) {
			attachments.add(new FileUpload("file_" + i + ".bmp", new ByteArrayOutputStream()));
		}
		return attachments;
	}
	
	private List<Attachment> createAttachments(int number) {
		List<Attachment> attachments = new ArrayList<>(number);
		for (int i = 0; i < number; i++) {
			attachments.add(new Attachment("file_" + i + ".bmp", new byte[]{}));
		}
		return attachments;
	}
	
	private List<AcceptanceCriteria> createAcceptanceCriteriaForm(int number) {
		List<AcceptanceCriteria> acceptanceCriteria = new ArrayList<>(number);
		for (int i = 0; i < number; i++) {
			acceptanceCriteria.add(new AcceptanceCriteria("acceptance criteria " + i));
		}
		return acceptanceCriteria;
	}
	
	private List<String> createAcceptanceCriteria(int number) {
		List<String> acceptanceCriteria = new ArrayList<>(number);
		for (int i = 0; i < number; i++) {
			acceptanceCriteria.add("acceptance criteria " + i);
		}
		return acceptanceCriteria;
	}
}
