package be.ordina.zubaliy.config;

import java.text.SimpleDateFormat;
/**
 * Place here app configuratino properties
 *
 * @author zubaliy
 *
 */
public class Config {
	/**
	 * If tested on openshift use OPENSHIFT_DATA_DIR. If local => target folder.
	 */
	public static final String UPLOAD_FOLDER = System.getenv("OPENSHIFT_DATA_DIR") == null ?
			"target/testUpload" : System.getenv("OPENSHIFT_DATA_DIR") + "upload/";

	public static final String MONGO_COLLECTION_NAME = "activityLog";

	public static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

}
