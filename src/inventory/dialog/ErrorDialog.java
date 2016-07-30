package inventory.dialog;

import javafx.scene.control.Alert;

public class ErrorDialog {

    public static void show(String text){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setGraphic(null);
        alert.setContentText(text);
        alert.showAndWait();
    }

}
