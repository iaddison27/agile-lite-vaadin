package org.test.factory;

import org.test.CreateTicketBackingBean;
import org.test.dto.Ticket;

public interface ITicketFactory {

	public Ticket createTicket(CreateTicketBackingBean ticket);
	
	public CreateTicketBackingBean createTicketBackingBean(Ticket ticket);
}
