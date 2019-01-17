package com.loserico.security;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.sql.Statement;
import java.util.logging.Logger;

import javax.sql.DataSource;

import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authc.credential.CredentialsMatcher;
import org.apache.shiro.authc.credential.DefaultPasswordService;
import org.apache.shiro.authc.credential.PasswordMatcher;
import org.apache.shiro.authc.credential.PasswordService;
import org.apache.shiro.crypto.hash.SimpleHash;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.realm.jdbc.JdbcRealm;
import org.apache.shiro.realm.jdbc.JdbcRealm.SaltStyle;
import org.junit.BeforeClass;
import org.junit.Test;

public class ShiroTest {

	private static Connection connection;
	private static DataSource dataSource;

	@BeforeClass
	public static void setup() throws ClassNotFoundException, SQLException {
		Class.forName("com.mysql.jdbc.Driver");
		connection = DriverManager.getConnection(
				"jdbc:mysql://118.178.252.68:3306/test?rewriteBatchedStatements=true&zeroDateTimeBehavior=convertToNull&useUnicode=true&characterEncoding=utf8",
				"test", "test$123");
		dataSource = new SimpleDataSource(connection);
		String clearSQL = "delete from users";
		String insertUserSQL = "insert into users(username, password, password_salt) values(?, ?, ?)";
		Statement clearStmt = connection.createStatement();
		clearStmt.execute(clearSQL);
		PreparedStatement stmt = connection.prepareStatement(insertUserSQL);
		stmt.setString(1, "rico");
		DefaultPasswordService passwordService = new DefaultPasswordService();
		stmt.setString(2, new SimpleHash("SHA-1", "123456", "asd").toString());
//		stmt.setString(2, passwordService.encryptPassword("123456"));
		stmt.setString(3, "asd");
		stmt.execute();
	}

	@Test
	public void testAuthc() {
		PasswordService passwordService = new DefaultPasswordService();
		CredentialsMatcher passwordMatcher = new PasswordMatcher();
		((PasswordMatcher)passwordMatcher).setPasswordService(passwordService);

		AuthorizingRealm realm = new JdbcRealm();
		realm.setCredentialsMatcher(passwordMatcher);
		((JdbcRealm)realm).setDataSource(dataSource);
//		((JdbcRealm)realm).setSaltStyle(SaltStyle.COLUMN);

		AuthenticationInfo authenticationInfo = realm
				.getAuthenticationInfo(new UsernamePasswordToken("rico", "123456"));
		System.out.println(authenticationInfo);
	}
	
	static class SimpleDataSource implements DataSource {
		
		private Connection connection;
		
		public SimpleDataSource(Connection connection) {
			this.connection = connection;
		}

		@Override
		public PrintWriter getLogWriter() throws SQLException {
			return null;
		}

		@Override
		public void setLogWriter(PrintWriter out) throws SQLException {
		}

		@Override
		public void setLoginTimeout(int seconds) throws SQLException {
		}

		@Override
		public int getLoginTimeout() throws SQLException {
			return 0;
		}

		@Override
		public Logger getParentLogger() throws SQLFeatureNotSupportedException {
			return null;
		}

		@Override
		public <T> T unwrap(Class<T> iface) throws SQLException {
			return null;
		}

		@Override
		public boolean isWrapperFor(Class<?> iface) throws SQLException {
			return false;
		}

		@Override
		public Connection getConnection() throws SQLException {
			return connection;
		}

		@Override
		public Connection getConnection(String username, String password) throws SQLException {
			return null;
		}
		
	}
}
