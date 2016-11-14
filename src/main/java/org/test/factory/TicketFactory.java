package org.test.factory;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.test.AcceptanceCriteria;
import org.test.CreateTicketBackingBean;
import org.test.FileUpload;
import org.test.SubtaskBackingBean;
import org.test.dto.Attachment;
import org.test.dto.Bug;
import org.test.dto.Subtask;
import org.test.dto.SubtaskState;
import org.test.dto.Ticket;
import org.test.dto.UserStory;
import org.test.service.ITicketTypeService;

/**
 * A SimpleFactory (https://www.binpress.com/tutorial/the-factory-design-pattern-explained-by-example/142) to create
 * different subclasses of Ticket
 * 
 * @author Ian Addison
 */
@Component
public class TicketFactory implements ITicketFactory {

	@Autowired
	private ITicketTypeService ticketTypeService;
	
	public Ticket createTicket(CreateTicketBackingBean ticket) {
		Ticket createdTicket = null;
		
		if (ticket != null && ticket.getTicketType() != null) {
			
			List<Subtask> subtasks = new ArrayList<>();
			if (ticket.getSubtasks() != null && ticket.getSubtasks().size() > 0) {
				for (SubtaskBackingBean subtaskForm : ticket.getSubtasks()) {
					List<Attachment> attachments = null;
					if (subtaskForm.getAttachments() != null && !subtaskForm.getAttachments().isEmpty()) {
						attachments = new ArrayList<>();
						for (FileUpload attachment : subtaskForm.getAttachments()) {
							attachments.add(new Attachment(attachment.getFilename(), attachment.getFile().toByteArray()));
						}
					}
					Subtask subtask = new Subtask(null, subtaskForm.getTitle(), subtaskForm.getDescription(), attachments, subtaskForm.getEstimate(), SubtaskState.TODO);
					
					// Deal with edit scenario
					subtask.setId(subtaskForm.getId());
					subtask.setVersion(subtaskForm.getVersion());
					
					subtasks.add(subtask);
					
				}
			}
			
			List<String> acceptanceCriteria = new ArrayList<>();
			// TODO: Define whether acceptanceCriteria is permitted on TicketType
			if ("User Story".equals(ticket.getTicketType().getType())) {
				if (ticket.getAcceptanceCriteria() != null && ticket.getAcceptanceCriteria().size() > 0) {
					for (AcceptanceCriteria acceptanceCriteriaForm : ticket.getAcceptanceCriteria()) {
						acceptanceCriteria.add(acceptanceCriteriaForm.getAcceptanceCriteria());
					}
				}
			}
			
			createdTicket = instantiateTicket(ticket, subtasks, acceptanceCriteria);
		}
		
		return createdTicket;
	}
	
	protected Ticket instantiateTicket(CreateTicketBackingBean ticket, List<Subtask> subtasks, List<String> acceptanceCriteria) {
		Ticket createdTicket = null;
		if (ticket.getTicketType() != null && ticket.getTicketType().getType() != null) {
			List<Attachment> attachments = null;
			if (ticket.getAttachments() != null && !ticket.getAttachments().isEmpty()) {
				attachments = new ArrayList<>();
				for (FileUpload attachment : ticket.getAttachments()) {
					attachments.add(new Attachment(attachment.getFilename(), attachment.getFile().toByteArray()));
				}
			}
			switch (ticket.getTicketType().getType()) {
				case "Bug":
					createdTicket = new Bug(null, ticket.getStoryPoints(), ticket.getTitle(), ticket.getDescription(), attachments, subtasks, ticket.getOrder());
					break;
				case "User Story":
					createdTicket = new UserStory(null, ticket.getStoryPoints(), ticket.getTitle(), ticket.getDescription(), attachments, acceptanceCriteria, subtasks, ticket.getOrder());
					break;
				default:
					break;
			}
		}
		// Deal with edit scenario 
		if (createdTicket != null) {
			createdTicket.setId(ticket.getId());
			createdTicket.setVersion(ticket.getVersion());
		}
		return createdTicket;
	}
	
	public CreateTicketBackingBean createTicketBackingBean(Ticket ticket) {
		CreateTicketBackingBean createdTicket = new CreateTicketBackingBean();
		
		if (ticket != null) {
			
			// TODO: Consider using Dozer
			if (ticket instanceof Bug) {
				createdTicket.setTicketType(ticketTypeService.getByType("Bug"));
			} else if (ticket instanceof UserStory) {
				createdTicket.setTicketType(ticketTypeService.getByType("User Story"));
			}
			
			createdTicket.setDescription(ticket.getDescription());
			createdTicket.setId(ticket.getId());
			createdTicket.setStoryPoints(ticket.getStoryPoints());
			createdTicket.setTitle(ticket.getTitle());
			createdTicket.setVersion(ticket.getVersion());
			
			List<SubtaskBackingBean> subtasks = new ArrayList<>();
			if (ticket.getSubtasks() != null && ticket.getSubtasks().size() > 0) {
				for (Subtask subtask : ticket.getSubtasks()) {
					SubtaskBackingBean subtaskForm = new SubtaskBackingBean();
					subtaskForm.setDescription(subtask.getDescription());
					subtaskForm.setEstimate(subtask.getEstimate());
					subtaskForm.setId(subtask.getId());
					subtaskForm.setTitle(subtask.getTitle());
					subtaskForm.setVersion(subtask.getVersion());
					subtasks.add(subtaskForm);
				}
			}
			createdTicket.setSubtasks(subtasks);
		}
		
		return createdTicket;
	}
}
