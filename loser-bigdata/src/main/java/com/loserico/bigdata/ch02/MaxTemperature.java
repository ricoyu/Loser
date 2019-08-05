package com.loserico.bigdata.ch02;

import java.io.IOException;

import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

public class MaxTemperature {

	public static void main(String[] args) throws IOException {
		if (args.length != 2) {
			System.err.println("Usage: MaxTemperature <input path> <output path>");
			System.exit(-1);
		}

		System.out.println("---------------------");
		Job job = new Job();
		job.setJarByClass(MaxTemperature.class);
		job.setJobName("Max Temperature");

		FileInputFormat.addInputPath(job, new Path(args[0]));
		FileOutputFormat.setOutputPath(job, new Path(args[1]));

		job.setMapperClass(MaxTemperatureMapper.class);
		job.setReducerClass(MapperTemperatureReducer.class);

		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(IntWritable.class);
	}
}
