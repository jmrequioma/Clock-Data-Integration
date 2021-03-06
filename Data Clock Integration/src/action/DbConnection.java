package action;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.*;
import java.util.Base64;

public class DbConnection {
	final static String IN_FILE = "onepeopleWSServer.ini";
	final static String CONN_STR = "dbConnectString";
	final static String DB_DRIVER = "dbDriverClass";
	final static String DB_UNAME = "dbUsername";
	final static String DB_PWORD = "dbPassword";
	public static Connection Connector() {
		try {
			String connString = getConfigValue(CONN_STR);
			String driver = getConfigValue(DB_DRIVER);
			String uname = getConfigValue(DB_UNAME);
			String pword = getConfigValue(DB_PWORD);
			Class.forName(driver);
			Connection conn = DriverManager.getConnection(connString, uname, decrypt(pword));
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
	
	private static String decrypt(String encryptedData) throws Exception {
		byte[] barr = Base64.getDecoder().decode(encryptedData);
		String decoded = new String(barr);
		System.out.println("decoded: " + decoded);
		return decoded;
	}
}
