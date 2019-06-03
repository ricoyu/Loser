package com.loserico.cache.utils;

import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Objects;

public class CollectionUtils {

	/**
	 * Creates a <i>mutable</i> {@code HashSet} instance initially containing the given elements.
	 *
	 * <p><b>Note:</b> if elements are non-null and won't be added or removed after this point, use
	 * {@link ImmutableSet#of()} or {@link ImmutableSet#copyOf(Object[])} instead. If {@code E} is an
	 * {@link Enum} type, use {@link EnumSet#of(Enum, Enum[])} instead. Otherwise, strongly consider
	 * using a {@code LinkedHashSet} instead, at the cost of increased memory footprint, to get
	 * deterministic iteration behavior.
	 *
	 * <p>This method is just a small convenience, either for {@code newHashSet(}{@link Arrays#asList
	 * asList}{@code (...))}, or for creating an empty set then calling {@link Collections#addAll}.
	 * This method is not actually very useful and will likely be deprecated in the future.
	 * @on
	 */
	public static <E> HashSet<E> newHashSet(E... elements) {
		Objects.requireNonNull(elements, "elements cannot be null");
		HashSet<E> set = new HashSet<>(elements.length);
		Collections.addAll(set, elements);
		return set;
	}

}
