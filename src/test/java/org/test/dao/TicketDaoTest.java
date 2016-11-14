package org.test.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.NoResultException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;
import org.test.dto.Attachment;
import org.test.dto.Bug;
import org.test.dto.Subtask;
import org.test.dto.SubtaskState;
import org.test.dto.Ticket;
import org.test.dto.TicketLocation;
import org.test.dto.UserStory;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:repositoryTest-context.xml" })
public class TicketDaoTest {

	@Autowired
	protected TicketDao ticketDao;
	
	@Test
	@Transactional
    @Rollback(true)
	public final void shouldReturnAllTickets() throws Exception {
		// Test data
		List<Ticket> expected = new ArrayList<>();
		
		expected.add(createAndPersistBug(null, 3, "Bug 1", "description for bug 1", createAttachments(1), 1, null, null));
		expected.add(createAndPersistUserStory(null, 5, "User Story 1", "description for user story 1", createAttachments(2), 2, null));
		expected.add(createAndPersistBug(null, 8, "Bug 2", "description for bug 2", null, 3, null, null));
		expected.add(createAndPersistUserStory(null, 13, "User Story 2", "description for user story 2", createAttachments(1), 4, null));
		
		assertEquals(expected, ticketDao.getAllTickets());
	}
	
	@Test
	@Transactional
    @Rollback(true)
	public final void shouldReturnAllTodoSubtasks() throws Exception {
		// Test data
		List<Subtask> expected = new ArrayList<>();
		
		createAndPersistBug(null, 3, "Bug 1", "description for bug 1", createAttachments(1), 1, null, null);
		createAndPersistUserStory(null, 5, "User Story 1", "description for user story 1", createAttachments(1), 2, null);
		createAndPersistSubtask(null, "Subtask 1", "description for subtask 1", createAttachments(1), 0, SubtaskState.DONE);
		expected.add(createAndPersistSubtask(null, "Subtask 2", "description for subtask 2", null, 7, SubtaskState.TODO));
		expected.add(createAndPersistSubtask(null, "Subtask 3", "description for subtask 3", createAttachments(1), 5, SubtaskState.TODO));
		createAndPersistSubtask(null, "Subtask 4", "description for subtask 4", null, 1, SubtaskState.IN_PROGRESS);
		
		assertEquals(expected, ticketDao.getSubtasksByState(SubtaskState.TODO));
	}
	
	@Test
	@Transactional
    @Rollback(true)
	public final void shouldReturnAllTicketsByLocation() throws Exception {
		// Test data
		List<Ticket> expectedBacklog = new ArrayList<>();
		List<Ticket> expectedCurrentSprint = new ArrayList<>();
		
		expectedCurrentSprint.add(createAndPersistBug(null, 3, "Bug 1", "description for bug 1", createAttachments(1), 1, null, TicketLocation.CURRENT_SPRINT));
		expectedBacklog.add(createAndPersistUserStory(null, 5, "User Story 1", "description for user story 1", null, 2, null));
		expectedBacklog.add(createAndPersistBug(null, 8, "Bug 2", "description for bug 2", null, 3, null, null));
		expectedCurrentSprint.add(createAndPersistUserStory(null, 13, "User Story 2", "description for user story 2", null, 4, TicketLocation.CURRENT_SPRINT));
		
		assertEquals(expectedBacklog, ticketDao.getAllTicketsByLocation(TicketLocation.BACKLOG));
		assertEquals(expectedCurrentSprint, ticketDao.getAllTicketsByLocation(TicketLocation.CURRENT_SPRINT));
	}
	
	@Test
	@Transactional
    @Rollback(true)
	public final void shouldReturnTicketWithTheSpecifiedId() throws Exception {
		// Test data
		createAndPersistBug(null, 3, "Bug 1", "description for bug 1", null, 1, null, null);
		createAndPersistUserStory(null, 5, "User Story 1", "description for user story 1", createAttachments(1), 2, null);
		Ticket expected = createAndPersistBug(null, 8, "Bug 2", "description for bug 2", null, 3, null, null);
		createAndPersistUserStory(null, 13, "User Story 2", "description for user story 2", createAttachments(1), 4, null);
		
		assertEquals(expected, ticketDao.getById(expected.getId()));
	}
	
	@Test(expected=NoResultException.class)
	@Transactional
    @Rollback(true)
	public final void shouldThrowExceptionWhenNoTicketExistsWithTheSpecifiedId() throws Exception {
		// Test data
		createAndPersistBug(null, 3, "Bug 1", "description for bug 1", null, 1, null, null);
		createAndPersistUserStory(null, 5, "User Story 1", "description for user story 1", createAttachments(1), 2, null);
		createAndPersistBug(null, 8, "Bug 2", "description for bug 2", null, 3, null, null);
		createAndPersistUserStory(null, 13, "User Story 2", "description for user story 2", createAttachments(1), 4, null);
		
		ticketDao.getById(0L);
	}
	
	@Test
	@Transactional
    @Rollback(true)
	public final void shouldReturnLargestOrderValue() throws Exception {
		// Test data
		createAndPersistBug(null, 3, "Bug 1", "description for bug 1", null, 1, null, null);
		createAndPersistUserStory(null, 5, "User Story 1", "description for user story 1", createAttachments(1), 2, null);
		
		assertEquals(Integer.valueOf(2), ticketDao.getLargestOrderValue());
	}
	
	@Test
	@Transactional
    @Rollback(true)
	public final void shouldReturnLargestOrderValueOfNullWhenNoTickets() throws Exception {
		assertNull(ticketDao.getLargestOrderValue());
	}
	
	@Test
	@Transactional
    @Rollback(true)
	public final void shouldReindexCorrectly() throws Exception {
		// Test data
		createAndPersistBug(null, 3, "Bug 1", "description for bug 1", createAttachments(1), 1, null, null);
		createAndPersistUserStory(null, 5, "User Story 1", "description for user story 1", null, 2, null);
		createAndPersistUserStory(null, 20, "User Story 2", "description for user story 2", null, 3, null);
		createAndPersistBug(null, 1, "Bug 2", "description for bug 2", null, 4, null, null);
		
		ticketDao.reindex();
		
		List<Ticket> actual = ticketDao.getAllTickets();
		assertEquals(Integer.valueOf(100), actual.get(0).getOrder());
		assertEquals(Integer.valueOf(200), actual.get(1).getOrder());
		assertEquals(Integer.valueOf(300), actual.get(2).getOrder());
		assertEquals(Integer.valueOf(400), actual.get(3).getOrder());
	}
	
	@Test
	@Transactional
    @Rollback(true)
	public final void shouldReturnTrueWhenAllSubtasksAreDone() throws Exception {
		// Test data
		List<Subtask> subtasks = new ArrayList<>();
		subtasks.add(new Subtask(null, "Subtask 1", "Description for subtask 1", createAttachments(1), 3, SubtaskState.DONE));
		subtasks.add(new Subtask(null, "Subtask 2", "Description for subtask 2", null, 1, SubtaskState.DONE));
		subtasks.add(new Subtask(null, "Subtask 3", "Description for subtask 3", createAttachments(3), 10, SubtaskState.DONE));
		Bug bug = createAndPersistBug(null, 3, "Bug 1", "description for bug 1", null, 1, subtasks, TicketLocation.CURRENT_SPRINT);
		
		assertTrue(ticketDao.allSubtasksDone(bug.getId()));
	}
	
	@Test
	@Transactional
    @Rollback(true)
	public final void shouldReturnFalseWhenNotAllSubtasksAreDone() throws Exception {
		// Test data
		List<Subtask> subtasks = new ArrayList<>();
		subtasks.add(new Subtask(null, "Subtask 1", "Description for subtask 1", createAttachments(1), 3, SubtaskState.DONE));
		subtasks.add(new Subtask(null, "Subtask 2", "Description for subtask 2", createAttachments(2), 1, SubtaskState.IN_PROGRESS));
		subtasks.add(new Subtask(null, "Subtask 3", "Description for subtask 3", null, 10, SubtaskState.TODO));
		Bug bug = createAndPersistBug(null, 3, "Bug 1", "description for bug 1", createAttachments(2), 1, subtasks, TicketLocation.CURRENT_SPRINT);
		
		assertFalse(ticketDao.allSubtasksDone(bug.getId()));
	}
	
	private List<Attachment> createAttachments(int number) {
		List<Attachment> attachments = new ArrayList<>(number);
		for (int i = 0; i < number; i++) {
			attachments.add(new Attachment("file_" + i + ".bmp", new byte[]{}));
		}
		return attachments;
	}

	private Bug createAndPersistBug(Long id, int storyPoints, String title, String description, List<Attachment> attachments, Integer order, List<Subtask> subtasks, TicketLocation location) {
		Bug bug = new Bug(null, storyPoints, title, description, attachments, subtasks, order);
		if (location != null) {
			bug.setLocation(location);
		}
		bug = (Bug) ticketDao.saveOrUpdate(bug);
		return bug;
	}
	
	private UserStory createAndPersistUserStory(Long id, int storyPoints, String title, String description, List<Attachment> attachments, Integer order, TicketLocation location) {
		UserStory userStory = new UserStory(null, storyPoints, title, description, attachments, null, null, order);
		if (location != null) {
			userStory.setLocation(location);
		}
		userStory = (UserStory) ticketDao.saveOrUpdate(userStory);
		return userStory;
	}
	
	private Subtask createAndPersistSubtask(Long id, String title, String description, List<Attachment> attachments, int estimate, SubtaskState state) {
		Subtask subtask = new Subtask(null, title, description, attachments, estimate, state);
		subtask = ticketDao.saveOrUpdate(subtask);
		return subtask;
	}
	
}
