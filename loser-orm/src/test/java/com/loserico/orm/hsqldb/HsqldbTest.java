package com.loserico.orm.hsqldb;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.junit.Test;

public class HsqldbTest {

	@Test
	public void testHsqldbStart() throws SQLException {
		Connection c = DriverManager.getConnection("jdbc:hsqldb:mem", "SA", "");
	}
}
