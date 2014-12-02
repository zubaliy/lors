package be.ordina.zubaliy.repository;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import lombok.Data;
import lombok.extern.log4j.Log4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import be.ordina.zubaliy.config.Config;
import be.ordina.zubaliy.entity.ActivityLog;
import be.ordina.zubaliy.util.Util;

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
@Data
public class ActivityLogRepo {

	@Autowired
	private MongoTemplate mongoTemplate;

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
		final Date date = Util.convertToDate(Util.createZeroToday().minusDays(value));

		return getStatsFrom(date);
	}

	/**
	 * Get total hours from specific date until now
	 *
	 * @param date the
	 * @return
	 */
	public AggregationOutput getStatsFrom(final Date date) {
		final DBObject match = new BasicDBObject("$match", new BasicDBObject("connected", new BasicDBObject("$gte",
				date)));

		final DBObject groupFields = new BasicDBObject("_id", "total");
		groupFields.put("total", new BasicDBObject("$sum", "$difference"));
		final DBObject group = new BasicDBObject("$group", groupFields);

		final List<DBObject> pipeline = Arrays.asList(match, group);

		log.debug(match);
		log.debug(group);
		log.debug(pipeline);
		return mongoTemplate.getCollection(Config.MONGO_COLLECTION_NAME).aggregate(pipeline);
	}

	/**
	 * Find logs from specific date until now
	 *
	 * @param date the
	 * @return
	 */
	public List<ActivityLog> findLogsByDate(final Date date) {
		final Query query = new Query();
		final Criteria criteria = Criteria.where("connected").gte(date);

		query.addCriteria(criteria);

		log.debug(query);

		final List<ActivityLog> result = mongoTemplate.find(query, ActivityLog.class);
		log.info("found: " + result.size());

		return result;
	}

	public AggregationOutput getStats(final String period, final Integer value) {
		AggregationOutput result = null;
		switch (period) {
		case "days":
			result = getStatsFrom(Util.convertToDate(Util.createZeroToday().minusDays(value)));
			break;
		case "weeks":
			result = getStatsFrom(Util.convertToDate(Util.createZeroThisWeek().minusWeeks(value)));
			break;
		case "months":
			result = getStatsFrom(Util.convertToDate(Util.createZeroThisMonth().minusMonths(value)));
			break;
		case "years":
			result = getStatsFrom(Util.convertToDate(Util.createZeroThisYear().minusMonths(value * 12)));
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
			result = findLogsByDate(Util.convertToDate(Util.createZeroToday().minusDays(value)));
			break;
		case "weeks":
			result = findLogsByDate(Util.convertToDate(Util.createZeroThisWeek().minusWeeks(value)));
			break;
		case "months":
			result = findLogsByDate(Util.convertToDate(Util.createZeroThisMonth().minusMonths(value)));
			break;
		case "years":
			result = findLogsByDate(Util.convertToDate(Util.createZeroThisYear().minusMonths(value * 12)));
			break;
		default:
			break;
		}

		return result;
	}
}
