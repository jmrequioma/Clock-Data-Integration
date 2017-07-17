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
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimerTask;

import main.OracleConnection;

public class MainModel extends TimerTask {
	Connection connection;
	String dateFromFile;
	Date date;
	final String DATE_POLL = "lastProcessed";
	final String MIN_POLL = "pollIntervalMinutes";
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
	
	private Date readDateFromFile() throws IOException {
		Date date = null;
		//BufferedReader br = null;
		//String sDate1 = "31/12/1998";
		SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		
		try {
			String inputDate;
			//br = new BufferedReader(new FileReader(inputFile));
			inputDate = getConfigValue("lastProcessed");
			if (inputDate != "") {
				date = formatter.parse(inputDate);
				System.out.println("date here!!!!!" + date);
			}
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return date;
	}
	
	private void writeDateToFile(Date date) {
		String dateToString = "";
		BufferedWriter bw = null;
		SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		try {
			String secondLine = getConfigValue(MIN_POLL);
			bw = new BufferedWriter(new FileWriter("date.txt"));
			formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
			dateToString = formatter.format(date);
			bw.write("lastProcessed=" + dateToString);
			bw.append(MIN_POLL + secondLine);
			
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
	
	private void writeDateToFile(String date) {
		BufferedWriter bw = null;
		try {
			String secondLine = getConfigValue(MIN_POLL);
			bw = new BufferedWriter(new FileWriter("date.txt"));
			bw.write("lastProcessed=" + date + "\n");
			bw.append(MIN_POLL + "=" + secondLine);
			
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
	private String formatDate(Date someDate) {
		DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
		String formattedDate = df.format(someDate);
		return formattedDate;
	}
	
	@SuppressWarnings("null")
	private void retrieve() throws SQLException {
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		Date cutOffDate = null;
		Date cutOffTime = null;
		//String query = "SELECT id, entity_code FROM entprofile";
		try {
			//preparedStatement = connection.prepareStatement(query);
			Date dateFromFile = readDateFromFile();
			java.sql.Date sqlDate= new java.sql.Date(dateFromFile.getTime());
			java.sql.Time sqlTime = new java.sql.Time(dateFromFile.getTime());
			System.out.println("HHHHHHHHHHHHHHH " + sqlDate);
			System.out.println("HHHHHHHHHHHHHHH " + sqlTime);
			String query = "SELECT id, member, clock_date, clock_time, clock_index, clock_id FROM tksclock WHERE (clock_date > ? AND clock_time > ?) ORDER BY clock_date, clock_time";
			preparedStatement = connection.prepareStatement(query);
			preparedStatement.setDate(1, sqlDate);
			preparedStatement.setTime(2, sqlTime);
			resultSet = preparedStatement.executeQuery();
			while(resultSet.next()) {
				System.out.println(resultSet.getLong(1)+"  "+resultSet.getLong(2)+"  "+ resultSet.getDate(3) + " " + resultSet.getTime(4) + " " + resultSet.getString(5) + " " + resultSet.getString(6));
				cutOffDate = resultSet.getDate(3);
				cutOffTime = resultSet.getTime(4);
			}
			Date date = readDateFromFile();
			System.out.println(date);
			System.out.println("cutofffffffffff " + cutOffDate);
			System.out.println("cutofffffffffff " + cutOffTime);
			String formattedDate = formatDate(cutOffDate);
			System.out.println("formattedddddddd: " + formattedDate);
			writeDateToFile(formattedDate + " " + cutOffTime.toString());
			System.out.println("Threads: " + java.lang.Thread.activeCount());
		} catch (NullPointerException npe) {
				System.out.println("no data retrieved");
		} catch (Exception e) {
			System.out.println("error");
		} finally {
			preparedStatement.close();
			resultSet.close();
		}
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		try {
			retrieve();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public String getConfigValue(String keyName) throws IOException {
		String keyValue = "";
		String keyNameModified = keyName.toLowerCase();
		String line;
		BufferedReader in = new BufferedReader(new FileReader("date.txt"));
		try {
			while ((line = in.readLine().toLowerCase()) != null) {
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
