package inventory.screen;

import inventory.model.Sale;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class SalesReport {

    private Stage saleStage = null;

    public void getSaleReportScreen(){

        DatePicker saleReportDatePicker = new DatePicker();
        saleReportDatePicker.setShowWeekNumbers(true);

        Button saleReportBtn = new Button("Go");

        HBox hBox = new HBox();
        hBox.setSpacing(5);
        hBox.setAlignment(Pos.CENTER);
        hBox.setPadding(new Insets(5,5,5,5));
        hBox.getChildren().addAll(saleReportDatePicker,saleReportBtn);


        TableView<Sale> saleTableView = getSaleReportTable();

        Button closeSaleViewBtn = new Button("Exit");
        closeSaleViewBtn.setOnAction(e -> saleStage.close() );

        HBox exitBtnWrapper = new HBox();
        exitBtnWrapper.setSpacing(5);
        exitBtnWrapper.setAlignment(Pos.CENTER);
        exitBtnWrapper.setPadding(new Insets(5,5,5,5));
        exitBtnWrapper.getChildren().addAll(closeSaleViewBtn);


        VBox vBox = new VBox();
        vBox.setSpacing(5);
        vBox.setPadding(new Insets(5,5,5,5));
        vBox.getChildren().addAll(hBox,saleTableView,exitBtnWrapper);


        Scene saleScene = new Scene(vBox,1200,500);

        saleStage = new Stage();
        saleStage.setScene(saleScene);
        saleStage.centerOnScreen();
        saleStage.setMaxWidth(1300);
        saleStage.setMaxHeight(600);
        saleStage.initModality(Modality.APPLICATION_MODAL);
        saleStage.showAndWait();
    }

    private TableView<Sale> getSaleReportTable(){
        TableView<Sale> saleTableView = new TableView<>();

        TableColumn timeOfSale = new TableColumn("Time");
        timeOfSale.setMinWidth(200);

        TableColumn productType = new TableColumn("Product Type");
        productType.setMinWidth(200);

        TableColumn productName = new TableColumn("Product Name");
        productName.setMinWidth(200);

        TableColumn productRate = new TableColumn("Rate");
        productRate.setMinWidth(200);

        TableColumn quantitySold = new TableColumn("Quantity Sold");
        quantitySold.setMinWidth(200);

        TableColumn saleAmt = new TableColumn("Amt");
        saleAmt.setMinWidth(200);

        saleTableView.getColumns().addAll(timeOfSale,productType,productName,productRate,quantitySold,saleAmt);

        return saleTableView;
    }

}
