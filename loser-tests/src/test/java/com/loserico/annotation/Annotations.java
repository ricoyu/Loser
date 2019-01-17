package com.loserico.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Java 8 extends the context where annotation might be used. Now, it is
 * possible to annotate mostly everything: local variables, generic types,
 * super-classes and implementing interfaces, even the method’s exceptions
 * declaration.
 * 
 * @author Loser
 * @since Jul 10, 2016
 * @version
 *
 */
public class Annotations {

	@Retention(RetentionPolicy.RUNTIME)
	@Target({ ElementType.TYPE_USE, ElementType.TYPE_PARAMETER })
	public @interface NonEmpty {
	}

	public static class Holder<@NonEmpty T> extends @NonEmpty Object {
		public void method() throws @NonEmpty Exception {
		}
	}

	@SuppressWarnings("unused")
	public static void main(String[] args) {
		final Holder<String> holder = new @NonEmpty Holder<String>();
		@NonEmpty
		Collection<@NonEmpty String> strings = new ArrayList<>();
	}
}