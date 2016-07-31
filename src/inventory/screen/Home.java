package inventory.screen;

import inventory.dao.DB;
import inventory.dao.InventoryDAO;
import inventory.dialog.*;
import inventory.model.DaySale;
import inventory.model.Product;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
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

    private TableView<Product> productTable;

    private TableView<DaySale> saleTable;

    private ComboBox<String> comboBox;

    public static void main(String[] args) {
        launch(args);
    }

    private Stage primaryStage;

    @Override
    public void start(Stage primaryStage) throws Exception{
        dbInit();
        this.primaryStage = primaryStage;
        Pane mainLayout = getMainLayout();
        Scene scene = new Scene(mainLayout,810,700);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Inventory");
        primaryStage.show();
    }

    private void dbInit() {
        DB.loadDriver();

        productTypeList = FXCollections.observableArrayList();
        if(InventoryDAO.checkTableExists("PRODUCT_TYPE")){
            List<String> pTypeList = InventoryDAO.retrieveProductType();
            productTypeList.addAll(pTypeList);
        }else {
            InventoryDAO.createProductTypeTable();
        }

        productList = FXCollections.observableArrayList();
        if(InventoryDAO.checkTableExists("PRODUCT")){
            //List<Product> pList = InventoryDAO.retrieveProduct();
            //productList.addAll(pList);
        }else {
            InventoryDAO.createProductTable();
        }

        todaySale = FXCollections.observableArrayList();
        if(InventoryDAO.checkTableExists("SALE_REPORT")){
            //DaySale daySale = InventoryDAO.getTodaySaleReport(LocalDate.now());
            //todaySale.add(daySale);
        } else {
            InventoryDAO.createSaleReportTable();
        }

    }

    private Pane getMainLayout(){
        VBox vBox = new VBox();
        vBox.setSpacing(5);
        vBox.setPadding(new Insets(5,5,5,5));

        MenuBar menuBar = getMenuBar();

        Pane productTypeCB = getProductComboBox();

        productTable = getProductTable();
        productTable.setItems(productList);

        Pane controlBox = getControlBox();

        Pane footer = getSaleFooter();

        vBox.getChildren().addAll(menuBar,productTypeCB,productTable,controlBox,footer);
        return vBox;
    }

    private MenuBar getMenuBar(){
        Menu menu = new Menu("File");

        MenuItem newProductType = new MenuItem("New");
        newProductType.setOnAction(event -> {
            String type = CreateProductType.show();
            if(type != null){
                if(!type.isEmpty()) {
                    productTypeList.add(type);
                }
            }
        });

        MenuItem sale = new MenuItem("SaleReport");
        sale.setOnAction(event -> {
            SalesReport salesReport = new SalesReport();
            salesReport.getSaleReportScreen();
        });

        MenuItem exit = new MenuItem("Exit");
        exit.setOnAction(event -> {
            DB.disconnect();
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

        comboBox = new ComboBox<>(productTypeList);
        comboBox.setPromptText("Product");

        comboBox.setOnAction(event -> {
            String type = comboBox.getValue();
            List<Product> list = InventoryDAO.getProductOfType(type);
            DaySale daySale = InventoryDAO.getTodaySaleReport(LocalDate.now(),type);

            // Refresh Product tableView.
            productList.clear();
            productList.addAll(list);
            productTable.refresh();

            // Refresh Daily Sale Table view.
            todaySale.clear();
            todaySale.add(daySale);
            saleTable.refresh();

        });

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
        createButton.setOnAction(e -> {
            Product product = CreateProduct.show(productTypeList);
            if(product != null){
                String type = comboBox.getValue();
                if(type.equals(product.getProductType())) {
                    productList.add(product);
                }
                productTable.refresh();
                productTable.getSelectionModel().clearSelection();
            }
        });

        Button deleteButton = new Button("Remove");
        deleteButton.setStyle(getButtonStyle());
        deleteButton.setOnAction(e ->{
            Product product = productTable.getSelectionModel().getSelectedItem();
            if(product != null){
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Remove Product");
                alert.setHeaderText(null);
                alert.setGraphic(null);
                alert.setContentText("Do you want to remove the selected product !!!");
                Optional<ButtonType> result = alert.showAndWait();
                if (result.isPresent() && result.get() == ButtonType.OK){
                    boolean rs = InventoryDAO.deleteProduct(product.getProductID());
                    if(rs){
                        productList.remove(product);
                    }else {
                        ErrorDialog.show("Couldn't remove product !!!");
                    }
                    productTable.refresh();
                    productTable.getSelectionModel().clearSelection();
                }
            }
        });

        Button editButton = new Button("Edit");
        editButton.setStyle(getButtonStyle());
        editButton.setOnAction(e -> {
            Product product = productTable.getSelectionModel().getSelectedItem();
            if(product != null) {
                EditProduct.show(productTypeList, product);
            }
            productTable.refresh();
            productTable.getSelectionModel().clearSelection();
        });

        Button updateButton = new Button("Update Stock");
        updateButton.setStyle(getButtonStyle());
        updateButton.setOnAction(e ->{
            Product product = productTable.getSelectionModel().getSelectedItem();
            if(product != null){
                int noOfUnit = UpdateProduct.show(product,"Update Stock","ADD");
                product.setLeftInStock(product.getLeftInStock() + noOfUnit);
            }
            productTable.refresh();
            productTable.getSelectionModel().clearSelection();
        });

        Button saleButton = new Button("Sell");
        saleButton.setStyle(getButtonStyle());
        saleButton.setOnAction(e ->{
            Product product = productTable.getSelectionModel().getSelectedItem();
            if(product != null){
                int noOfUnit = UpdateProduct.show(product,"Sell Stock","DEDUCT");
                if(noOfUnit > 0) {
                    product.setLeftInStock(product.getLeftInStock() - noOfUnit);
                    DaySale daySale = todaySale.get(0);
                    daySale.setQuantitySold(daySale.getQuantitySold() + noOfUnit);
                    daySale.setSaleAmt(daySale.getSaleAmt() + (noOfUnit * product.getProductRate()));
                    saleTable.refresh();
                }
            }
            productTable.refresh();
            productTable.getSelectionModel().clearSelection();
        });

        hBox.getChildren().addAll(createButton,deleteButton,editButton,updateButton,saleButton);
        return hBox;
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

        saleTable = new TableView<>();
        saleTable.setItems(todaySale);
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