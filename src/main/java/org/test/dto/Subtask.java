package org.test.dto;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

@Entity
@Table(name="SUBTASK")
@PrimaryKeyJoinColumn(name="ABSTRACT_TASK_ID")
@NamedQueries({
    @NamedQuery(name = "Subtask.findByState", 
    			query = "SELECT s FROM Subtask s where s.state = :state order by s.id"),
	@NamedQuery(name = "Subtask.allSubtasksDone", 
				query = "SELECT count(s) = 0 FROM Subtask s where s.parent.id = :id and s.state != 'DONE'"),
})
public class Subtask extends AbstractTask implements Serializable {
	
	@Column(name = "ESTIMATE")
	private int estimate;
	
	@Column(name = "STATE")
	@Enumerated(EnumType.STRING)
	private SubtaskState state;
	
	@ManyToOne
	@JoinColumn(name = "ID")
	private Ticket parent;
	
	public Subtask() {
		super();
	}
	
	public Subtask(Long id, String title, String description, List<Attachment> attachments, int estimate, SubtaskState state) {
		super(id, title, description, attachments);
		this.estimate = estimate;
		this.state = state;
	}

	public int getEstimate() {
		return estimate;
	}

	public void setEstimate(int estimate) {
		this.estimate = estimate;
	}
	
	public SubtaskState getState() {
		return state;
	}

	public void setState(SubtaskState state) {
		this.state = state;
	}

	public Ticket getParent() {
		return parent;
	}

	public void setParent(Ticket parent) {
		this.parent = parent;
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
