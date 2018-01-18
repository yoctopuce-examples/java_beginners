package sample;

import com.yoctopuce.YoctoAPI.YAPI;
import com.yoctopuce.YoctoAPI.YAPI_Exception;
import com.yoctopuce.YoctoAPI.YSensor;
import com.yoctopuce.YoctoAPI.YTemperature;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.util.Duration;

import java.net.URL;
import java.util.ResourceBundle;

public class Controller implements Initializable {
    public Label lab_hwid;
    public Label lab_value;
    private int hardwaredetect;
    private Timeline timeline;
    private YSensor sensor;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        lab_hwid.setText("No sensor detected");
        lab_value.setText("N/A");
        try {
            YAPI.RegisterHub("usb");
        } catch (YAPI_Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("Init error");
            alert.setContentText(e.getLocalizedMessage());
            alert.showAndWait();
            Platform.exit();
        }
        timeline = new Timeline();
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.setAutoReverse(false);
        timeline.getKeyFrames().add(
                new KeyFrame(Duration.millis(100),
                        new EventHandler<ActionEvent>() {
                            @Override
                            public void handle(ActionEvent event) {
                                periodicTimer();
                            }
                        }));
        timeline.play();
    }


    private void periodicTimer() {
        try {
            if (hardwaredetect == 0) {
                YAPI.UpdateDeviceList();
            }
            hardwaredetect = (hardwaredetect + 1) % 20;
            YAPI.HandleEvents();
            if (sensor == null) sensor = YSensor.FirstSensor();
            if (sensor != null) {
                lab_hwid.setText(sensor.get_friendlyName());
                lab_value.setText(sensor.get_currentValue() + " " + sensor.get_unit());
            }
        } catch (YAPI_Exception e) {
            lab_hwid.setText("Sensor is offline");
            lab_value.setText("OFFLINE");
            sensor = null;
        }
    }
}