package com.loserico.commons.jackson.serializer;

import java.io.IOException;
import java.math.BigDecimal;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.loserico.commons.utils.MathUtils;

/**
 * 将BigDecimal输出成货币形式：12,333.23
 * <p>
 * Copyright: Copyright (c) 2018-07-11 10:18
 * <p>
 * Company: DataSense
 * <p>
 * @author Rico Yu	ricoyu520@gmail.com
 * @version 1.0
 * @on
 */
public class MoneySerializer extends JsonSerializer<BigDecimal> {
	
    @Override
    public void serialize(BigDecimal value, JsonGenerator jgen, SerializerProvider provider) throws IOException,
            JsonProcessingException {
        jgen.writeString(MathUtils.format2Currency(value, 2));
    }
    
}