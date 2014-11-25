package be.ordina.zubaliy.api;

import java.util.List;

import org.springframework.web.bind.annotation.RequestBody;

import com.mongodb.DBObject;

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
	 * Get all activity logs from mongo
	 *
	 * @param dataList is list with Map<String, Object>
	 *
	 * @return
	 */
	List<DBObject> getActivityLogs();

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
