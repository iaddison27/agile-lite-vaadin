package org.test.dto;

import java.util.List;

import javax.persistence.CollectionTable;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;

@Entity
@Table(name="USER_STORY")
@PrimaryKeyJoinColumn(name="ABSTRACT_TASK_ID")
public class UserStory extends Ticket {

	@ElementCollection
	@CollectionTable(name="USER_STORY_ACCEPTANCE_CRITERIA")
	private List<String> acceptanceCriteria;

	public UserStory() {
		super();
	}
	
	public UserStory(Long id, int storyPoints, String title, String description, List<Attachment> attachments, List<String> acceptanceCriteria, List<Subtask> subtasks,  Integer order) {
		super(id, storyPoints, title, description, attachments, subtasks, order);
		this.acceptanceCriteria = acceptanceCriteria;
	}
	
	public List<String> getAcceptanceCriteria() {
		return acceptanceCriteria;
	}

	public void setAcceptanceCriteria(List<String> acceptanceCriteria) {
		this.acceptanceCriteria = acceptanceCriteria;
	}	
	
	@Override
	public String getIconName() {
		return "story";
	}
}
