package org.test;

import java.util.ArrayList;
import java.util.List;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.test.dto.TicketType;

public class CreateTicketBackingBean {

	private Long id;
	
	private Long version;
	
	@NotNull(message="Type must be selected")
	private TicketType ticketType;
	
	//@NotEmpty(message="Title must be provided")
	private String title;
	
	//@NotEmpty(message="Description must be provided")
	private String description;
	
	@Min(value=0, message="Cannot be less than 0 Story Points")
    @Max(value=200, message="Cannot be more than 200 Story Points")
	private int storyPoints;
	
	private int order;
	
	private List<AcceptanceCriteria> acceptanceCriteria;
	
	private List<SubtaskBackingBean> subtasks;
	
	private List<FileUpload> attachments;
	
	public CreateTicketBackingBean() {
		super();
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getVersion() {
		return version;
	}

	public void setVersion(Long version) {
		this.version = version;
	}

	public TicketType getTicketType() {
		return ticketType;
	}

	public void setTicketType(TicketType ticketType) {
		this.ticketType = ticketType;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int getStoryPoints() {
		return storyPoints;
	}

	public void setStoryPoints(int storyPoints) {
		this.storyPoints = storyPoints;
	}
	
	public int getOrder() {
		return order;
	}

	public void setOrder(int order) {
		this.order = order;
	}

	public List<AcceptanceCriteria> getAcceptanceCriteria() {
		if (acceptanceCriteria == null) {
			acceptanceCriteria = new ArrayList<AcceptanceCriteria>();
		}
		return acceptanceCriteria;
	}

	public void setAcceptanceCriteria(List<AcceptanceCriteria> acceptanceCriteria) {
		this.acceptanceCriteria = acceptanceCriteria;
	}

	public List<SubtaskBackingBean> getSubtasks() {
		if (subtasks == null) {
			subtasks = new ArrayList<SubtaskBackingBean>();
		}
		return subtasks;
	}

	public void setSubtasks(List<SubtaskBackingBean> subtasks) {
		this.subtasks = subtasks;
	}

	public List<FileUpload> getAttachments() {
		return attachments;
	}

	public void setAttachment(List<FileUpload> attachments) {
		this.attachments = attachments;
	}

	@Override
	public int hashCode() {
		return HashCodeBuilder.reflectionHashCode(this);
	}

	@Override
	public boolean equals(Object obj) {
		return EqualsBuilder.reflectionEquals(this, obj);
	}
	
	@Override
	public String toString() {
		return ReflectionToStringBuilder.toString(this);
	}
	
}
