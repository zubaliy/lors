package be.ordina.zubaliy.entity;

import java.util.Date;

import org.bson.types.ObjectId;

@lombok.Data
public class ActivityLog {

	private ObjectId _id;
	private Date connected;
	private Date disconnected;
	private Integer difference;
}
