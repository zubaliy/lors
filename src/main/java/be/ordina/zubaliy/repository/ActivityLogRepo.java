package be.ordina.zubaliy.repository;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.TemporalAdjusters;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import lombok.extern.log4j.Log4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import be.ordina.zubaliy.config.Config;
import be.ordina.zubaliy.entity.ActivityLog;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.mongodb.AggregationOutput;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

/**
 * Repository for mongodb
 *
 * @author zubaliy
 *
 */
@Repository
@Log4j
public class ActivityLogRepo {

	@Autowired
	MongoTemplate mongoTemplate;

	public void insertActivityLogRow(final List<Map<String, Object>> dataList) {
		for (final Map<String, Object> data : dataList) {
			mongoTemplate.insert(data, Config.MONGO_COLLECTION_NAME);
		}
	}

	public void insertActivityLogJson(final String jsonDataList) {
		final Gson gson = new GsonBuilder().setDateFormat(Config.sdf.toPattern()).create();
		final JsonParser parser = new JsonParser();
		final JsonArray jArray = parser.parse(jsonDataList).getAsJsonArray();
		for (final JsonElement jsonElement : jArray) {
			final ActivityLog log = gson.fromJson(jsonElement, ActivityLog.class);
			mongoTemplate.insert(log);
		}
	}

	public List<DBObject> getActivityLogs() {
		return mongoTemplate.getCollection(Config.MONGO_COLLECTION_NAME).find().toArray();
	}

	public AggregationOutput getStatsForNDays(final Integer value) {
		log.info(String.format("Get stats for last %s days ", value));
		final Date date = convertToDate(createZeroToday().minusDays(value));

		return getStatsFrom(date);
	}

	private AggregationOutput getStatsFrom(final Date date) {
		final DBObject match = new BasicDBObject("$match", new BasicDBObject("connected", new BasicDBObject("$gte",
				date)));

		final DBObject groupFields = new BasicDBObject("_id", "total");
		groupFields.put("total", new BasicDBObject("$sum", "$difference"));
		final DBObject group = new BasicDBObject("$group", groupFields);

		final List<DBObject> pipeline = Arrays.asList(match, group);

		log.info(match);
		log.info(group);
		log.info(pipeline);
		return mongoTemplate.getCollection(Config.MONGO_COLLECTION_NAME).aggregate(pipeline);
	}

	/**
	 * Find logs from specific date
	 *
	 * @param date the
	 * @return
	 */
	private List<ActivityLog> findLogsByDate(final Date date) {
		final Query query = new Query();
		final Criteria criteria = Criteria.where("connected").gte(date);

		query.addCriteria(criteria);

		log.debug(query);

		final List<ActivityLog> result = mongoTemplate.find(query, ActivityLog.class);
		log.info("found: " + result.size());

		return result;
	}

	private Date convertToDate(final LocalDate localdate) {
		return Date.from(localdate.atStartOfDay(ZoneId.systemDefault()).toInstant());
	}

	private LocalDate createZeroToday() {
		return LocalDate.now();
	}

	private LocalDate createZeroThisWeek() {
		return LocalDate.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
	}

	private LocalDate createZeroThisMonth() {
		final LocalDate now = LocalDate.now();
		return now.minusDays(now.getDayOfMonth() + 1);
	}

	private LocalDate createZeroThisYear() {
		final LocalDate now = LocalDate.now();
		return now.minusDays(now.getDayOfYear() + 1);
	}

	public AggregationOutput getStats(final String period, final Integer value) {
		AggregationOutput result = null;
		switch (period) {
		case "days":
			result = getStatsFrom(convertToDate(createZeroToday().minusDays(value)));
			break;
		case "weeks":
			result = getStatsFrom(convertToDate(createZeroThisWeek().minusWeeks(value)));
			break;
		case "months":
			result = getStatsFrom(convertToDate(createZeroThisMonth().minusMonths(value)));
			break;
		case "years":
			result = getStatsFrom(convertToDate(createZeroThisYear().minusMonths(value * 12)));
			break;
		default:
			break;
		}

		return result;
	}

	public List<ActivityLog> getActivityLogs(final String period, final Integer value) {
		List<ActivityLog> result = null;
		switch (period) {
		case "days":
			result = findLogsByDate(convertToDate(createZeroToday().minusDays(value)));
			break;
		case "weeks":
			result = findLogsByDate(convertToDate(createZeroThisWeek().minusWeeks(value)));
			break;
		case "months":
			result = findLogsByDate(convertToDate(createZeroThisMonth().minusMonths(value)));
			break;
		case "years":
			result = findLogsByDate(convertToDate(createZeroThisYear().minusMonths(value * 12)));
			break;
		default:
			break;
		}

		return result;
	}
}
