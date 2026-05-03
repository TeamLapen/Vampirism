package de.teamlapen.faction.server;

import de.teamlapen.faction.common.config.FactionConfig;
import org.apache.logging.log4j.*;
import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.appender.FileAppender;
import org.apache.logging.log4j.core.config.AppenderRef;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.LoggerConfig;
import org.apache.logging.log4j.core.layout.PatternLayout;

/**
 * logger for faction stuff that is only initialized for dedicated server and only working if {@link de.teamlapen.faction.common.config.ServerConfig#enableFactionLogging} is true<br>
 * otherwise the logger does nothing
 */
public class FactionLogger {

    private static final String LOG_FILE_NAME = "logs/factions.log";
    private static final String LOG_NAME = "factions";
    public static final String LOGGER_NAME = "factions";

    public static final Marker LEVEL = MarkerManager.getMarker("LEVEL");
    public static final Marker FACTION = MarkerManager.getMarker("FACTION");
    public static final Marker SKILLS = MarkerManager.getMarker("SKILLS");
    public static final Marker LORD_LEVEL = MarkerManager.getMarker("LORD LEVEL");
    public static final Marker MINION_INVENTORY = MarkerManager.getMarker("MINION INVENTORY");

    private static Logger logger;

    /**
     * requires common config to be loaded
     */
    public static void init() {
        if (!FactionConfig.server().enableFactionLogging.get() || logger != null) return;
        final LoggerContext ctx = (LoggerContext) LogManager.getContext(false);
        Configuration configuration = ctx.getConfiguration();

        PatternLayout logPattern = PatternLayout.newBuilder().withPattern("[%d{DATE}] [%level] [%markerSimpleName] %msg%n").build();

        Appender fileAppender = FileAppender.newBuilder()
                .withFileName(LOG_FILE_NAME)
                .withAppend(true)
                .setName(LOG_NAME)
                .setConfiguration(configuration)
                .setLayout(logPattern)
                .build();

        fileAppender.start();

        AppenderRef[] refs = new AppenderRef[] {AppenderRef.createAppenderRef(LOG_NAME, null, null)};

        LoggerConfig loggerConfig = LoggerConfig.createLogger(false, Level.INFO, LOGGER_NAME, "true", refs, null, configuration, null);
        loggerConfig.addAppender(fileAppender, null, null);

        configuration.addLogger(LOGGER_NAME, loggerConfig);
        ctx.updateLoggers();
        logger = LogManager.getLogger(LOGGER_NAME);
    }

    public static void info(Marker marker, String msg, Object... args) {
        if (logger != null) {
            logger.info(marker, msg, args);
        }
    }
}
