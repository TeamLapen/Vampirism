package de.teamlapen.vampirism.common.util;


import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

public class LogUtil {

    public static final Marker TEST = new MarkerManager.Log4jMarker("testing");
    public static final Marker FACTION = new MarkerManager.Log4jMarker("FACTIONS");

    /**
     * Can be used to create temporary log messages during development. These can easy be found to be removed by checking the reference to this method.
     */
    public static void testLog(String msg, Object... args) {
        de.teamlapen.lib.util.LogUtil.testLog(msg, args);
    }
}
