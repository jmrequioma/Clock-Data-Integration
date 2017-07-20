package action;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.*;

public class OracleConnection {
	final static String IN_FILE = "onepeopleWSServer.ini";
	final static String CONN_STR = "dsConnectString";
	final static String UNAME = "username";
	final static String PWORD = "password";
	public static Connection Connector() {
		try {
			String connString = getConfigValue(CONN_STR);
			String uname = getConfigValue(UNAME);
			String pword = getConfigValue(PWORD);
			Class.forName("oracle.jdbc.driver.OracleDriver");
			Connection conn = DriverManager.getConnection("jdbc:oracle:thin:" + connString, uname, pword);
			//Connection conn = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:xe","ONEADM","OPDBdefADMPWD");
			return conn;
		} catch (Exception e) {
			System.out.println(e);
			return null;
		}
	}
	
	public static String getConfigValue(String keyName) throws IOException {
		String keyValue = "";
		String keyNameModified = keyName;
		String line;
		BufferedReader in = new BufferedReader(new FileReader(IN_FILE));
		try {
			while ((line = in.readLine()) != null) {
				int ind = line.indexOf(keyNameModified + "=");
				if (ind >= 0) {
					keyValue = line.substring((ind+keyNameModified+"=").length() - 1, line.length()).trim();
					return keyValue;
				}
			}
		} catch(NullPointerException npe) {
			//
		} finally {
			in.close();
		}
		return keyValue;
	}
}
