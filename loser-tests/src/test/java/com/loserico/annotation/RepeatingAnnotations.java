package com.loserico.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

public class RepeatingAnnotations {

	@Target(ElementType.TYPE)
	@Retention(RetentionPolicy.RUNTIME)
	public @interface Filters {
		Filter[] value();
	}

	/**
	 * Annotation class Filter annotated with @Repeatable(Filters.class). The
	 * Filters is just a holder of Filter annotations but Java compiler tries
	 * hard to hide its presence from the developers. As such, the interface
	 * Filterable has Filter annotation defined twice (with no mentions of
	 * Filters).
	 * 
	 * @author Loser
	 * @since Jul 8, 2016
	 * @version
	 *
	 */
	@Target(ElementType.TYPE)
	@Retention(RetentionPolicy.RUNTIME)
	@Repeatable(Filters.class)
	public @interface Filter {
		String value();
	};

	@Filter("filter1")
	@Filter("filter2")
	public interface Filterable {
	}

	public static void main(String[] args) {
		for (Filter filter : Filterable.class.getAnnotationsByType(Filter.class)) {
			System.out.println(filter.value());
		}
	}
}