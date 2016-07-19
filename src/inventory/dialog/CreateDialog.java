package inventory.dialog;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class CreateDialog {

    public void create() {

        Stage stage = new Stage();

        GridPane gridPane = new GridPane();
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.setAlignment(Pos.TOP_CENTER);

        TextField productName = new TextField();
        productName.setPromptText("Product Name");
        productName.setMaxWidth(500);
        gridPane.add(productName,0,1);

        TextField totalStock = new TextField();
        totalStock.setPromptText("Stock");
        gridPane.add(totalStock,0,2);

        TextField rate = new TextField();
        rate.setPromptText("Rate");
        gridPane.add(rate,0,3);

        Button createButton = new Button("Create");
        createButton.setOnAction(e ->{
           stage.close();
        });

        gridPane.add(createButton,0,4);

        Scene scene = new Scene(gridPane,400,200);

        stage.setScene(scene);
        stage.centerOnScreen();
        stage.setMaxWidth(400);
        stage.setMaxHeight(200);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.showAndWait();
    }

}
