package de.teamlapen.vampirism.misc;

import de.teamlapen.vampirism.config.VampirismConfig;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;
import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.appender.FileAppender;
import org.apache.logging.log4j.core.config.AppenderRef;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.LoggerConfig;
import org.apache.logging.log4j.core.layout.PatternLayout;

/**
 * logger for vampirism faction stuff that is only initialized for dedicated server and only woorking if {@link de.teamlapen.vampirism.config.VampirismConfig.Common#enableFactionLogging} is true<br>
 * otherwise the logger does nothing
 */
public class VampirismLogger {

    private static final String LOG_FILE_NAME = "logs/vampirism.log";
    private static final String LOG_NAME = "vampirism";
    public static final String LOGGER_NAME = "vampirism";

    public static final Marker LEVEL = MarkerManager.getMarker("LEVEL");
    public static final Marker SKILLS = MarkerManager.getMarker("SKILLS");
    public static final Marker LORD_LEVEL = MarkerManager.getMarker("LORD LEVEL");
    public static final Marker MINION_INVENTORY = MarkerManager.getMarker("MINION INVENTORY");

    private static Logger logger;

    /**
     * requires common config to be loaded
     */
    public static void init() {
        if (!VampirismConfig.COMMON.enableFactionLogging.get() || logger != null) return;
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

        AppenderRef[] refs = new AppenderRef[]{AppenderRef.createAppenderRef(LOG_NAME, null, null)};

        LoggerConfig loggerConfig = LoggerConfig.createLogger(false, Level.INFO, LOGGER_NAME, "true", refs, null, configuration, null);
        loggerConfig.addAppender(fileAppender, null, null);

        configuration.addLogger(LOGGER_NAME, loggerConfig);
        ctx.updateLoggers();
        logger  = LogManager.getLogger(LOGGER_NAME);
    }

    public static void info(Marker marker, String msg, Object... args) {
        if (logger != null) {
            logger.info(marker, msg, args);
        }
    }
}
