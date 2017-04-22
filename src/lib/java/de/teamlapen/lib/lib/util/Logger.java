package de.teamlapen.lib.lib.util;


import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;

/**
 * Logging class, which provides different methods for different log levels and always adds a tag which states to what the log is related
 *
 * @author Max
 */
public class Logger {

    private final org.apache.logging.log4j.Logger logger;
    private final Throwable stackInfo = new Throwable();
    private final String packagename;
    private final String modid;
    /**
     * Used to convert DEBUG levels to INFO level in dev, since it seems to be impossible to change the view level
     */
    private boolean debug = false;
    /**
     * Currently Forge automatically adds the mod id on server side, but not on client side.
     */
    private boolean displayId = false;

    public Logger(String modid, String packagename) {
        logger = LogManager.getLogger(modid);
        this.packagename = packagename;
        this.modid = modid;
    }

    public void d(String tag, String format, Object... data) {
        if (debug) {
            log(Level.INFO, tag, format, data);
        } else {
            log(Level.DEBUG, tag, format, data);
        }

    }

    /**
     * Tells the logger to also print out the mod's ID, since Forge Client in dev environment does not do this automatically
     */
    public void displayModID() {
        this.displayId = true;
    }

    public void e(String tag, String format, Object... data) {
        log(Level.ERROR, tag, format, data);
    }

    public void e(String tag, Throwable t, String format, Object... data) {
        log(Level.ERROR, tag, format, data);
        logger.catching(Level.ERROR, t);
    }

    public void i(String tag, String format, Object... data) {
        log(Level.INFO, tag, format, data);
    }

    public boolean isDebug() {
        return debug;
    }

    /**
     * Makes debug messages appear on INFO level
     *
     * @param debug
     */
    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    /**
     * Should be used for test log calls so they can be easily found and removed
     *
     * @param format
     * @param data
     */
    public void t(String format, Object... data) {
        log(Level.INFO, "Test", format, data);
    }

    public void w(String tag, String format, Object... data) {
        log(Level.WARN, tag, format, data);
    }

    private String getLogLocation(Throwable t) {
        final StackTraceElement[] stack = t.getStackTrace();
        if (stack.length < 3)
            return "";
        final StackTraceElement caller = stack[2];
        String msg = caller.getClassName() + "." + caller.getMethodName() + "(" + caller.getFileName() + ":" + caller.getLineNumber() + ")";
        return msg.replace(packagename, "{" + modid + "}");
    }

    private void log(Level level, String tag, String format, Object... data) {
        if (tag == null) {
            tag = getLogLocation(stackInfo.fillInStackTrace());
        }
        logger.log(level, (displayId ? '[' + modid + ']' : "") + '[' + tag + ']' + String.format(format, data));
    }
}
