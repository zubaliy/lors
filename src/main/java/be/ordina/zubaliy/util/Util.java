package be.ordina.zubaliy.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

public class Util {

	static public Date convertToDate(final LocalDate localdate) {
		return Date.from(localdate.atStartOfDay(ZoneId.systemDefault()).toInstant());
	}

	static public Date convertToDate(final LocalDateTime localdatetime) {
		return Date.from(localdatetime.atZone(ZoneId.systemDefault()).toInstant());
	}
}
