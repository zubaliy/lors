package be.ordina.zubaliy.repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.data.mongodb.core.MongoTemplate;

import be.ordina.zubaliy.config.Config;
import be.ordina.zubaliy.entity.ActivityLog;
import be.ordina.zubaliy.util.Util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mongodb.DB;
import com.mongodb.MongoClient;

import de.flapdoodle.embed.mongo.distribution.Version;
import de.flapdoodle.embed.mongo.tests.MongodForTestsFactory;

@RunWith(MockitoJUnitRunner.class)
@Log4j
public class ActivityLogRepoTest {
	static final String DB_NAME = "test";
	static final String DB_COL_NAME = Config.MONGO_COLLECTION_NAME;

	MongodForTestsFactory factory;
	MongoClient mongo;
	DB db;

	ActivityLogRepo repo;
	List<ActivityLog> logs;

	int size = 31;

	MongoTemplate mongoTemplate;

	Gson gson;
	String json;

	@SneakyThrows
	@Before
	public void setup() {
		// set mongo
		factory = MongodForTestsFactory.with(Version.Main.PRODUCTION);
		mongo = factory.newMongo();
		db = mongo.getDB(DB_NAME);

		// set repo
		repo = new ActivityLogRepo();
		mongoTemplate = new MongoTemplate(mongo, DB_COL_NAME);
		repo.setMongoTemplate(mongoTemplate);

		// set collection
		logs = new ArrayList<>();

		for (int i = 1; i <= size; i++) {
			final Date connected = Util.convertToDate(LocalDateTime.of(2014, 1, i, 10, 0));
			final Date disconnected = Util.convertToDate(LocalDateTime.of(2014, 1, i, 11, 0));
			final int difference = 3600000;
			final ActivityLog log = new ActivityLog(null, connected, disconnected, difference);
			logs.add(log);
		}

		gson = new GsonBuilder().setDateFormat(Config.sdf.toPattern()).create();

		json = gson.toJson(logs).toString();

		repo.insertActivityLogJson(json);
	}

	@After
	public void teardown() {
		if (factory != null) {
			factory.shutdown();
		}
	}

	@Test
	public void testInsertActivityLogJson() {
		Assert.assertEquals(size, repo.getActivityLogs().size());
	}

	@Test
	public void testGetActivityLogs() {
		final Date date = Util.convertToDate(LocalDate.of(2014, 1, 31));
		final List<ActivityLog> logs = repo.findLogsByDate(date);
		//logs.stream().forEach(log::info);
		Assert.assertEquals(1, logs.size());
	}

}
