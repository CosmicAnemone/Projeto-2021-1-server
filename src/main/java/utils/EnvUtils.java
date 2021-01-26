package utils;

import ch.qos.logback.classic.Logger;
import org.slf4j.LoggerFactory;

public class EnvUtils {
	private static final Logger LOG = (Logger) LoggerFactory.getLogger(EnvUtils.class);
	
	public static int PORT;
	public static String DB_connection_string, database_name;
	
	public static boolean init() {
		try {
			PORT = Integer.parseInt(System.getenv("PORT"));
		} catch (NumberFormatException e) {
			LOG.error("Failed to get environment variable PORT\n" + ExceptionOps.print(e));
			return false;
		}
		return validate(DB_connection_string = System.getenv("DB"), "DB") &&
			   validate(database_name = System.getenv("database_name"), "database_name");
	}
	
	private static boolean validate(String variable, String name) {
		if (variable != null) return true;
		LOG.error("Failed to get environment variable " + name);
		return false;
	}
}
