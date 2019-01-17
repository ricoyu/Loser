package com.loserico.concurrent.threadlocal.threadpool;

/**
 * Threading stories: ThreadLocal in web applications
 * https://www.javacodegeeks.com/2012/05/threading-stories-threadlocal-in-web.html
 * 
 * 
 * @author Rico Yu	ricoyu520@gmail.com
 * @since 2017-08-21 15:17
 * @version 1.0
 *
 */
public class ThreadLocalUtil {

	private final static ThreadLocal<ThreadVariables> THREAD_VARIABLES = new ThreadLocal<ThreadVariables>() {

		/**
		 * 
		 * @see java.lang.ThreadLocal#initialValue()
		 * 
		 */

		@Override

		protected ThreadVariables initialValue() {
			return new ThreadVariables();
		}

	};

	public static Object getThreadVariable(String name) {
		return THREAD_VARIABLES.get().get(name);
	}

	public static Object getThreadVariable(String name, InitialValue initialValue) {
		Object o = THREAD_VARIABLES.get().get(name);
		if (o == null) {
			THREAD_VARIABLES.get().put(name, initialValue.create());
			return getThreadVariable(name);
		} else {
			return o;
		}
	}

	public static void setThreadVariable(String name, Object value) {
		THREAD_VARIABLES.get().put(name, value);
	}

	public static void destroy() {
		THREAD_VARIABLES.remove();
	}

}
