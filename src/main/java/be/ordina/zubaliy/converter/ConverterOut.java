package be.ordina.zubaliy.converter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.time.DurationFormatUtils;

import be.ordina.zubaliy.entity.ActivityLog;

import com.mongodb.DBObject;


public class ConverterOut {

	public static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	public static List<be.ordina.zubaliy.converter.out.ActivityLog> convert(final List<ActivityLog> logs) {
		final List<be.ordina.zubaliy.converter.out.ActivityLog> list = new ArrayList<>();

		for (final ActivityLog log : logs) {
			list.add(convert(log));
		}

		return list;
	}


	public static be.ordina.zubaliy.converter.out.ActivityLog convert(final ActivityLog log) {
		final be.ordina.zubaliy.converter.out.ActivityLog converted = new be.ordina.zubaliy.converter.out.ActivityLog();

		converted.setVroom(sdf.format(log.getConnected()));
		converted.setBrake(sdf.format(log.getDisconnected()));
		converted.setVoyage(DurationFormatUtils.formatDurationWords(log.getDifference(), true, true));

		return converted;
	}

	public static void adjustAggregationResult(final DBObject result, final String period, final String value){
		final int total = (int) result.get("total");
		result.removeField("_id");
		result.put("total", DurationFormatUtils.formatDurationWords(total, true, true));
		result.put("for", String.format("For the period of last %s %s", value, period));
	}
}
