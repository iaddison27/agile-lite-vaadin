package org.test.dto;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

@Entity
@Table(name="TICKET")
@PrimaryKeyJoinColumn(name="ABSTRACT_TASK_ID")
@NamedQueries({
    @NamedQuery(name = "Ticket.findAll", 
    			query = "SELECT t FROM Ticket t order by t.order"),
    @NamedQuery(name = "Ticket.findAllByLocation", 
				query = "SELECT t FROM Ticket t where t.location = :location order by t.order"),
    @NamedQuery(name = "Ticket.getById", 
    			query = "SELECT t FROM Ticket t where t.id = :id"),
    @NamedQuery(name = "Ticket.findLargestOrderValue", 
				query = "SELECT max(t.order) FROM Ticket t")
})
public abstract class Ticket extends AbstractTask implements Serializable {
	
	@Column(name="TITLE")
	private int storyPoints;
	
	@OneToMany(cascade = CascadeType.ALL, mappedBy = "parent", fetch = FetchType.LAZY)
	@OrderBy("id desc")
	@Fetch(FetchMode.SELECT)
	private List<Subtask> subtasks;
	
	@Column(name="LIST_ORDER")
	private Integer order;
	
	@Column(name = "LOCATION")
	@Enumerated(EnumType.STRING)
	private TicketLocation location;
	
	@Column(name = "DONE")
	private Boolean done;
	
	public Ticket() {
		super();
	}

	public Ticket(Long id, int storyPoints, String title, String description, List<Attachment> attachments, List<Subtask> subtasks, Integer order) {
		super(id, title, description, attachments);
		this.storyPoints = storyPoints;
		this.subtasks = subtasks;
		this.order = order;
		if (this.order == null) {
			this.order = 0;
		}
		
		if (this.subtasks != null) {
			for (Subtask subtask : subtasks) {
				subtask.setParent(this);
			}
		}
		this.location = TicketLocation.BACKLOG;
		this.done = Boolean.FALSE;
	}

	public int getStoryPoints() {
		return storyPoints;
	}

	public void setStoryPoints(int storyPoints) {
		this.storyPoints = storyPoints;
	}
	
	public List<Subtask> getSubtasks() {
		return subtasks;
	}

	public void setSubtasks(List<Subtask> subtasks) {
		this.subtasks = subtasks;
	}
	
	public int getSubtasksTotalEstimate() {
		int total = 0;
		if (subtasks != null) {
			for (Subtask subtask : subtasks) {
				total += subtask.getEstimate();
			}
		}
		return total;
	}

	public Integer getOrder() {
		return order;
	}

	public void setOrder(Integer order) {
		this.order = order;
	}

	public TicketLocation getLocation() {
		return location;
	}

	public void setLocation(TicketLocation location) {
		this.location = location;
	}

	public Boolean getDone() {
		return done;
	}

	public void setDone(Boolean done) {
		this.done = done;
	}

	public abstract String getIconName();

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
		return (new ReflectionToStringBuilder(this) {
	         protected boolean accept(Field f) {
	        	 // Ignore "acceptanceCriteria" as it results in a LazyLoadException 
	             return super.accept(f) && !f.getName().equals("acceptanceCriteria");
	         }
	     }).toString();
	}
	
}
