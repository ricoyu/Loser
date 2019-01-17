package com.loserico.orm.velocity;

import java.io.StringWriter;
import java.util.Properties;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.app.VelocityEngine;
import org.junit.Test;

public class VelocityTest {

	@Test
	public void testIfNull() {
		String queryString = "#ifNotNull($roleId) and id #end";
//		Velocity.init("velocity.properties");
		Properties properties = new Properties();
		properties.setProperty("userdirective", "com.loserico.orm.velocity.IfNotNull");
		properties.setProperty("userdirective", "com.loserico.orm.velocity.IfNotNull");
		Velocity.init(properties);
		//建立context， 并放入数据  
		VelocityContext context = new VelocityContext();
		context.put("roleId", "aaa");
		//解析后数据的输出目标，java.io.Writer的子类  
		StringWriter sql = new StringWriter();
		//进行解析  
		Velocity.evaluate(context, sql, "", queryString);
		queryString = sql.toString();
		System.out.println(queryString);
	}
}
