package inventory.home;

import inventory.dialog.CreateDialog;
import inventory.model.Product;
import inventory.model.ProductType;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

public class Home extends Application {


    public static void main(String[] args) {
        launch(args);
    }

    private Stage primaryStage = null;

    @Override
    public void start(Stage primaryStage) throws Exception{
        this.primaryStage = primaryStage;
        Pane mainLayout = getMainLayout();
        Scene scene = new Scene(mainLayout,400,600);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Inventory");
        primaryStage.show();
    }

    private Pane getMainLayout(){
        VBox vBox = new VBox();
        vBox.setSpacing(5);
        //vBox.setAlignment(Pos.CENTER);
        vBox.getChildren().addAll(getMenuBar(),getProductComboBox(),getProductTable(),getControlBox(),getSaleView());
        return vBox;
    }

    private MenuBar getMenuBar(){
        Menu menu = new Menu("File");

        MenuItem menuItem = new MenuItem("Exit");
        menuItem.setOnAction(event -> {
            primaryStage.close();
        });

        menu.getItems().add(menuItem);

        MenuBar menuBar = new MenuBar();
        menuBar.getMenus().add(menu);

        return menuBar;
    }

    private Pane getProductComboBox() {
        HBox hBox = new HBox();
        hBox.setAlignment(Pos.CENTER);
        hBox.setPadding(new Insets(10, 10, 10, 10));
        hBox.setSpacing(10);

        ObservableList<ProductType> productTypeList = FXCollections.observableArrayList(
                ProductType.IceCream,
                ProductType.SoftDrink
        );
        ComboBox<ProductType> comboBox = new ComboBox<>();
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

        tableView.getColumns().addAll(productNameColumn,inStockColumn,productRateColumn);

        return tableView;
    }

    private HBox getControlBox() {
        HBox hBox = new HBox(10);
        hBox.setAlignment(Pos.CENTER);

        Button createButton = new Button("Create");
        createButton.setStyle(getButtonStyle());
        createButton.setOnAction(e ->{
            CreateDialog createDialog = new CreateDialog();
            createDialog.create();
        });

        Button deleteButton = new Button("Del");
        deleteButton.setStyle(getButtonStyle());
        deleteButton.setOnAction(e ->{

        });

        Button addButton = new Button("Add");
        addButton.setStyle(getButtonStyle());
        addButton.setOnAction(e ->{

        });

        Button reduceButton = new Button("Reduce");
        reduceButton.setStyle(getButtonStyle());
        reduceButton.setOnAction(e ->{

        });

        hBox.getChildren().addAll(createButton,deleteButton,addButton,reduceButton);
        return hBox;
    }

    private String getButtonStyle(){
        String styleProps = "-fx-background-color: #336699;-fx-text-fill: #ffffff;";
        return styleProps;
    }


    private Pane getSaleView() {

        VBox vBox = new VBox();
        vBox.setAlignment(Pos.CENTER);
        vBox.setPadding(new Insets(15, 12, 15, 12));
        vBox.setSpacing(10);

        DatePicker datePicker = new DatePicker();

        vBox.getChildren().add(datePicker);
        return vBox;
    }


    private GridPane getSalentryView(){
        GridPane gridPane = new GridPane();
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.setPadding(new Insets(10,10,10,10));
        gridPane.setAlignment(Pos.CENTER);

        Label saleLabel = new Label("Today's Sale");
        saleLabel.setFont(Font.font("Ubuntu", FontWeight.BOLD, 25));
        gridPane.add(saleLabel,0,1,2,1);

        Label noOfIceCreamSold = new Label("IceCream Sold:");
        noOfIceCreamSold.setFont(Font.font("Ubuntu", FontWeight.NORMAL, 15));
        gridPane.add(noOfIceCreamSold,0,2);

        Label noOfIceCreamSoldCnt = new Label("0");
        noOfIceCreamSoldCnt.setFont(Font.font("Ubuntu", FontWeight.NORMAL, 15));
        gridPane.add(noOfIceCreamSoldCnt,1,2);

        Label totalIceCreamSale = new Label("IceCream Sale:");
        totalIceCreamSale.setFont(Font.font("Ubuntu", FontWeight.NORMAL, 15));
        gridPane.add(totalIceCreamSale,0,3);

        Label totalIceCreamSaleAmt = new Label("0");
        totalIceCreamSaleAmt.setFont(Font.font("Ubuntu", FontWeight.NORMAL, 15));
        gridPane.add(totalIceCreamSaleAmt,1,3);


        Label noOfSoftDrinkSold = new Label("SoftDrink Sold:");
        noOfSoftDrinkSold.setFont(Font.font("Ubuntu", FontWeight.NORMAL, 15));
        gridPane.add(noOfSoftDrinkSold,0,4);

        Label noOfSoftDrinkSoldCnt = new Label("0");
        noOfSoftDrinkSoldCnt.setFont(Font.font("Ubuntu", FontWeight.NORMAL, 15));
        gridPane.add(noOfSoftDrinkSoldCnt,1,4);

        Label totalSoftDrinkSale = new Label("SoftDrink Sale:");
        totalSoftDrinkSale.setFont(Font.font("Ubuntu", FontWeight.NORMAL, 15));
        gridPane.add(totalSoftDrinkSale,0,5);

        Label totalSoftDrinkSaleAmt = new Label("0");
        totalSoftDrinkSaleAmt.setFont(Font.font("Ubuntu", FontWeight.NORMAL, 15));
        gridPane.add(totalSoftDrinkSaleAmt,1,5);

        return gridPane;
    }

}
