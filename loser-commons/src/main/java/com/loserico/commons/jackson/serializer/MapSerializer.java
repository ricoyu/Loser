package com.loserico.commons.jackson.serializer;

import java.util.HashSet;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;

public class MapSerializer extends com.fasterxml.jackson.databind.ser.std.MapSerializer {

	private static final long serialVersionUID = -1612513641809387300L;

	public MapSerializer(HashSet<String> ignoredEntries,
			JavaType keyType, JavaType valueType, boolean valueTypeIsStatic,
			TypeSerializer vts,
			JsonSerializer<?> keySerializer, JsonSerializer<?> valueSerializer) {
		super(ignoredEntries, keyType, valueType, valueTypeIsStatic, vts, keySerializer, valueSerializer);
	}

	public Map<?, ?> _orderEntries(Map<?, ?> input) {
		// minor optimization: may already be sorted?
		if (input instanceof SortedMap<?, ?>) {
			return input;
		}
		return new TreeMap<Object, Object>(new KeyComparator(input));
	}

}
