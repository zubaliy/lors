package be.ordina.zubaliy.api;

import org.springframework.web.bind.annotation.RequestBody;

/**
 * API interface for LifeOnRoad
 *
 * @author zubaliy
 *
 */
interface LifeOnRoadInterface {

	/**
	 * Insert activity logs into mongo
	 *
	 * @param dataList is list with Map<String, Object>
	 *
	 * @return
	 */
	String insertActivityLogs(@RequestBody final String json);

	/**
	 * Get activity logs for N periods
	 *
	 * @param period days, weeks, months, years
	 * @param value number of days
	 *
	 * @return logs
	 */
	Object getActivityLogs(final String period, final Integer value);

	/**
	 * Get activity stats for N period
	 *
	 * @param period days, weeks, months, years
	 * @param value number of days
	 *
	 * @return stats
	 */
	Object getActivityStats(final String period, final Integer value);

}
