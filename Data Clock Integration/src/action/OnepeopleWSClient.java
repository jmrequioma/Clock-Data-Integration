package action;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.StringReader;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLConnection;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimerTask;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.net.ssl.HttpsURLConnection;
import javax.swing.text.Document;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.util.Base64;

public class OnepeopleWSClient extends TimerTask {
	Connection connection;
	String dateFromFile;
	Date date;
	final String IN_FILE = "onepeopleWSServer.ini";
	final String CONN_STR = "dbConnectString";
	final String DB_DRIVER = "dbDriverClass";
	final String DB_UNAME = "dbUsername";
	final String DB_PWORD = "dbPassword";
	final String HOST = "wsHost";
	final String ACC_CODE = "wsAccountCode";
	final String ACC_PASS = "wsAccountPass";
	final String DATE_POLL = "lastProcessed";
	final String MIN_POLL = "pollIntervalMinutes";
	final String DUM_DATEVAL = "0000/00/00 00:00:00";
	
	String wsPass = "";
	String dbPass = "";
	private final String CLOCK_DEFAULT = "RFID";
	private final String HTTPS_PROTOCOL = "https";
    private final String HTTP_PROTOCOL = "http";
    
    //private String accountCode = "110001-1P"; // DEMO/TEST: XX0002-1R
    //private String accountPass = "OPWS13072009"; // DEMO/TEST: OPWSXX0002
    private boolean enableCertValidate = false;
    private String http_port = "80";
    private String https_port = "443";

	private static String HOST_NAME = "0900003-PC";
	private String clockId = "01";
	private String clockIndex = "DUAL";
	private String wsURLClock = HTTPS_PROTOCOL.concat("://").concat(HOST_NAME).concat(":").concat(https_port).concat("/onepeople/services/EclockRemote?wsdl");
	private String wsURLRoot = HTTPS_PROTOCOL.concat("://").concat(HOST_NAME).concat(":").concat(https_port).concat("/onepeople/services/");
	
	public OnepeopleWSClient() {
		connection = DbConnection.Connector();
		if (connection == null) {
			System.out.println("connection not successful");
			System.exit(1);
		} else {
			System.out.println("connection IS successful");
		}
		try {
			this.HOST_NAME = getConfigValue(HOST);
			wsPass = getConfigValue(ACC_PASS);
			dbPass = getConfigValue(DB_PWORD);
			http_port = "80";
			https_port = "443";
			wsURLClock = HTTPS_PROTOCOL.concat("://").concat(HOST_NAME).concat(":").concat(https_port).concat("/onepeople/services/EclockRemote?wsdl");
			wsURLRoot = HTTPS_PROTOCOL.concat("://").concat(HOST_NAME).concat(":").concat(https_port).concat("/onepeople/services/");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
	
	private Date readDataFromFile() throws IOException {
		Date date = null;
		
		
		try {
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
			String inputDate;
			//br = new BufferedReader(new FileReader(inputFile));
			inputDate = getConfigValue("lastProcessed");
			if (!(inputDate.equals(""))) {
				date = formatter.parse(inputDate);
				System.out.println("date here!!!!!" + date);
				System.out.println("password: " + getConfigValue(ACC_PASS));
				wsPass = getConfigValue(ACC_PASS);
				System.out.println(wsPass);
			} else {
				date = formatter.parse(DUM_DATEVAL);   // dummy date value
			}
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			date = null;
		}
		return date;
	}
	
	private void writeDataToFile(String date) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException {
		BufferedWriter bw = null;
		try {
			String connLine = getConfigValue(CONN_STR);
			String driverLine = getConfigValue(DB_DRIVER);
			String unameLine = getConfigValue(DB_UNAME);
			String pwordLine = getConfigValue(DB_PWORD);
			String hostLine = getConfigValue(HOST);
			String accCodeLine = getConfigValue(ACC_CODE);
			String accPassLine = getConfigValue(ACC_PASS);
			System.out.println("encrypted test: " + accPassLine);
			String minLine = getConfigValue(MIN_POLL);
			bw = new BufferedWriter(new FileWriter(IN_FILE));
			bw.write(CONN_STR + "=" + connLine + "\n");
			bw.append(DB_DRIVER + "=" + driverLine + "\n");
			bw.append(DB_UNAME + "=" + unameLine + "\n");
			bw.append(DB_PWORD + "=" + pwordLine + "\n");
			bw.append(HOST + "=" + hostLine + "\n");
			bw.append(ACC_CODE + "=" + accCodeLine + "\n");
			bw.append(ACC_PASS + "=" + accPassLine + "\n");
			bw.append(DATE_POLL + "=" + date + "\n");
			bw.append(MIN_POLL + "=" + minLine);
			
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
		DateFormat df = new SimpleDateFormat("yyyy/MM/dd");
		String formattedDate = df.format(someDate);
		return formattedDate;
	}
	
	@SuppressWarnings("null")
	private void retrieve() throws Exception {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		Date cutOffDate = null;
		Date cutOffTime = null;
		Date clockDateTime = null;
		String employeeId = "";
		String returnValue = "";
		try {
			Date dateFromFile = readDataFromFile();
			if (dateFromFile != (formatter.parse(DUM_DATEVAL))) {
				java.sql.Date sqlDate= new java.sql.Date(dateFromFile.getTime());
				java.sql.Time sqlTime = new java.sql.Time(dateFromFile.getTime());
				System.out.println("sqlDate: " + sqlDate);
				System.out.println("sqlTime: " + sqlTime);
				String query = "SELECT clk.id, clk.member, clk.clock_date, clk.clock_time, clk.clock_index, clk.clock_id FROM xvw_tksclock clk WHERE (clk.clock_date > ? AND clk.clock_time > ?) ORDER BY clk.clock_time";
				preparedStatement = connection.prepareStatement(query);
				preparedStatement.setDate(1, sqlDate);
				preparedStatement.setTime(2, sqlTime);
				resultSet = preparedStatement.executeQuery();
				while(resultSet.next()) {
					System.out.println(resultSet.getLong(1)+"  "+resultSet.getString(2)+"  "+ resultSet.getDate(3) + " " + resultSet.getTime(4) + " " + resultSet.getString(5) + " " + resultSet.getString(6));
					cutOffDate = resultSet.getDate(4);
					cutOffTime = resultSet.getTime(4);
					employeeId = resultSet.getString(2);
					clockDateTime = dateTime(cutOffDate, cutOffTime);
					returnValue = clockEntryData(employeeId, clockDateTime);
					if (returnValue.length() > 0) {
						System.out.println(returnValue);
					}
				}
				Date date = readDataFromFile();
				System.out.println(date);
				System.out.println("last Date Processed " + cutOffDate);
				System.out.println("last Time Processed " + cutOffTime);
				String formattedDate = formatDate(cutOffDate);
				System.out.println("formattedddddddd: " + formattedDate);
				if (formattedDate != null || cutOffTime != null || dateFromFile != (formatter.parse(DUM_DATEVAL))) {
					writeDataToFile(formattedDate + " " + cutOffTime.toString());
				}
				System.out.println("Threads: " + java.lang.Thread.activeCount());
			}
		} catch (NullPointerException npe) {
				System.out.println("no data retrieved");
		} catch (Exception e) {
			e.printStackTrace();
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
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public String getConfigValue(String keyName) throws IOException {
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
	
	private String clockEntryData(String memberId, Date dateTime) throws MalformedURLException, IOException {    	
    	if(memberId == null || memberId == "") {
    		return null;
    	}
    		
    	if(!enableCertValidate) {
    		OnepeopleWSClient.disableCertificateValidation();
    	}
    	
	    String responseString = "";
	    String outputString = "";
	    String clockId;
	    String returnValue = null;
	    String accountPass = getConfigValue(ACC_PASS);
	    
	    try {
	    	if(this.clockId == null) {
	    		clockId = this.CLOCK_DEFAULT;
	    	} else {
	    		clockId = this.CLOCK_DEFAULT + "-" + this.clockId;
	    	}
	    	
	        URL url = new URL(wsURLClock);
	        URLConnection connection = url.openConnection();
	        ByteArrayOutputStream bout = new ByteArrayOutputStream();
	        String xmlInput =
	            "  <soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:web=\"" + wsURLRoot + "\">\n" +
	            "   <soapenv:Header/>\n" +
	            "   <soapenv:Body>\n" +
	            "      <web:clockEntryData>\n" +
	            "         <!--Optional:-->\n" +
	            "         <web:acctCode>" + getConfigValue(ACC_CODE) + "</web:acctCode>\n" +
	            "         <web:keyPass>" + decrypt(accountPass) + "</web:keyPass>\n" +
	            "         <web:memberId>" + memberId + "</web:memberId>\n" +
	            "		  <web:clockIndex>" + clockIndex + "</web:clockIndex>\n" +
	            "         <web:clockId>" + clockId + "</web:clockId>\n" +
	            "          <web:clockDateTime>" + dateTime + "</web:clockDateTime>\n" +
	            "      </web:clockEntryData>\n" +
	            "   </soapenv:Body>\n" +
	            "  </soapenv:Envelope>";
	
	        byte[] buffer = new byte[xmlInput.length()];
	        buffer = xmlInput.getBytes();
	        bout.write(buffer);
	        byte[] b = bout.toByteArray();
	        String SOAPAction = wsURLRoot + "clockIn";
	        // Set the appropriate HTTP parameters.
	        HttpsURLConnection httpConn = (HttpsURLConnection) prepareConnection(connection, b.length, SOAPAction);
	        OutputStream out = httpConn.getOutputStream();
	        //Write the content of the request to the outputstream of the HTTP Connection.
	        out.write(b);
	        out.close();
	        //Ready with sending the request.
	
	        //Read the response.
	        InputStreamReader isr =
	            new InputStreamReader(httpConn.getInputStream());
	        BufferedReader in = new BufferedReader(isr);
	    
	        //Write the SOAP message response to a String.
	        while ((responseString = in.readLine()) != null) {
	            outputString = outputString + responseString;
	        }
	        
		    //Parse the String output to a org.w3c.dom.Document and be able to reach every node with the org.w3c.dom API.
		    Document document = parseXmlFile(outputString);
		    NodeList nodeLst = ((org.w3c.dom.Document) document).getElementsByTagName("clockInResponse");
		    returnValue = nodeLst.item(0).getTextContent();
		    System.out.println("returnValue: " + returnValue);		    
	    } catch (SocketTimeoutException e) {
	        e.printStackTrace();
	    } catch (ProtocolException e) {
	        e.printStackTrace();
	    } catch (ConnectException e) {
	        e.printStackTrace();
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	    
	    return returnValue;
	}
	
	private static void disableCertificateValidation() {
    	javax.net.ssl.HttpsURLConnection.setDefaultHostnameVerifier(
			new javax.net.ssl.HostnameVerifier(){

			    public boolean verify(String hostname,
			            javax.net.ssl.SSLSession sslSession) {
			        return hostname.equals(HOST_NAME);
			    }
			});    		
	}
	
	private HttpURLConnection prepareConnection(URLConnection connection, int contentLength, String soapAction) 
			throws ProtocolException {
			
	        HttpURLConnection httpConn = (HttpURLConnection) connection;
	        httpConn.setRequestProperty("Content-Length",
	                                    String.valueOf(contentLength));
	        httpConn.setRequestProperty("Content-Type", "text/xml; charset=utf-8");
	        httpConn.setRequestProperty("SOAPAction", soapAction);
	        httpConn.setRequestMethod("POST");
	        httpConn.setDoOutput(true);
	        httpConn.setDoInput(true);
	        
	        return httpConn;
		}
	private Document parseXmlFile(String in) {
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            InputSource is = new InputSource(new StringReader(in));
            return (Document) db.parse(is);
        } catch (ParserConfigurationException e) {
            throw new RuntimeException(e);
        } catch (SAXException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
	
	private Date dateTime(Date date, Date time) {
        Date result = null;
        
        try {
            Calendar calendarA = Calendar.getInstance();
            calendarA.setTime(date);
            Calendar calendarB = Calendar.getInstance();
            calendarB.setTime(time);
    
            calendarA.set(Calendar.HOUR_OF_DAY, calendarB.get(Calendar.HOUR_OF_DAY));
            calendarA.set(Calendar.MINUTE, calendarB.get(Calendar.MINUTE));
            calendarA.set(Calendar.SECOND, calendarB.get(Calendar.SECOND));
            calendarA.set(Calendar.MILLISECOND, calendarB.get(Calendar.MILLISECOND));
    
            result = calendarA.getTime();
        } catch(NullPointerException npe) {
            // Swallow.
        }
        
        return result;
    }
	
	private String encrypt(String data) throws Exception {
		byte[] encodedVal = data.getBytes();
		String encoded = Base64.getEncoder().encodeToString(encodedVal);
		//System.out.println("encoded: " + encoded);
		return encoded;
	}
	
	private String decrypt(String encryptedData) throws Exception {
		byte[] barr = Base64.getDecoder().decode(encryptedData);
		String decoded = new String(barr);
		//System.out.println("decoded: " + decoded);
		return decoded;
	}
}