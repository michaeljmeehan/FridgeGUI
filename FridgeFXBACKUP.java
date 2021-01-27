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

	// the data source controller
	private FridgeDSC fridgeDSC;

	public void init() throws Exception {
		// creating an instance of the data source controller to be used
		// in this application
		fridgeDSC = new FridgeDSC();

		//COMPLETED...?
		/* TODO 2-01 - TO COMPLETE ****************************************
		 * call the data source controller database connect method
		 * NOTE: that database connect method throws exception
		 */

		 fridgeDSC.connect();
	}

	public void start(Stage stage) throws Exception {

		/* TODO 2-02 - COMPLETED ****************************************
		 * - this method is the start method for your application
		 * - set application title
		 * - show the stage
		 */

		 build(stage);
		 stage.setTitle(getClass().getName());
		 stage.show();

		 //COMPLETED
		/* TODO 2-03 - COMPLETED ****************************************
		 * currentThread uncaught exception handler
		 */

		 Thread.currentThread().setUncaughtExceptionHandler((thread, exception) ->
		 {
			 System.out.println("ERROR: " + exception);
			 Alert alert = new Alert(Alert.AlertType.WARNING);
			 alert.setContentText("Exception thrown: " + exception);
			 alert.showAndWait();
		 });
	}

	public void build(Stage stage) throws Exception {
        /* If you are getting the FX up and running before the DSC then you need to comment out the DSC calls above
         * and manually create an array of groceries to add to the tableview array.
         *
         * LocalDate date = LocalDate.now();
         * Item myItem = new Item("Mars Bar", true);
         * Grocery mygr1 = new Grocery(1,myItem,date,10,FridgeDSC.SECTION.COOLING);
         * Grocery mygr2 = new Grocery(2,myItem,date,15,FridgeDSC.SECTION.COOLING);
         * ArrayList<Grocery> mygrs = new ArrayList<>();
         * mygrs.add(mygr1);
         * mygrs.add(mygr2);
         *
         * then below *after* the TableView has been set up
         *
         * tableView.setItems(tableData);
         *
         * add the manually created array
         * tableData.addAll(mygrs);
         */

		// Create table data (an observable list of objects)
		ObservableList<Grocery> tableData = FXCollections.observableArrayList();

		// Define table columns
		TableColumn<Grocery, String> idColumn = new TableColumn<Grocery, String>("Id");
		TableColumn<Grocery, String> itemNameColumn = new TableColumn<Grocery, String>("Item");
		TableColumn<Grocery, Integer> quantityColumn = new TableColumn<Grocery, Integer>("QTY");
		TableColumn<Grocery, String> sectionColumn = new TableColumn<Grocery, String>("Section");
		TableColumn<Grocery, String> daysAgoColumn = new TableColumn<Grocery, String>("Bought");


		//COMPLETED
		/* TODO 2-04 - TO COMPLETE ****************************************
		 * for each column defined, call their setCellValueFactory method
		 * using an instance of PropertyValueFactory
		 */

		 idColumn.setCellValueFactory(
		 	new PropertyValueFactory<Grocery, String>("id"));
		 itemNameColumn.setCellValueFactory(
		  new PropertyValueFactory<Grocery, String>("Item"));
		 quantityColumn.setCellValueFactory(
		  new PropertyValueFactory<Grocery, Integer>("QTY"));
		 sectionColumn.setCellValueFactory(
		  new PropertyValueFactory<Grocery, String>("Section"));
		 daysAgoColumn.setCellValueFactory(
		  new PropertyValueFactory<Grocery, String>("Bought"));

		// Create the table view and add table columns to it
		TableView<Grocery> tableView = new TableView<Grocery>();

		//COMPLETED
		/* TODO 2-05 - TO COMPLETE ****************************************
		 * add table columns to the table view create above
		 */
		 tableView.getColumns().add(idColumn);
		 tableView.getColumns().add(itemNameColumn);
		 tableView.getColumns().add(quantityColumn);
		 tableView.getColumns().add(sectionColumn);
		 tableView.getColumns().add(daysAgoColumn);

		//	Attach table data to the table view
		tableView.setItems(tableData);

		//COMPLETED
		/* TODO 2-06 - TO COMPLETE ****************************************
		 * set minimum and maximum width to the table view and each columns
		 */

		 idColumn.setMinWidth(50);
		 itemNameColumn.setMinWidth(150);
		 quantityColumn.setMinWidth(50);
		 sectionColumn.setMinWidth(150);
		 daysAgoColumn.setMinWidth(50);
		 tableView.setMinWidth(600);
		 tableView.setMaxWidth(800);

		/* TODO 2-07 - TO COMPLETE ****************************************
		 * call data source controller get all groceries method to add
		 * all groceries to table data observable list
		 */

		// tableView.add(fridgeDSC.getAllGroceries());


		// =====================================================
		// ADD the remaining UI elements
		// NOTE: the order of the following TODO items can be
		// 		 changed to satisfy your UI implementation goals
		// =====================================================


		/* TODO 2-08 - TO COMPLETE ****************************************
		 * filter container - part 1
		 * add all filter related UI elements you identified
		 */

		 //Text field - allows the user to filter the TableView data
		 TextField filterByTF = new TextField(" ");

		 //ChoiceBox label
		 Label filterByLB = new Label ("Filter by: ");

		 ChoiceBox<FILTER_COLUMNS> choices = new ChoiceBox<FILTER_COLUMNS>();
		 choices.getItems().addAll(FILTER_COLUMNS.values());

		 //Set default value to ITEM
		 choices.setValue(FILTER_COLUMNS.ITEM);

		 //Show expire only CheckBox
		 CheckBox showExpireOnlyCB = new CheckBox("Show Expire Only");

		/* TODO 2-09 - TO COMPLETE ****************************************
		 * filter container - part 2:
		 * - addListener to the "Filter By" ChoiceBox to clear the filter
		 *   text field vlaue and to enable the "Show Expire Only" CheckBox
		 *   if "BOUGHT_DAYS_AGO" is selected
		 */



		/* TODO 2-10 - TO COMPLETE ****************************************
		 * filter container - part 2:
		 * - addListener to the "Filter By" ChoiceBox to clear and set focus
		 *   to the filter text field and to enable the "Show Expire Only"
		 *   CheckBox if "BOUGHT_DAYS_AGO" is selected
		 *
		 * - setOnAction on the "Show Expire Only" Checkbox to clear and
		 *   set focus to the filter text field
		 */

		/* TODO 2-11 - TO COMPLETE ****************************************
		 * filter container - part 3:
		 * - create a filtered list
		 * - create a sorted list from the filtered list
		 * - bind comparators of sorted list with that of table view
		 * - set items of table view to be sorted list
		 * - set a change listener to text field to set the filter predicate
		 *   of filtered list
		 */


		/* TODO 2-12 - TO COMPLETE ****************************************
		 * ACTION buttons: ADD, UPDATE ONE, DELETE, EXIT
		 * - ADD button sets the add UI elements to visible;
		 *   NOTE: the add input controls and container may have to be
		 * 		   defined before these action controls & container(s)
		 * - UPDATE ONE and DELETE buttons action need to check if a
		 *   table view row has been selected first before doing their
		 *   action; hint: should you also use an Alert confirmation?
         * - EXIT button. Use stage.close() after making sure that data is synced
		 */

		/* TODO 2-13 - TO COMPLETE ****************************************
		 * add input controls and container(s)
		 * - Item will list item data from the data source controller list
		 *   all items method
		 * - Section will list all sections defined in the data source
		 *   controller SECTION enum
		 * - Quantity: a texf field, self descriptive
		 * - CANCEL button: clears all input controls
		 * - SAVE button: sends the new grocery information to the data source
		 *   controller add grocery method; be mindful of exceptions when any
		 *   or all of the input controls are empty upon SAVE button click
		 */

		// =====================================================================
		// SET UP the Stage
		// =====================================================================
		//

		//My containers
		HBox filterHBox = new HBox(filterByTF, filterByLB, choices, showExpireOnlyCB);

		/* TODO 2-14 - TO COMPLETE ****************************************
         * - Create primary VBox container, add it to the scene add external style sheet to the scene
         *   - VBox root = new VBox(...);
		 *   - add all your containers, controls to the root VBox
         *   - add root container to the scene
         */
        VBox root = new VBox(filterHBox); // modify
		Scene scene = new Scene(root);
        /*
         *   - add external style sheet to the scene - scene.getStylesheets().add(..);
         *   - add scene to stage
		 */

        stage.setScene(scene);
	}

	public void stop() throws Exception {

		/* TODO 2-15 - TO COMPLETE ****************************************
		 * call the data source controller database disconnect method
		 * NOTE: that database disconnect method throws exception
		 */
	}
}
