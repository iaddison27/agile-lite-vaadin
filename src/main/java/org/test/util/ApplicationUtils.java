package org.test.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ApplicationUtils {

	@Value("#{application['hoursInDay']}")
    protected int hoursInDay;	

	public String getFormattedDaysAndHours(int hours) {
		String result = "";
		if (hours >= hoursInDay) {
			result = (hours / hoursInDay) + "d";
			int remainder = hours % hoursInDay;
			if (remainder > 0) {
				result += " " + remainder + "h";
			}
		} else {
			result = hours + "h";
		}
		return result;
	}
	
}
