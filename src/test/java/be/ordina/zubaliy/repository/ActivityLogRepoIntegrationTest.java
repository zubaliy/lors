package be.ordina.zubaliy.repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import lombok.SneakyThrows;

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
import com.mongodb.AggregationOutput;
import com.mongodb.DB;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;

import de.flapdoodle.embed.mongo.Command;
import de.flapdoodle.embed.mongo.MongodExecutable;
import de.flapdoodle.embed.mongo.MongodStarter;
import de.flapdoodle.embed.mongo.config.ArtifactStoreBuilder;
import de.flapdoodle.embed.mongo.config.DownloadConfigBuilder;
import de.flapdoodle.embed.mongo.config.IMongodConfig;
import de.flapdoodle.embed.mongo.config.MongodConfigBuilder;
import de.flapdoodle.embed.mongo.config.Net;
import de.flapdoodle.embed.mongo.config.RuntimeConfigBuilder;
import de.flapdoodle.embed.mongo.distribution.Version;
import de.flapdoodle.embed.process.config.IRuntimeConfig;
import de.flapdoodle.embed.process.extract.UserTempNaming;
import de.flapdoodle.embed.process.runtime.Network;

@RunWith(MockitoJUnitRunner.class)
public class ActivityLogRepoIntegrationTest {
	static final String DB_NAME = "test";
	static final String DB_COL_NAME = Config.MONGO_COLLECTION_NAME;

	static MongodStarter runtime = null;
	MongodExecutable mongodExecutable = null;
	static final int MONGO_PORT = 12345;
	MongoClient mongo;
	DB db;

	ActivityLogRepo repo;
	MongoTemplate mongoTemplate;

	List<ActivityLog> logs;
	int size = 31;

	Gson gson;
	String json;

	@SneakyThrows
	@Before
	public void setup() {
		// set mongo
		if (runtime == null) {
	
		    Command command = Command.MongoD;
	
		    IRuntimeConfig runtimeConfig = new RuntimeConfigBuilder()
		        .defaults(command)
		        .artifactStore(new ArtifactStoreBuilder()
		            .defaults(command)
		            .download(new DownloadConfigBuilder()
		            .defaultsForCommand(command))
		            .executableNaming(new UserTempNaming()))
		        .build();
		
		    runtime = MongodStarter.getInstance(runtimeConfig);
		}
	    IMongodConfig mongodConfig = new MongodConfigBuilder()
        .version(Version.Main.PRODUCTION)
        .net(new Net(MONGO_PORT, Network.localhostIsIPv6()))
        .build();

	    mongodExecutable = runtime.prepare(mongodConfig);
        mongodExecutable.start();

        MongoClient mongo = new MongoClient("localhost", MONGO_PORT);

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
		if (mongodExecutable != null) {
			mongodExecutable.stop();
			mongodExecutable = null;
		}
	}

	@Test
	public void testInsertActivityLogJson() {
		Assert.assertEquals(size, repo.getMongoTemplate().getCollection(DB_COL_NAME).find().size());
	}

	@Test
	public void testGetActivityLogs() {
		final Date date = Util.convertToDate(LocalDate.of(2014, 1, 31));
		final List<ActivityLog> logs = repo.findLogsByDate(date);

		Assert.assertEquals(1, logs.size());
	}

	@Test
	public void testGetActivityStats(){
		final Date date = Util.convertToDate(LocalDate.of(2014, 1, 30));
		final AggregationOutput output = repo.getStatsFrom(date);
		final DBObject result = output.results().iterator().next();

		Assert.assertEquals(2*60*60*1000, result.get("total"));
	}

}
