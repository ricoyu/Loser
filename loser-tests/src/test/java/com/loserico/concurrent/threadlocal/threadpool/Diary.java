package com.loserico.concurrent.threadlocal.threadpool;

public final class Diary {
	private static final ThreadLocal<Day> days = new ThreadLocal<Day>() {
		// Initialize to Monday
		protected Day initialValue() {
			return Day.MONDAY;
		}
	};

	private static Day currentDay() {
		return days.get();
	}

	public static void setDay(Day newDay) {
		days.set(newDay);
	}

	// Performs some thread-specific task
	public void threadSpecificTask() {
		System.out.println(currentDay());
	}
}