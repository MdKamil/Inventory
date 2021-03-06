package inventory.screen;

import inventory.dao.InventoryDAO;
import inventory.model.SaleReport;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class SalesReport {

    private Stage saleStage;

    private ObservableList<SaleReport> totalSaleList;

    private TableView<SaleReport> saleTableView;

    public void getSaleReportScreen(){

        DatePicker saleReportDatePicker = new DatePicker();
        saleReportDatePicker.setValue(LocalDate.now());
        saleReportDatePicker.setShowWeekNumbers(true);

        Button saleReportBtn = new Button("Go");
        saleReportBtn.setOnAction(event -> {
            List<SaleReport> list = InventoryDAO.getSaleRecord(saleReportDatePicker.getValue());
            totalSaleList.clear();
            totalSaleList.addAll(list);
            saleTableView.refresh();
        });

        HBox hBox = new HBox();
        hBox.setSpacing(5);
        hBox.setAlignment(Pos.CENTER);
        hBox.setPadding(new Insets(5,5,5,5));
        hBox.getChildren().addAll(saleReportDatePicker,saleReportBtn);


        saleTableView = getSaleReportTable();
        List<SaleReport> saleList = InventoryDAO.getSaleRecord(LocalDate.now());
        totalSaleList = FXCollections.observableArrayList();
        totalSaleList.addAll(saleList);
        saleTableView.setItems(totalSaleList);

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

    private TableView<SaleReport> getSaleReportTable(){
        TableView<SaleReport> saleTableView = new TableView<>();

        TableColumn<SaleReport,LocalTime> timeOfSale = new TableColumn<>("Time");
        timeOfSale.setCellValueFactory(new PropertyValueFactory<>("saleTime"));
        timeOfSale.setMinWidth(200);

        TableColumn<SaleReport,String> productType = new TableColumn<>("Product Type");
        productType.setCellValueFactory(new PropertyValueFactory<>("productType"));
        productType.setMinWidth(200);

        TableColumn<SaleReport,String> productName = new TableColumn<>("Product Name");
        productName.setCellValueFactory(new PropertyValueFactory<>("productName"));
        productName.setMinWidth(200);

        TableColumn<SaleReport,Integer> productRate = new TableColumn<>("Rate");
        productRate.setCellValueFactory(new PropertyValueFactory<>("productRate"));
        productRate.setMinWidth(200);

        TableColumn<SaleReport,Integer> quantitySold = new TableColumn<>("Quantity Sold");
        quantitySold.setCellValueFactory(new PropertyValueFactory<>("quantitySold"));
        quantitySold.setMinWidth(200);

        TableColumn<SaleReport,Integer> saleAmt = new TableColumn<>("Amt");
        saleAmt.setCellValueFactory(new PropertyValueFactory<>("saleAmt"));
        saleAmt.setMinWidth(200);

        saleTableView.getColumns().addAll(timeOfSale,productType,productName,productRate,quantitySold,saleAmt);

        return saleTableView;
    }

}
