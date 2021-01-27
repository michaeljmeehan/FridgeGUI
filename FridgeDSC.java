import java.sql.*;
import java.util.*;
import java.io.File;
import java.io.PrintWriter;
import java.util.Scanner;
import java.time.LocalDate;
import java.time.Duration;
import java.time.format.DateTimeFormatter;


public class FridgeDSC {

    // the date format we will be using across the application
    public static final String DATE_FORMAT = "dd/MM/yyyy";

    /*
        FREEZER, // freezing cold
        MEAT, // MEAT cold
        COOLING, // general fridge area
        CRISPER // veg and fruits section

        note: Enums are implicitly public static final
    */
    public enum SECTION {
        FREEZER,
        MEAT,
        COOLING,
        CRISPER
    };

    private static Connection connection;
    private static Statement statement;
    private static PreparedStatement preparedStatement;

    /* TODO 1-01 - COMPLETED ****************************************
     * set the value of the string for the following 3 lines:
     * - url
     * - user
     * - password
     *
     * For a local mysql installation use something like
     *
     * String url = "jdbc:mysql://localhost:3306/fridgedb";
     * String user = "root";
     * String password = "1234";
     */

    public static void connect() throws SQLException {
        try {
            Class.forName("com.mysql.jdbc.Driver"); // You may need to comment out this line

             String url = "jdbc:mysql://localhost:3306/fridgedb";
             String user = "root";
             String password = "0111";

            connection = DriverManager.getConnection(url, user, password);
            statement = connection.createStatement();
        } catch(Exception e) {
            System.out.println(e);
            e.printStackTrace();
        }
    }

    public static void disconnect() throws SQLException {
        if(preparedStatement != null) preparedStatement.close();
        if(statement != null) statement.close();
        if(connection != null) connection.close();
    }

    /*
    TODO 1-02 - COMPLETED ****************************************
     * - preparedStatement to add argument name to the queryString
     * - resultSet to execute the preparedStatement query
     * - iterate through the resultSet result
     */

     /*
     TODO 1-03 - COMPLETED ****************************************
      * - if resultSet has result, get data and create an Item instance
      */

    public Item searchItem(String name) throws Exception
    {
         connect();

         String queryString = "SELECT * FROM item WHERE name = ?";
         preparedStatement = connection.prepareStatement(queryString);
         preparedStatement.setString(1, name);
         ResultSet rs = preparedStatement.executeQuery();

         Item item = null;
         if (rs.next())
         { // i.e. the item exists
           boolean expires = rs.getBoolean(2);
           item = new Item(name, expires);
         }
         disconnect();
         return item;
    }

    /* TODO 1-04 - COMPLETED ****************************************
     * - preparedStatement to add argument name to the queryString
     * - resultSet to execute the preparedStatement query
     * - iterate through the resultSet result
     */

     /* TODO 1-05 - COMPLETED ****************************************
      * - if resultSet has result, get data and create a Grocery instance
      * - making sure that the item name from grocery exists in
      *   item table (use searchItem method)
      * - pay attention about parsing the date string to LocalDate
      */

    public Grocery searchGrocery(int id) throws Exception
    {
      connect();

      DateTimeFormatter dtf = DateTimeFormatter.ofPattern(DATE_FORMAT);
      String queryString = "SELECT * FROM grocery WHERE id = ?";

      preparedStatement = connection.prepareStatement(queryString);
      preparedStatement.setInt(1, id);
      ResultSet rs = preparedStatement.executeQuery();

      Grocery grocery = null;
      if (rs.next())
        { // i.e. the grocery exists
          Item item = searchItem(rs.getString(2)); //you should check if the item == null and only continue if the item exists
          //convert the date to the right format
          LocalDate date = LocalDate.parse(rs.getString(3), dtf);
          //get the quantity
          int quantity = rs.getInt(4);
          //convert the string to a section
          FridgeDSC.SECTION section = SECTION.valueOf(rs.getString(5));
          grocery = new Grocery(id, item, date, quantity, section);
        }
        disconnect();
        return grocery;
    }

    /* TODO 1-06 - COMPLETED ****************************************
     * - resultSet to execute the statement query
     */

     /* TODO 1-07 - COMPLETED ****************************************
      * - iterate through the resultSet result, create intance of Item
      *   and add to list items
      */

    public List<Item> getAllItems() throws Exception
    {
      connect();

      String queryString = "SELECT * FROM item";
      ResultSet rs = statement.executeQuery(queryString);

      List<Item> items = new ArrayList<Item>();

      while(rs.next())
      {
        String name = rs.getString(1);
        boolean expires = rs.getBoolean(2);

        items.add(new Item(name, expires));
      }

      disconnect();
      return items;
    }

    /* TODO 1-08 - COMPLETED ****************************************
     * - resultSet to execute the statement query
     */

     /* TODO 1-09 - SEMI- COMPLETED****************************************
      * - iterate through the resultSet result, create intance of Item
      *   and add to list items
      * - making sure that the item name from each grocery exists in
      *   item table (use searchItem method)
      * - pay attention about parsing the date string to LocalDate
      */

    public List<Grocery> getAllGroceries() throws Exception
      {
        connect();

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern(DATE_FORMAT);
        String queryString = "SELECT * FROM grocery";
        ResultSet rs = statement.executeQuery(queryString);

        List<Grocery> groceries = new ArrayList<Grocery>();

    try{
        while(rs.next())
        {
          int id = rs.getInt(1);
          String itemName = rs.getString(2);
          Item item = searchItem(itemName);
          if(item == null)
          {
            System.err.println("Error - Item: " + itemName + " does not exist");
            continue;
          }
          LocalDate date = LocalDate.parse(rs.getString(3), dtf);
          int quantity = rs.getInt(4);
          SECTION section = SECTION.valueOf(rs.getString(5));

          groceries.add(new Grocery(id , item, date, quantity, section));
        }
      } catch(Exception e) {
          //System.out.println(e);
          //e.printStackTrace();
      }
        disconnect();
        return groceries;
      }

/* TODO 1-10 - COMPLETED ****************************************
 * - preparedStatement to add arguments to the queryString
 * - resultSet to executeUpdate the preparedStatement query
 */

 // NOTE: should we check if itemName (argument name) exists in item table?
 //      --> adding a groceries with a non-existing item name should through an exception

    public int addGrocery(String name, int quantity, SECTION section) throws Exception
      {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern(DATE_FORMAT);
        LocalDate date = LocalDate.now();
        String dateStr = date.format(dtf);

        //Pre: check if ItemName exists in item table
        Item item = searchItem(name);
        boolean pre = (item != null);

        if(!pre)
        {
          String msg = "Item name: " + item.getName() + " is not new!";
          System.out.println("\n ERROR: " + msg);
          throw new Exception(msg);

          // Throwing exception terminates this method here, and
          // returning to the calling method
        }
        //Post: add grocery; given all pre-conditions are satisfied
        connect();

        //Set the ID to 1 - autoincrement
        //preparedStatement.setInt(1, 0);
        String command = "INSERT INTO grocery (itemName, date, quantity, section) VALUES( ?, ?, ?, ?)";
        preparedStatement = connection.prepareStatement(command);
        preparedStatement.setString(1, name);
        preparedStatement.setString(2, dateStr);
        preparedStatement.setInt(3, quantity);
        preparedStatement.setString(4, section.toString());
        preparedStatement.executeUpdate();

        // retrieving & returning last inserted record id
        ResultSet rs = statement.executeQuery("SELECT LAST_INSERT_ID()");
        rs.next();
        int newId = rs.getInt(1);

        return newId;
    }

      /* TODO 1-11 - TO COMPLETE ****************************************
       * - search grocery by id
       * - check if has quantity is greater one; if not throw exception
       *   with adequate error message
       */

       /* TODO 1-12 - TO COMPLETE ****************************************
        * - statement execute update on queryString
        * - should the update affect a row search grocery by id and
        *   return it; else throw exception with adequate error message
        *
        * NOTE: method should return instance of grocery
        */

    public Grocery useGrocery(int id) throws Exception
    {
      //Pre: id should exist in the database
      Grocery g = searchGrocery(id);

      if (g == null)
      {
        return null;
      }

      //Check if has quantity < 1
      if (g.getQuantity() <= 1){
        throw new Exception("Error: There is 1 or less of item: " + g.getItemName());
      }

      String queryString =
          "UPDATE grocery " +
          "SET quantity = quantity - 1 " +
          "WHERE quantity > 1 " +
          "AND id = " + id + ";";

      if(statement.executeUpdate(queryString) > 0){
        return searchGrocery(id);
      } else {
        throw new Exception("ERROR: Row not updated");
        //return null;
      }
    }

    public int removeGrocery(int id) throws Exception {
        String queryString = "DELETE FROM grocery WHERE id = " + id + ";";

        /* TODO 1-13 - TO COMPLETE ****************************************
         * - search grocery by id
         * - if grocery exists, statement execute update on queryString
         *   return the value value of that statement execute update
         * - if grocery does not exist, throw exception with adequate
         *   error message
         *
         * NOTE: method should return int: the return value of a
         *       stetement.executeUpdate(...) on a DELETE query
         */

         preparedStatement = connection.prepareStatement(queryString);
         preparedStatement.setInt(1, id);
         ResultSet rs = preparedStatement.executeQuery();

         connect();

         boolean pre = rs.next();
         if(!pre)
         {
           //If no results throw ERROR
           throw new RuntimeException("The grocery ID:" + id + " does not exist");
         }

         disconnect();
         return statement.executeUpdate(queryString);

    }


    // STATIC HELPERS -------------------------------------------------------

    public static long calcDaysAgo(LocalDate date) {
        return Math.abs(Duration.between(LocalDate.now().atStartOfDay(), date.atStartOfDay()).toDays());
    }

    public static String calcDaysAgoStr(LocalDate date) {
        String formattedDaysAgo;
        long diff = calcDaysAgo(date);

        if (diff == 0)
            formattedDaysAgo = "today";
        else if (diff == 1)
            formattedDaysAgo = "yesterday";
        else formattedDaysAgo = diff + " days ago";

        return formattedDaysAgo;
    }

    // To perform some quick tests
    public static void main(String[] args) throws Exception {
        FridgeDSC myFridgeDSC = new FridgeDSC();
        myFridgeDSC.connect();

        System.out.println("\nSYSTEM:\n");
        System.out.println();

        System.out.println("\n\nshowing all of each:");
        System.out.println("\n All items: ");
        System.out.println(myFridgeDSC.getAllItems());
        System.out.println("\n");
        System.out.println("\n All groceries: ");
        System.out.println(myFridgeDSC.getAllGroceries());

        int addedId = myFridgeDSC.addGrocery("Milk", 40, SECTION.COOLING);
        System.out.println("added: " + addedId);
        System.out.println("deleting " + (addedId - 1) + ": " + (myFridgeDSC.removeGrocery(addedId - 1) > 0 ? "DONE" : "FAILED"));
        System.out.println("using " + (addedId) + ": " + myFridgeDSC.useGrocery(addedId));
        System.out.println(myFridgeDSC.searchGrocery(addedId));

        myFridgeDSC.disconnect();
    }
}
