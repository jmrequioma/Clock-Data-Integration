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
	@FXML Button btn1;
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		// TODO Auto-generated method stub
		btn1.setText("Start");
		
	}
	
	// Event Listener on Button.onAction
	@FXML
	public void btnClick(ActionEvent event) throws SQLException {
		Timer timer = new Timer();
		if (btn1.getText() == "Start" || toggle) {
			//timer.schedule(new MainModel(), 0, 2000 * 60);
			//try {
				//Date date = mainModel.readDateFromFile();
				//System.out.println(date);
				//mainModel.writeDateToFile(date);
			//} catch (IOException e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
			//}
			/*
			try {
				String answer = mainModel.getConfigValue("cutoff");
				System.out.println(answer);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			*/
			mainModel.retrieve();
			btn1.setText("Stop");
		} else {
			btn1.setText("Start");
			toggle = false;
		}
	}
}
