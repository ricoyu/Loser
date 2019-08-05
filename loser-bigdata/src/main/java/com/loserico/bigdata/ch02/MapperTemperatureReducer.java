package com.loserico.bigdata.ch02;

import java.io.IOException;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

/**
 * 查找最高气温的Reduce类
 * <p>
 * Copyright: Copyright (c) 2019-08-03 09:17
 * <p>
 * Company: Sexy Uncle Inc.
 * <p>
 * @author Rico Yu  ricoyu520@gmail.com
 * @version 1.0
 * @on
 */
public class MapperTemperatureReducer extends Reducer<Text, IntWritable, Text, IntWritable> {

	@Override
	protected void reduce(Text key, Iterable<IntWritable> values, Context context)
			throws IOException, InterruptedException {
		int maxValue = Integer.MIN_VALUE;
		for (IntWritable value : values) {
			maxValue = Math.max(maxValue, value.get());
		}

		context.write(key, new IntWritable(maxValue));
	}

}
