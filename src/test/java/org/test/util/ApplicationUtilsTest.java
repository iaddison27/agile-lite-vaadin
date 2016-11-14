package org.test.util;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;
import org.test.util.ApplicationUtils;

public class ApplicationUtilsTest {

	private ApplicationUtils applicationUtils = new ApplicationUtils();
	
	@Before
	public void before() {
		applicationUtils.hoursInDay = 8;
	}
	
	@Test
	public void threeHours() {
		assertEquals("3h", applicationUtils.getFormattedDaysAndHours(3));
	}
	
	@Test
	public void zeroHours() {
		assertEquals("0h", applicationUtils.getFormattedDaysAndHours(0));
	}
	
	@Test
	public void eightHours() {
		assertEquals("1d", applicationUtils.getFormattedDaysAndHours(8));
	}
	
	@Test
	public void sixteenHours() {
		assertEquals("2d", applicationUtils.getFormattedDaysAndHours(16));
	}
	
	@Test
	public void twentyHours() {
		assertEquals("2d 4h", applicationUtils.getFormattedDaysAndHours(20));
	}
}
