package action;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.Timer;

import javafx.event.ActionEvent;

public class MainController implements Initializable {
	public MainModel mainModel = new MainModel();
	Timer timer;
	private int toggle = 0;
	final String DATE_POLL = "lastProcessed";
	final String MIN_POLL = "pollIntervalMinutes";
	@FXML Label cutOffLbl;
	@FXML Button btnStart;
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		// TODO Auto-generated method stub
		btnStart.setText("Start");
		try {
			cutOffLbl.setText(mainModel.getConfigValue(DATE_POLL));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	// Event Listener on Button.onAction
	@SuppressWarnings("null")
	@FXML
	private void btnClickStart(ActionEvent event) throws SQLException {
		if (toggle == 0) {
			timer = new Timer();
			System.out.println("1");
			try {
				String numOfMins = mainModel.getConfigValue(MIN_POLL);
				timer.schedule(new MainModel(), 0, 1000 * 60 * Integer.parseInt(numOfMins));
				toggle++;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			btnStart.setText("Stop");
		} else {
			System.out.println("2");
			toggle = 0;
			timer.cancel();
			timer.purge();
			btnStart.setText("Start");
			//System.exit(0);
		}
	}
	
	@FXML
	private void btnClickStop(ActionEvent event) {
		
	}
	
	@FXML
	private void btnRefreshClick(ActionEvent event) {
		try {
			cutOffLbl.setText(mainModel.getConfigValue(DATE_POLL));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void setCutOffLbl(String lbl) {
		cutOffLbl.setText(lbl);
	}
}
