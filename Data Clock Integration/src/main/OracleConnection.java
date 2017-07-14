package main;

import java.sql.*;

public class OracleConnection {
	public static Connection Connector() {
		try {
			Class.forName("oracle.jdbc.driver.OracleDriver");
			Connection conn = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:xe","SYSTEM","OPDB20120101");
			return conn;
		} catch (Exception e) {
			System.out.println(e);
			return null;
		}
	}
}
