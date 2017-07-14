package model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import main.OracleConnection;

public class MainModel {
	Connection connection;
	public MainModel() {
		connection = OracleConnection.Connector();
		if (connection == null) {
			System.out.println("connection not successful");
			System.exit(1);
		} else {
			System.out.println("connection IS successful");
		}
	}
	
	public boolean isDbConnected() {
		try {
			return !connection.isClosed();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	}
	
	@SuppressWarnings("null")
	public void retrieve() throws SQLException {
		Statement preparedStatement = connection.createStatement();
		ResultSet resultSet = null;
		String query = "SELECT id, member, clock_date, clock_time, clock_index, clock_id FROM tksclock";
		//String query = "SELECT id, entity_code FROM entprofile";
		try {
			//preparedStatement = connection.prepareStatement(query);
			
			resultSet = preparedStatement.executeQuery(query);
			while(resultSet.next())  
				System.out.println(resultSet.getLong(1)+"  "+resultSet.getLong(2)+"  "+ resultSet.getDate(3) + " " + resultSet.getDate(4) + " " + resultSet.getString(5) + " " + resultSet.getString(6));
				//System.out.println(resultSet.getLong(1) + " " + resultSet.getString(2));
		} catch (Exception e) {
			System.out.println("error");
		} finally {
			preparedStatement.close();
			resultSet.close();
		}
		
	}
}
