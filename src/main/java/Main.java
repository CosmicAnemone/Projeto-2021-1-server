import ch.qos.logback.classic.Logger;
import database.DatabaseManager;
import network.NetworkMain;
import org.slf4j.LoggerFactory;
import utils.EnvUtils;
import utils.ExceptionOps;
import utils.LogUtils;

public class Main {
	private static final Logger LOG = (Logger) LoggerFactory.getLogger(Main.class);
	
	public static void main(String[] args) throws Exception {
		LogUtils.init(LOG);
		
		LOG.info("Loading environment variables");
		if (!EnvUtils.init()) {
			LOG.error("Failed to load environment variables. Shutting down.");
			return;
		}
		LOG.info("Environment variables loaded");
		
		LOG.info("Starting server on port " + EnvUtils.PORT);
		NetworkMain.create();
		LOG.info("Server started");
		
		LOG.info("Setting up shutdown hook");
		Runtime.getRuntime().addShutdownHook(new Thread(Main::cleanShutdownAndAwait));
		LOG.info("Shutdown hook setup");
		
		LOG.info("DatabaseManager.init()");
		DatabaseManager.init();
		
		LOG.info("Init done");
	}
	
	private static void cleanShutdownAndAwait() {
		LOG.info("Starting shutdown procedure");
		
		LOG.info("Shutting down ratpack");
		try {
			NetworkMain.awaitTillClosed();
			LOG.info("Ratpack shutdown succeded");
		} catch (Exception e) {
			LOG.error("Ratpack shutdown went wrong. Exception:\n" +
					  ExceptionOps.print(e));
		}
		
		LOG.info("Closing database");
		DatabaseManager.close();
		LOG.info("Database closed");
		
		LOG.info("Shutdown procedure finished");
	}
	
}
