package com.loserico.commons.jackson.serializer;
import java.io.IOException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoField;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.SerializerProvider;

/**
 * Serializer for Java 8 temporal {@link LocalTime}s.
 *
 * @author Nick Williams
 * @since 2.2
 */
public class LocalTimeSerializer extends com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer {

	private static final long serialVersionUID = -1781796527684857415L;
	private boolean useTimestamp = false;
	private DateTimeFormatter formatter = null;
//	private DateTimeFormatter shortFormatter = ofPattern("HH:mm");
    public static final LocalTimeSerializer INSTANCE = new LocalTimeSerializer();

    protected LocalTimeSerializer() {
        this(null);
    }

    public LocalTimeSerializer(boolean useTimestamp, DateTimeFormatter formatter) {
        this.useTimestamp = useTimestamp;
        this.formatter = formatter;
    }
    
    public LocalTimeSerializer(DateTimeFormatter formatter) {
        this.formatter = formatter;
    }

    @Override
    public void serialize(LocalTime value, JsonGenerator g, SerializerProvider provider) throws IOException
    {
        if (useTimestamp) {
            g.writeStartArray();
            g.writeNumber(value.getHour());
            g.writeNumber(value.getMinute());
            if(value.getSecond() > 0 || value.getNano() > 0)
            {
                g.writeNumber(value.getSecond());
                if(value.getNano() > 0)
                {
                    if(provider.isEnabled(SerializationFeature.WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS))
                        g.writeNumber(value.getNano());
                    else
                        g.writeNumber(value.get(ChronoField.MILLI_OF_SECOND));
                }
            }
            g.writeEndArray();
        } else {
            DateTimeFormatter dtf = formatter;
            if (dtf == null) {
                dtf = _defaultFormatter();
            }
            g.writeString(value.format(dtf));
        }
    }

    // since 2.7: TODO in 2.8; change to use per-type defaulting
    protected DateTimeFormatter _defaultFormatter() {
        return DateTimeFormatter.ISO_LOCAL_TIME;
    }
}