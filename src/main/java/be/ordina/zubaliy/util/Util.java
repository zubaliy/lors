package be.ordina.zubaliy.util;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.TemporalAdjusters;
import java.util.Date;

/**
 * Util methods:
 * Convert from LocalDate, LocalDateTime => Date
 * Generate Date from today, this week, this month, this year
 *
 * @author zubaliy
 *
 */
public class Util {

	static public Date convertToDate(final LocalDate localdate) {
		return Date.from(localdate.atStartOfDay(ZoneId.systemDefault()).toInstant());
	}

	static public Date convertToDate(final LocalDateTime localdatetime) {
		return Date.from(localdatetime.atZone(ZoneId.systemDefault()).toInstant());
	}

	static public LocalDate createZeroToday() {
		return LocalDate.now();
	}

	static public LocalDate createZeroThisWeek() {
		return LocalDate.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
	}

	static public LocalDate createZeroThisMonth() {
		final LocalDate now = LocalDate.now();
		return now.minusDays(now.getDayOfMonth() + 1);
	}

	static public LocalDate createZeroThisYear() {
		final LocalDate now = LocalDate.now();
		return now.minusDays(now.getDayOfYear() + 1);
	}

	static public Date createZeroTodayDate() {
		return convertToDate(createZeroToday());
	}

	static public Date createZeroThisWeekDate() {
		return convertToDate(createZeroThisWeek());
	}

	static public Date createZeroThisMonthDate() {
		return convertToDate(createZeroThisMonth());
	}

	static public Date createZeroThisYearDate() {
		return convertToDate(createZeroThisYear());
	}
}
