package jp.co.stream.clientsideresponse;

import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;


class DebugLog {
	
	private static String TAG = "DebugLog";
	private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss SSS");

	public enum LOG_LEVEL {

		verbose (1),
		debug (2),
		info (3),
		warning (4),
		error (5),
		none (6);

		private int value;

		private LOG_LEVEL(int value) {
			this.value = value;
		}

		public int getValue() {
			return value;
		}

	}

	
	private static LOG_LEVEL LEVEL = LOG_LEVEL.verbose;

	public static void v(String tag, String msg) {
		if (LEVEL.getValue() <= LOG_LEVEL.verbose.getValue()) {
			Log.v(tag, sdf.format(new Date()) + " " + msg);
		}
	}

	public static void d(String tag, String msg) {
		if (LEVEL.getValue() <= LOG_LEVEL.debug.getValue()) {
			Log.d(tag, sdf.format(new Date()) + " " + msg);
		}
	}

	public static void i(String tag, String msg) {
		if (LEVEL.getValue() <= LOG_LEVEL.info.getValue()) {
			Log.i(tag, sdf.format(new Date()) + " " + msg);
		}
	}

	public static void w(String tag, String msg) {
		if (LEVEL.getValue() <= LOG_LEVEL.warning.getValue()) {
			Log.w(tag, sdf.format(new Date()) + " " + msg);
		}
	}

	public static void e(String tag, String msg) {
		if (LEVEL.getValue() <= LOG_LEVEL.error.getValue()) {
			Log.e(tag, sdf.format(new Date()) + " " + msg);
		}
	}

	public static void e(String tag, String msg, Throwable tr) {
		if (LEVEL.getValue() <= LOG_LEVEL.error.getValue()) {
			Log.e(tag, sdf.format(new Date()) + " " + msg, tr );
		}
	}

	public static void setLoggingLevel(LOG_LEVEL logLevel) {
		LEVEL = logLevel;
	}
	
	public static LOG_LEVEL getLoggingLevel() {
		return LEVEL;
	}

}
