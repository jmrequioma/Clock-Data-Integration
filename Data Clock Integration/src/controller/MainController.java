package controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import model.MainModel;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.Date;
import java.util.ResourceBundle;
import java.util.Timer;

import javafx.event.ActionEvent;

public class MainController implements Initializable {
	public MainModel mainModel = new MainModel();
	private boolean toggle = true;
	final String MIN_POLL = "pollIntervalMinutes";
	@FXML Button btn1;
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		// TODO Auto-generated method stub
		btn1.setText("Start");
		
	}
	
	// Event Listener on Button.onAction
	@FXML
	private void btnClick(ActionEvent event) throws SQLException {
		Timer timer = new Timer();
		if (btn1.getText() == "Start" || toggle) {
			
			try {
				String numOfMins = mainModel.getConfigValue(MIN_POLL);
				timer.schedule(new MainModel(), 0, 1000 * 60 * Integer.parseInt(numOfMins));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
			}
			btn1.setText("Stop");
		} else {
			btn1.setText("Start");
			toggle = false;
		}
	}
}
