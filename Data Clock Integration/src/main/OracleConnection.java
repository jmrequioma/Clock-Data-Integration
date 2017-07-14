package main;

import java.sql.*;

public class OracleConnection {
	public static Connection Connector() {
		try {
			Class.forName("oracle.jdbc.driver.OracleDriver");
			Connection conn = DriverManager.getConnection("jdbc:oracle:thin:@0900003-PC:1521:oneppl","ONEADM","OPDB20120101");
			//Connection conn = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:xe","ONEADM","OPDBdefADMPWD");
			return conn;
		} catch (Exception e) {
			System.out.println(e);
			return null;
		}
	}
}
