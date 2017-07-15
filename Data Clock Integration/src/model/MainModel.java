package model;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import main.OracleConnection;

public class MainModel {
	Connection connection;
	String dateFromFile;
	Date date;
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
	
	public Date readDateFromFile() throws IOException {
		Date date = null;
		String inputFile = "date.txt";
		BufferedReader br = null;
		//String sDate1 = "31/12/1998";
		SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		
		try {
			String inputDate;
			br = new BufferedReader(new FileReader(inputFile));
			inputDate = br.readLine();
			if (inputDate != "") {
				date = formatter.parse(inputDate);
				System.out.println("date here!!!!!" + date);
			}
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
    		try {
    			if(br != null) br.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
    	}
		return date;
	}
	
	public void writeDateToFile(Date date) {
		String dateToString = "";
		BufferedWriter bw = null;
		SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		try {
			bw = new BufferedWriter(new FileWriter("date.txt"));
			formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
			dateToString = formatter.format(date);
			bw.write(dateToString);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
    		try {
    			if(bw != null) bw.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
    	}
	}
	
	@SuppressWarnings("null")
	public void retrieve() throws SQLException {
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		//String query = "SELECT id, entity_code FROM entprofile";
		try {
			//preparedStatement = connection.prepareStatement(query);
			Date dateFromFile = readDateFromFile();
			java.sql.Date sqlDate= new java.sql.Date(dateFromFile.getTime());
			System.out.println("HHHHHHHHHHHHHHH " + sqlDate);
			String query = "SELECT id, member, clock_date, clock_time, clock_index, clock_id FROM tksclock WHERE clock_date > ?";
			preparedStatement = connection.prepareStatement(query);
			preparedStatement.setDate(1, sqlDate);
			resultSet = preparedStatement.executeQuery();
			while(resultSet.next())
				System.out.println(resultSet.getLong(1)+"  "+resultSet.getLong(2)+"  "+ resultSet.getDate(3) + " " + resultSet.getTime(4) + " " + resultSet.getString(5) + " " + resultSet.getString(6));
				//System.out.println(resultSet.getLong(1) + " " + resultSet.getString(2));
		} catch (Exception e) {
			System.out.println("error");
		} finally {
			preparedStatement.close();
			resultSet.close();
		}
		
	}
}
