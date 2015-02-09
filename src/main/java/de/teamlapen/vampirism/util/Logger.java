package de.teamlapen.vampirism.util;

import java.io.FileNotFoundException;
import java.io.PrintStream;

import org.apache.logging.log4j.Level;

import cpw.mods.fml.common.FMLLog;

/**
 * Logging class, which provides different methods for different log levels and
 * always adds a tag which states to what the log is related
 * 
 * @author Max
 * 
 */
public class Logger {

	public static void d(String tag, String msg) {
		log(Level.DEBUG, "[" + tag + "]" + msg);
	}

	public static void e(String tag, String msg) {
		log(Level.ERROR, "[" + tag + "]" + msg);
	}

	public static void e(String tag, String msg, Throwable t) {
		String stacktrace = "";
		
		PrintStream p;
		try {
			p = new PrintStream(stacktrace);
			t.printStackTrace(p);
		} catch (FileNotFoundException e1) {
			stacktrace = t.getMessage();
		}
		log(Level.ERROR, "[" + tag + "]" + msg + "\nThrowable: "+t.getClass().getCanonicalName()+"\nStacktrace: " + stacktrace+"\nMessage: "+t.getMessage());
	}

	public static void i(String tag, String msg) {
		log(Level.INFO, "[" + tag + "]" + msg);
	}

	private static void log(Level level, String msg) {
		FMLLog.log(REFERENCE.MODID, level, msg);
	}

	public static void w(String tag, String msg) {
		log(Level.WARN, "[" + tag + "]" + msg);

	}
}
