import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.util.*;
import java.io.*;
import javafx.collections.*;
import javafx.collections.transformation.*;
import javafx.scene.control.cell.*;
import javafx.beans.property.*;

public class FridgeFX extends Application {

  // used as ChoiceBox value for filter
	public enum FILTER_COLUMNS {
		ITEM,
		SECTION,
		BOUGHT_DAYS_AGO
	};

  private FridgeDSC fridgeDSC;
  private ObservableList<Grocery> tableData;
  private TableView<Grocery> tableView = new TableView<>();

  public void init() throws Exception {
    //TODO 2-01: COMPLETED
		fridgeDSC = new FridgeDSC();
    fridgeDSC.connect();
	}

  @Override
	public void start(Stage stage) throws Exception
	{
    //TODO 2-02: COMPLETED
		build(stage);
		stage.setTitle(getClass().getName());
		stage.show();

    //TODO 2-03: COMPLETED
    Thread.currentThread().setUncaughtExceptionHandler((thread, exception) ->
    {
      System.out.println("ERROR: " + exception);
      Alert alert = new Alert(Alert.AlertType.WARNING);
      alert.setContentText("Exception thrown: " + exception);
      alert.showAndWait();
    });
	}

  public void build(Stage stage) throws Exception
	{
    VBox root = new VBox();
    stage.setScene(new Scene(root));
    {
			root.getStylesheets().add("fridge.css");
		}

	//Filter ChoiceBox
	ChoiceBox<FILTER_COLUMNS> choices = new ChoiceBox<FILTER_COLUMNS>();
	choices.getItems().addAll(FILTER_COLUMNS.values());

	//Set default value to ITEM
	choices.setValue(FILTER_COLUMNS.ITEM);
	choices.getSelectionModel().selectedItemProperty().addListener(
		// ChangeListener
		(ov, oldValue, newValue) ->
		{
			if(choices.getValue() == FILTER_COLUMNS.ITEM){
				//Filter columns in the tableView according to text Filter
			} else if (choices.getValue() == FILTER_COLUMNS.SECTION){
				//TextField will target the values in the section column
			} else {
				getExpiredCheckBox();
			}
		});

  //ADD FILTER OPTIONS
  TextField filterTF = getFilterTextField();
  Label filterByLB = getFilterLabel();

  Pane filterPane = new FlowPane(filterTF, filterByLB, getFilterChoiceBox(), getExpiredCheckBox());
  root.getChildren().add(filterPane);
  {
    filterPane.setStyle("-fx-alignment: CENTER-LEFT");
  }

  //Add table view
  tableView = getTableView();

  root.getChildren().add(new FlowPane(tableView));

  //Load data into table view
  loadData();

  //ADD  BUTTONS TO ADD, UPDATE ONE, DELETE, EXIT
  Button addBT = getAddButton();
  Button updateBT = getUpdateButton();
  Button deleteBT = getDeleteButton();
  Button exitBT = getExitButton();
  Pane actionPane = new FlowPane(addBT, updateBT, deleteBT, exitBT);
	actionPane.setId("actionPane");
  root.getChildren().add(actionPane);

	//ADD HIDDEN filter controls
	ComboBox selectItems = getHiddenItemComboBox();
	ChoiceBox hiddenChoices = getHiddenSectionChoiceBox();
	TextField hiddenQuantityTextField = getHiddenQuantityTextField();
	Pane hiddenFilterPane = new FlowPane(selectItems, hiddenChoices, hiddenQuantityTextField);
	hiddenFilterPane.setId("hiddenFilterPane");
	root.getChildren().add(hiddenFilterPane);
	{
		hiddenFilterPane.setStyle("-fx-alignment: CENTER");
		root.setStyle("-fx-spacing: 10");
	}

	//ADD HIDDEN SAVE/CLEAR BUTTONS
	Button hiddenClearButton = getHiddenClearButton();
	Button hiddenSaveButton = getHiddenSaveButton();
	Pane hiddenButtonPane = new FlowPane(hiddenClearButton, hiddenSaveButton);
	root.getChildren().add(hiddenButtonPane);
	{
		hiddenButtonPane.setStyle("-fx-alignment: CENTER");
	}
}

  public TableView<Grocery> getTableView() throws Exception
  {
    TableView<Grocery> tableView = new TableView<Grocery>();

    TableColumn<Grocery, String> idColumn = new TableColumn<Grocery, String>("Id");
    idColumn.setCellValueFactory(new PropertyValueFactory<Grocery, String>("id"));

    TableColumn<Grocery, String> itemNameColumn = new TableColumn<Grocery, String>("Item");
    itemNameColumn.setCellValueFactory(new PropertyValueFactory<Grocery, String>("Item"));

    TableColumn<Grocery, Integer> quantityColumn = new TableColumn<Grocery, Integer>("QTY");
    quantityColumn.setCellValueFactory(new PropertyValueFactory<Grocery, Integer>("QTY"));

    TableColumn<Grocery, String> sectionColumn = new TableColumn<Grocery, String>("Section");
    sectionColumn.setCellValueFactory(new PropertyValueFactory<Grocery, String>("Section"));

    TableColumn<Grocery, String> daysAgoColumn = new TableColumn<Grocery, String>("Bought");
    daysAgoColumn.setCellValueFactory(new PropertyValueFactory<Grocery, String>("Bought"));

    tableView.getColumns().add(idColumn);
    tableView.getColumns().add(itemNameColumn);
    tableView.getColumns().add(quantityColumn);
    tableView.getColumns().add(sectionColumn);
    tableView.getColumns().add(daysAgoColumn);

    idColumn.setMinWidth(50);
    itemNameColumn.setMinWidth(200);
    quantityColumn.setMinWidth(50);
    sectionColumn.setMinWidth(150);
    daysAgoColumn.setMinWidth(50);
    tableView.setMinWidth(700);
    tableView.setMaxWidth(800);

    return tableView;
  }

  public void loadData() throws Exception{
    tableData = FXCollections.observableArrayList(fridgeDSC.getAllGroceries());
    tableView.setItems(tableData);
  }

  //TODO FILTER OPTIONS
  public TextField getFilterTextField()
  {
    TextField filterTF = new TextField();

  /*  ERROR
	//Wrap table data in a filtered list
    FilteredList<Grocery> filteredList  =
      new FilteredList<>(tableData, g -> true);

    //Wrap filtered list in a sorted list
    SortedList<Grocery> sortedList = new SortedList<>(filteredList);

    //Bind the comparator of the sorted list to that of the table
    //So when the later changes, the former changes also
    sortedList.comparatorProperty().bind(tableView.comparatorProperty());
    tableView.setItems(sortedList);

  // add a change listener
 filterTF.textProperty().addListener((observable, oldValue, newValue) ->
  {
    filteredList.setPredicate(grocery ->
      {
        // If filter text is empty, display all products
        if (newValue == null || newValue.isEmpty())
        {
          return true; //display the row
        }

        String filterString = newValue.toUpperCase();
        if(grocery.getItemName().toUpperCase().contains(filterString))
        {
          return true; //Display the row
        } else {
          return false; //Do not display the row
        }
      });
    });*/
    return filterTF;
  }

  public Label getFilterLabel()
  {
    Label filterByLB = new Label("Filter By: ");
    return filterByLB;
  }

	//TODO
 public ChoiceBox getFilterChoiceBox()
  {
    ChoiceBox<FILTER_COLUMNS> choices = new ChoiceBox<FILTER_COLUMNS>();
    choices.getItems().addAll(FILTER_COLUMNS.values());

    //Set default value to ITEM
    choices.setValue(FILTER_COLUMNS.ITEM);

		//Change listener
		choices.getSelectionModel().selectedItemProperty().addListener(
		(ov, oldValue, newValue) ->
		{
			if(choices.getValue() == FILTER_COLUMNS.ITEM)
			{
				System.out.println("Test expired CheckBox");
				getExpiredCheckBox().setDisable(false);
			}
		});

    return choices;
  }

  public CheckBox getExpiredCheckBox()
  {
    CheckBox showExpireOnlyCB = new CheckBox("Show Expire Only");
		showExpireOnlyCB.setDisable(true);

		if(getFilterChoiceBox().getValue() == FILTER_COLUMNS.BOUGHT_DAYS_AGO)
		{
			showExpireOnlyCB.setDisable(false);
		}

    return showExpireOnlyCB;
  }

	public Button getAddButton() throws Exception
	{
		Button addBT = new Button("ADD");

		addBT.setOnAction(e -> {
			//clicking this should show the hidden filter options
			/*
			ERROR: unable to add grocery
			try{
				groceries.addGrocery(id, item, date, quantity, section);
			} catch (Exception exception){
				throw new RuntimeException(exception.getMessage());
			}

			//Check if the grocery has been added
			System.out.println("\n ADD GROCERY: \n" + FridgeDSC.getAllGroceries());
			*/
		});

		return addBT;
	}

//Hidden Container controls
	private ComboBox getHiddenItemComboBox(){
		ComboBox<FridgeDSC> selectItems = new ComboBox<FridgeDSC>();
    //selectItems.getItems().addAll(FridgeDSC.getItemName().values());
		selectItems.setPromptText("Select item ");

		return selectItems;
	}

	private ChoiceBox getHiddenSectionChoiceBox()
	{
		ChoiceBox<FridgeDSC.SECTION> hiddenChoices = new ChoiceBox<FridgeDSC.SECTION>();
    hiddenChoices.getItems().addAll(FridgeDSC.SECTION.values());
		hiddenChoices.getSelectionModel().select(0);

		return hiddenChoices;
	}

	private TextField getHiddenQuantityTextField()
	{
		TextField hiddenQuantityTextField = new TextField();

		return hiddenQuantityTextField;
	}

	private Button getHiddenClearButton(){
		Button hiddenClearButton = new Button ("CLEAR");
		hiddenClearButton.setOnAction(e -> {
		});

		return hiddenClearButton;
	}

	private Button getHiddenSaveButton(){
		Button hiddenSaveButton = new Button ("SAVE");
		hiddenSaveButton.setOnAction(e -> {
		});

		return hiddenSaveButton;
	}

	public Button getUpdateButton() throws Exception
	{
		Button updateBT = new Button ("UPDATE ONE");

		updateBT.setOnAction(e ->
		{
			//Ask for CONFIRMATION
			Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
			confirm.setTitle("Really Update?");
			confirm.setContentText("Are you sure you want to update? ");

			Optional<ButtonType> result = confirm.showAndWait();
			/* ERROR: can't update grocery list

			try{
				FridgeDSC.useGrocery(id, item, date, quantity, section);
			} catch (Exception exception){
				throw new RuntimeException(exception.getMessage());
			}

			//Check to see if update has occured
			System.out.println("\n UPDATE GROCERY: " + FridgeDSC.getAllGroceries());
			*/
		});

		return updateBT;
	}

  public Button getDeleteButton()
  {
  	Button deleteBT = new Button("DELETE");

  	deleteBT.setOnAction(e ->
  	{
  		//Ask for CONFIRMATION
  		Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
  		confirm.setTitle("Confirmation");
  		confirm.setContentText("Are you sure? Delete selected grocery?");

  		Optional<ButtonType> result = confirm.showAndWait();
  		/*ERROR: Can't delete grocery

			if(result.isPresent() && result.get() == ButtonType.OK)
  		{
  			Grocery grocery = tableView.getSelectionModel.getSelectedItem();
  		}

  		//remove() can throw exception
  		try{
  			fridgeDSC.removeGrocery(grocery.getId());
  			groceries.remove(grocery);
  		}
  		catch(Exception exception)
  		{
  			throw new RuntimeException(exception.getMessage());
  		}*/
  	});

  	return deleteBT;
  }

  public Button getExitButton() {
  	Button exitBT = new Button ("EXIT");

  	exitBT.setOnAction(e ->
  	{
  		//Ask for CONFIRMATION
  		Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
  		confirm.setTitle("Confirmation");
  		confirm.setContentText("Are you sure you want to exit?");

  		Optional<ButtonType> result = confirm.showAndWait();

			//Close the stage
			Stage stage = (Stage) exitBT.getScene().getWindow();
    	stage.close();
  	});

  	return exitBT;
  }

	public void stop() throws Exception {
		 try {
			 FridgeDSC.disconnect();
		 } catch (Exception ex)
		 {
			 System.out.println("Error program cannot close: " + ex);
		 }
	}
}
