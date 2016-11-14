package org.test.dto;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

import com.vaadin.server.Resource;
import com.vaadin.server.ThemeResource;

@Entity
@Table(name="TICKET_TYPE")
@NamedQueries({
    @NamedQuery(name = "TicketType.findAll", 
    			query = "SELECT t FROM TicketType t order by t.type"),
    @NamedQuery(name = "TicketType.getByType", 
    			query = "SELECT t FROM TicketType t where t.type = :type"),
})
public class TicketType {

	@Id
    @Column(name="ID")
    @GeneratedValue
    private Integer id;
	
    @Column(name="TYPE")
	private String type;
	
    @Column(name="ICON")
	private String icon;

    public TicketType() {
		super();
	}
    
	public TicketType(String type, String icon) {
		super();
		this.type = type;
		this.icon = icon;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public Resource getIconResource() {
		return new ThemeResource("images/" + icon + ".png");
	}
	
	// hashCode and equals required so that we can populate the ComboBox from an existing Ticket
	
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
