package de.teamlapen.vampirism.util;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;

/**
 * Logging class, which provides different methods for different log levels and always adds a tag which states to what the log is related
 * 
 * @author Max
 * 
 */
public class Logger {

	private static final org.apache.logging.log4j.Logger logger = LogManager.getLogger(REFERENCE.MODID);
	private static final Throwable stackInfo = new Throwable();
	/**
	 * Used to convert DEBUG levels to INFO level in dev, since it seems to be impossible to change the view level
	 */
	public static boolean inDev = false;

	public static void d(String tag, String format, Object... data) {
		if (inDev) {
			log(Level.INFO, tag, format, data);
		} else {
			log(Level.DEBUG, tag, format, data);
		}

	}

	public static void e(String tag, String format, Object... data) {
		log(Level.ERROR, tag, format, data);
	}

	public static void e(String tag, Throwable t, String format, Object... data) {
		log(Level.ERROR, tag, format, data);
		logger.catching(Level.ERROR, t);
	}

	private static String getLogLocation(Throwable t) {
		final StackTraceElement[] stack = t.getStackTrace();
		if (stack.length < 3)
			return "";
		final StackTraceElement caller = stack[2];
		String msg = caller.getClassName() + "." + caller.getMethodName() + "(" + caller.getFileName() + ":" + caller.getLineNumber() + ")";
		return msg.replace("de.teamlapen.vampirism", "vampirism");
	}

	public static void i(String tag, String format, Object... data) {
		log(Level.INFO, tag, format, data);
	}

	/**
	 * Should be used for test log calls so they can be easily found and removed
	 * @param format
	 * @param data
	 */
	public static void t(String format, Object... data) {
		log(Level.INFO, "Test", format, data);
	}

	private static void log(Level level, String tag, String format, Object... data) {
		if (tag == null) {
			tag = getLogLocation(stackInfo.fillInStackTrace());
		}
		logger.log(level, '[' + tag + ']' + String.format(format, data));
	}

	public static void w(String tag, String format, Object... data) {
		log(Level.WARN, tag, format, data);
	}
}
