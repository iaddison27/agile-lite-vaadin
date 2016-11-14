package org.test.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;
import org.test.dto.TicketType;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:repositoryTest-context.xml" })
public class TicketTypeDaoTest {

	@Autowired
	protected TicketTypeDao ticketTypeDao;
	
	@Test
	@Transactional
    @Rollback(true)
	public final void shouldReturnAllTicketTypes() throws Exception {
		// Test data
		List<TicketType> expected = new ArrayList<>();
		
		expected.add(createAndPersistTicketType("TYPE_A", "ICON_1"));
		expected.add(createAndPersistTicketType("TYPE_B", "ICON_2"));
		expected.add(createAndPersistTicketType("TYPE_C", "ICON_3"));
		
		assertEquals(expected, ticketTypeDao.getTicketTypes());
	}

	private TicketType createAndPersistTicketType(String type, String icon) {
		TicketType t = new TicketType(type, icon);
		t = ticketTypeDao.saveOrUpdate(t);
		return t;
	}
	
	@Test
	@Transactional
    @Rollback(true)
	public final void shouldReturnTicketTypeMatchingTheSpecifiedType() throws Exception {
		// Test data
		createAndPersistTicketType("TYPE_A", "ICON_1");
		TicketType expected = createAndPersistTicketType("TYPE_B", "ICON_2");
		createAndPersistTicketType("TYPE_C", "ICON_3");
		
		assertEquals(expected, ticketTypeDao.getByType("TYPE_B"));
	}
	
	@Test(expected=NoResultException.class)
	@Transactional
    @Rollback(true)
	public final void shouldThrowExceptionWhenNoTicketTypeMatchingTheSpecifiedType() throws Exception {
		// Test data
		createAndPersistTicketType("TYPE_A", "ICON_1");
		createAndPersistTicketType("TYPE_B", "ICON_2");
		createAndPersistTicketType("TYPE_C", "ICON_3");
		
		ticketTypeDao.getByType("TYPE_D");
	}
	
	@Test(expected=NonUniqueResultException.class)
	@Transactional
    @Rollback(true)
	public final void shouldThrowExceptionWhenMultipleTicketTypesMatchingTheSpecifiedType() throws Exception {
		// Test data
		createAndPersistTicketType("TYPE_A", "ICON_1");
		createAndPersistTicketType("TYPE_A", "ICON_2");
		createAndPersistTicketType("TYPE_C", "ICON_3");
		
		ticketTypeDao.getByType("TYPE_A");
	}
	
}
