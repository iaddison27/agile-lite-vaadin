package org.test.dto;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;

@Entity
@Table(name="BUG")
@PrimaryKeyJoinColumn(name="ABSTRACT_TASK_ID")
public class Bug extends Ticket {

	public Bug() {
		super();
	}
	
	public Bug(Long id, int storyPoints, String title, String description, List<Attachment> attachments, List<Subtask> subtasks, Integer order) {
		super(id, storyPoints, title, description, attachments, subtasks, order);
	}

	@Override
	public String getIconName() {
		return "bug";
	}

}
