package be.ordina.zubaliy.api;

import java.util.List;

import javax.annotation.PostConstruct;

import lombok.extern.log4j.Log4j;

import org.dozer.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import be.ordina.zubaliy.converter.ConverterOut;
import be.ordina.zubaliy.repository.ActivityLogRepo;

import com.mongodb.DBObject;

@Controller
@RequestMapping(value = "/lifeonroad/api")
@Log4j
@ResponseBody
public class LifeOnRoadImpl implements LifeOnRoadInterface {

	@Autowired
	ActivityLogRepo activityLogRepo;

	@Autowired
	MongoTemplate mongoTemplate;

	@Autowired
	Mapper mapper;



	@PostConstruct
	public void init() {}

	@Override
	@RequestMapping(value = "/insert/ActivityLogs", method = RequestMethod.POST)
	public String insertActivityLogs(@RequestBody final String json) {
		log.debug(json);
		activityLogRepo.insertActivityLogJson(json);
		return json;
	}

	@Override
	@RequestMapping(value = "/get/activitylogs/{period}/{value}")
	public List<be.ordina.zubaliy.converter.out.ActivityLog> getActivityLogs(@PathVariable final String period,
			@PathVariable final Integer value) {
		log.info("Get logs for " + value + " " + period);
		return ConverterOut.convert(activityLogRepo.getActivityLogs(period, value));
	}

	@Override
	@RequestMapping(value = "/get/activitystats/{period}/{value}")
	public Object getActivityStats(@PathVariable final String period, @PathVariable final Integer value) {
		log.info("Get logs for " + value + " " + period);
		final Iterable<DBObject> iterator = activityLogRepo.getStats(period, value).results();
		// take the first element only
		final DBObject result = iterator.iterator().next();
		ConverterOut.adjustAggregationResult(result, period, value.toString());

		return result;
	}

}
