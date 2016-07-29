package inventory.screen;

import inventory.dao.DB;
import inventory.dao.InventoryDAO;
import inventory.model.DaySale;
import inventory.model.Product;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class Home extends Application {

    private static final Logger logger = LogManager.getLogger(Home.class);

    private ObservableList<String> productTypeList;

    private ObservableList<Product> productList;

    private ObservableList<DaySale> todaySale;

    public static void main(String[] args) {
        launch(args);
    }

    private Stage primaryStage = null;

    @Override
    public void start(Stage primaryStage) throws Exception{
        initVar();
        //dbInit();
        this.primaryStage = primaryStage;
        Pane mainLayout = getMainLayout();
        Scene scene = new Scene(mainLayout,810,700);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Inventory");
        primaryStage.show();
    }

    private void initVar() {
        productTypeList = FXCollections.observableArrayList();
        productList = FXCollections.observableArrayList();
        todaySale = FXCollections.observableArrayList();
    }

    private void dbInit() {
        DB.loadDriver();

        List<String> pTypeList = InventoryDAO.retrieveProductType();
        productTypeList.addAll(pTypeList);

        List<Product> pList = InventoryDAO.retrieveProduct();
        productList.addAll(pList);

        if(InventoryDAO.checkSaleReportTable()) {
            DaySale daySale = InventoryDAO.getTodaySaleReport(LocalDate.now());
            todaySale.add(daySale);
        }

    }

    private Pane getMainLayout(){
        VBox vBox = new VBox();
        vBox.setSpacing(5);
        vBox.setPadding(new Insets(5,5,5,5));
        vBox.getChildren().addAll(getMenuBar(),getProductComboBox(),getProductTable(),getControlBox(),getSaleFooter());
        return vBox;
    }

    private MenuBar getMenuBar(){
        Menu menu = new Menu("File");

        MenuItem newProductType = new MenuItem("New");
        newProductType.setOnAction(event -> {
            TextInputDialog newProductTypeDialog = new TextInputDialog();
            newProductTypeDialog.setTitle("Create new ProductType");
            newProductTypeDialog.setContentText("Enter ProductType:");
            newProductTypeDialog.setHeaderText(null);
            newProductTypeDialog.setGraphic(null);

            Node button = newProductTypeDialog.getDialogPane().lookupButton(ButtonType.OK);
            button.setDisable(true);

            TextField textField = newProductTypeDialog.getEditor();
            textField.textProperty().addListener((observable, oldValue, newValue) -> {
                String value = newValue.trim();
                if(!value.isEmpty()){
                    button.setDisable(false);
                }else {
                    button.setDisable(true);
                }
            });

            Optional<String> optional = newProductTypeDialog.showAndWait();
            if(optional.isPresent()){
                String typeName = optional.get().trim();
                boolean result = InventoryDAO.updateProductTypeTable(typeName);
                if(result){
                    productTypeList.add(typeName);
                }
                textField.clear();
            }
        });

        MenuItem sale = new MenuItem("SaleReport");
        sale.setOnAction(event -> {
            SalesReport salesReport = new SalesReport();
            salesReport.getSaleReportScreen();
        });

        MenuItem exit = new MenuItem("Exit");
        exit.setOnAction(event -> {
            logger.info("APPLICATION SHUTDOWN");
            primaryStage.close();
        });

        menu.getItems().addAll(newProductType,sale,exit);

        MenuBar menuBar = new MenuBar();
        menuBar.getMenus().add(menu);
        return menuBar;
    }

    private Pane getProductComboBox() {
        HBox hBox = new HBox();
        hBox.setAlignment(Pos.CENTER);
        hBox.setPadding(new Insets(5, 5, 5, 5));
        hBox.setSpacing(5);

        ComboBox<String> comboBox = new ComboBox<>();
        comboBox.setPromptText("Product");
        comboBox.getItems().addAll(productTypeList);

        hBox.getChildren().add(comboBox);
        return hBox;
    }

    private TableView<Product> getProductTable() {
        TableView<Product> tableView = new TableView<>();

        TableColumn<Product,String> productNameColumn = new TableColumn<>("ProductName");
        productNameColumn.setMinWidth(200);
        productNameColumn.setCellValueFactory(new PropertyValueFactory<>("productName"));

        TableColumn<Product,Integer> inStockColumn = new TableColumn<>("In Stock");
        inStockColumn.setMinWidth(200);
        inStockColumn.setCellValueFactory(new PropertyValueFactory<>("leftInStock"));

        TableColumn<Product,Integer> productRateColumn = new TableColumn<>("Rate");
        productRateColumn.setMinWidth(200);
        productRateColumn.setCellValueFactory(new PropertyValueFactory<>("productRate"));

        TableColumn<Product,Integer> netWeightColumn = new TableColumn<>("Net.Wt");
        netWeightColumn.setMinWidth(200);
        netWeightColumn.setCellValueFactory(new PropertyValueFactory<>("netWeight"));

        tableView.getColumns().addAll(productNameColumn,inStockColumn,productRateColumn,netWeightColumn);
        return tableView;
    }

    private Pane getControlBox() {
        HBox hBox = new HBox(10);
        hBox.setAlignment(Pos.CENTER);

        Button createButton = new Button("Add");
        createButton.setStyle(getButtonStyle());
        createButton.setOnAction(e ->{

            // Custom dialog.
            Dialog<ButtonType> dialog = new Dialog<>();
            dialog.setTitle("Add Product");
            dialog.getDialogPane().getButtonTypes().addAll(ButtonType.FINISH, ButtonType.CANCEL);

            VBox vBox = new VBox();
            vBox.setSpacing(10);

            ChoiceBox productType = new ChoiceBox();
            productType.setItems(FXCollections.observableArrayList(
                    "Icecream","SoftDrink")
            );

            TextField productName = new TextField();
            productName.setPrefWidth(350);
            productName.setPromptText("Product Name");

            TextField totalStock = new TextField();
            totalStock.setPrefWidth(350);
            totalStock.setPromptText("Stock");

            TextField rate = new TextField();
            rate.setPrefWidth(350);
            rate.setPromptText("Rate");

            TextField netWeight = new TextField();
            netWeight.setPrefWidth(350);
            netWeight.setPromptText("Net Weight");

            vBox.getChildren().addAll(productType,productName,totalStock,rate,netWeight);

            dialog.getDialogPane().setContent(vBox);

            Node finishBtn =  dialog.getDialogPane().lookupButton(ButtonType.FINISH);
            finishBtn.setDisable(true);

            productName.textProperty().addListener((observable, oldValue, newValue) -> {
                if(!newValue.trim().isEmpty() && check(totalStock.getText().trim()) && check(rate.getText().trim())){
                    finishBtn.setDisable(false);
                }else {
                    finishBtn.setDisable(true);
                }
            });

            totalStock.textProperty().addListener((observable, oldValue, newValue) -> {
                if(!productName.getText().isEmpty() && check(newValue.trim()) && check(rate.getText().trim())){
                    finishBtn.setDisable(false);
                }else {
                    finishBtn.setDisable(true);
                }
            });

            rate.textProperty().addListener((observable, oldValue, newValue) -> {
                if(!productName.getText().isEmpty() && check(totalStock.getText().trim()) && check(newValue.trim())){
                    finishBtn.setDisable(false);
                }else {
                    finishBtn.setDisable(true);
                }
            });

            Optional<ButtonType> result = dialog.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.FINISH){
                System.out.println("product added");
            } else {
                System.out.println("not added");
            }

        });

        Button deleteButton = new Button("Remove");
        deleteButton.setStyle(getButtonStyle());
        deleteButton.setOnAction(e ->{
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Remove Product");
            alert.setHeaderText(null);
            alert.setContentText("Do you want to remove the selected product !!!");
            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK){

            }
        });

        Button addButton = new Button("Update Stock");
        addButton.setStyle(getButtonStyle());
        addButton.setOnAction(e ->{
            TextInputDialog inputDialog = new TextInputDialog();
            inputDialog.setContentText("Enter no of units:");
            inputDialog.setTitle("Update stock");
            inputDialog.setHeaderText(null);
            inputDialog.setGraphic(null);

            Node button = inputDialog.getDialogPane().lookupButton(ButtonType.OK);
            button.setDisable(true);

            TextField textField = inputDialog.getEditor();
            textField.textProperty().addListener((observable, oldValue, newValue) -> {
                String value = newValue.trim();
                if(check(value)){
                    button.setDisable(false);
                }else {
                    button.setDisable(true);
                }
            });

            Optional<String> result = inputDialog.showAndWait();
            if(result.isPresent()){
                String quantity = result.get();
                System.out.println(quantity);
            }
        });

        Button reduceButton = new Button("SaleReport");
        reduceButton.setStyle(getButtonStyle());
        reduceButton.setOnAction(e ->{
            TextInputDialog inputDialog = new TextInputDialog();
            inputDialog.setTitle("Update stock");
            inputDialog.setContentText("Enter no of units:");
            inputDialog.setHeaderText(null);
            inputDialog.setGraphic(null);

            Node button = inputDialog.getDialogPane().lookupButton(ButtonType.OK);
            button.setDisable(true);

            TextField textField = inputDialog.getEditor();
            textField.textProperty().addListener((observable, oldValue, newValue) -> {
                String value = newValue.trim();
                if(check(value)){
                    button.setDisable(false);
                }else {
                    button.setDisable(true);
                }
            });

            Optional<String> result = inputDialog.showAndWait();
            if(result.isPresent()){
                String quantity = result.get();
                System.out.println(quantity);
            }
        });

        hBox.getChildren().addAll(createButton,deleteButton,addButton,reduceButton);
        return hBox;
    }

    private boolean check(String text) {
        try {
            Integer.parseInt(text);
        } catch(NumberFormatException e) {
            return false;
        } catch(NullPointerException e) {
            return false;
        }
        return true;
    }

    private String getButtonStyle(){
        String styleProps = "-fx-background-color: #336699;-fx-text-fill: #ffffff;";
        return styleProps;
    }


    private Pane getSaleFooter() {
        VBox vBox = new VBox();
        vBox.setAlignment(Pos.CENTER);
        vBox.setPadding(new Insets(15, 12, 15, 12));
        vBox.setSpacing(10);

        TableView<DaySale> saleTable = new TableView<>();
        saleTable.setPrefHeight(100);

        TableColumn<DaySale,Integer> totalSold = new TableColumn<>("Total Sold");
        totalSold.setCellValueFactory(new PropertyValueFactory<>("quantitySold"));
        totalSold.setMinWidth(200);

        TableColumn<DaySale,Integer> totalAmt = new TableColumn<>("Total Amt");
        totalAmt.setCellValueFactory(new PropertyValueFactory<>("saleAmt"));
        totalAmt.setMinWidth(200);

        saleTable.getColumns().addAll(totalSold,totalAmt);

        vBox.getChildren().addAll(saleTable);
        return vBox;
    }

}
