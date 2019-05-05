package de.teamlapen.lib.lib.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;


public class LogUtil {

    public static final Marker CONFIG = new MarkerManager.Log4jMarker("config");
    public static final Marker COMPAT = new MarkerManager.Log4jMarker("compat");


    private static final Logger LOGGER = LogManager.getLogger("TestLogger");

    /**
     * Can be used to create temporary log messages during development. These can easy be found to be removed by checking the reference to this method.
     */
    public static void testLog(String msg, Object... args) {
        LOGGER.warn(msg, args);
    }

    private LogUtil() {

    }
}
