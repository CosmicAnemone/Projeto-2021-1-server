package utils;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import database.DatabaseManager;
import database.EmpresaManager;
import network.RequestHandler;
import org.slf4j.LoggerFactory;

public class LogUtils {
	public static void init(Logger mainLog){
		Logger LOG = (Logger) LoggerFactory.getLogger(LogUtils.class);
		
		//Configuring loggers so I don't get endless logs
		LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
		loggerContext.getLogger("io.netty").setLevel(Level.WARN);
		loggerContext.getLogger("ratpack").setLevel(Level.WARN);
		loggerContext.getLogger("org.mongodb.driver").setLevel(Level.ERROR);
		//I do, however, want all logs I wrote myself
		loggerContext.getLogger(EnvUtils.class).setLevel(Level.TRACE);
		loggerContext.getLogger(DatabaseManager.class).setLevel(Level.TRACE);
		loggerContext.getLogger(EmpresaManager.class).setLevel(Level.TRACE);
		loggerContext.getLogger(RequestHandler.class).setLevel(Level.TRACE);
		mainLog.setLevel(Level.TRACE);
		LOG.setLevel(Level.INFO);
		LOG.info("Logs configured");
	}
}
