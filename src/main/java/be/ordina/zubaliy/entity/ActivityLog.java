package be.ordina.zubaliy.entity;

import java.util.Date;

import lombok.AllArgsConstructor;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;

@lombok.Data
@Document
@AllArgsConstructor
public class ActivityLog {

	private ObjectId _id;
	private Date connected;
	private Date disconnected;
	private Integer difference;
}
